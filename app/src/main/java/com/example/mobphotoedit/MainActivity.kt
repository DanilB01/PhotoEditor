package com.example.mobphotoedit

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

   companion object {
       private val IMAGE_CAPTURE_CODE = 1002
       private val IMAGE_PICK_CODE = 1001
       private val PERMISSION_CODE = 1000
       private val RETURN_CODE = 2000
   }
    var imageUri: Uri? = null
    var flagCamera: Boolean = false
    var flagGallery: Boolean = false
    var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        yes.visibility = View.GONE
        no.visibility = View.GONE

        yes.setOnClickListener {
            val intent = Intent(this, DesktopActivity::class.java)
            intent.putExtra("ImageUri", imageUri.toString())
            intent.putExtra("ImagePath", imagePath)
            startActivityForResult(intent, RETURN_CODE)
        }

        no.setOnClickListener {
            setDefaultInterface()
        }

        takePhotoButton.setOnClickListener{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    //разрешение не дано
                    flagCamera = true
                    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    //запрос разрешения
                    requestPermissions(permission, PERMISSION_CODE)
                }
                else{
                    //разрешение дано
                    openCamera()
                }
            }
            else{
                //system OS < Marshmallow
                openCamera()
            }
        }

        fromGalleryButton.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    //разрешение не дано
                    flagGallery = true
                    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    //запрос разрешения
                    requestPermissions(permission, PERMISSION_CODE)
                }
                else{
                    //разрешение дано
                    pickPhotoFromGallery()
                }
            }
            else{
                //system OS < Marshmallow
                pickPhotoFromGallery()
            }
        }
    }

    fun setDefaultInterface(){
        imageUri = null
        pic.setImageResource(R.drawable.ic_image_black_24dp)
        yes.visibility = View.GONE
        no.visibility = View.GONE
        message.text = getString(R.string.add)
    }

    override fun onStart() {
        super.onStart()
        stars.onStart()
    }

    override fun onStop() {
        stars.onStop()
        super.onStop()
        setDefaultInterface()
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        imagePath = imageUri?.path

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    private fun pickPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            PERMISSION_CODE->{
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //разрешение получено
                    if(flagCamera){
                        openCamera()
                    }
                    else if(flagGallery){
                        pickPhotoFromGallery()
                    }
                }
                else{
                    //разрешение не получено
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //вызывается когда изображение с камеры получено
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                IMAGE_PICK_CODE -> {
                    imageUri = data?.data
                    imagePath = data?.data?.path
                    pic.setImageURI(imageUri)
                    yes.visibility = View.VISIBLE
                    no.visibility = View.VISIBLE
                    message.text = getString(R.string.cont)
                }

                RETURN_CODE-> {
                    setDefaultInterface()
                }
            }
        }
    }




}



