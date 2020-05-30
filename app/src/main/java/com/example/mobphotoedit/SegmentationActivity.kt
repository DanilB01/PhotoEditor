package com.example.mobphotoedit

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_segmentation.*
import kotlinx.android.synthetic.main.activity_segmentation.photo
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class SegmentationActivity : AppCompatActivity() {
    var imageBitmap: Bitmap? = null
    var src: Mat? = null
    var orig: Mat? = null
    var cascadeClassifier : CascadeClassifier? = null
    private var absoluteFaceSize : Double? = null
    private var imageUriUri: Uri? = null
    private var isChanged = false

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                LoaderCallbackInterface.SUCCESS -> {
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segmentation)

        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        photo.setImageURI(imageUri)
        imageUriUri = imageUri

        if(OpenCVLoader.initDebug()){
            Toast.makeText(this, "openCv successfully loaded", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "openCv cannot be loaded", Toast.LENGTH_SHORT).show();
        }
        find.setOnClickListener {
            isChanged = true
            val `is`: InputStream = resources.openRawResource(R.raw.haarcascade_frontalface_alt2)
            val cascadeDir: File = getDir("cascade", Context.MODE_PRIVATE)
            val mCascadeFile = File(cascadeDir, "haarcascade_frontalface_alt2.xml")
            val os = FileOutputStream(mCascadeFile)

            val buffer = ByteArray(4096)
            var bytesRead: Int? = null
            while (`is`.read(buffer).also({ bytesRead = it }) != -1) {
                bytesRead?.let { it1 -> os.write(buffer, 0, it1) }
            }
            `is`.close()
            os.close()

            cascadeClassifier = CascadeClassifier(mCascadeFile.absolutePath)

            imageBitmap = (photo.drawable as BitmapDrawable).bitmap

            absoluteFaceSize = imageBitmap!!.height * 0.05

            src = Mat(imageBitmap!!.height, imageBitmap!!.width, CvType.CV_8UC4)
            orig = Mat(imageBitmap!!.height, imageBitmap!!.width, CvType.CV_8UC4)
            Utils.bitmapToMat(imageBitmap, src)
            Utils.bitmapToMat(imageBitmap, orig)

            Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2GRAY)

            var faces = MatOfRect()

            cascadeClassifier?.detectMultiScale(
                src, faces, 1.1, 2, 2,
                Size(absoluteFaceSize!!, absoluteFaceSize!!), Size()
            )

            val facesArray = faces.toArray()
            for (i in facesArray.indices) Imgproc.rectangle(
                orig,
                facesArray[i].tl(),
                facesArray[i].br(),
                Scalar(0.0, 255.0, 0.0, 255.0),
                3
            )
            val bool = facesArray.isEmpty()
            Imgproc.circle(orig, Point(10.0, 50.0), 50, Scalar(255.0, 0.0, 0.0))
            Utils.matToBitmap(orig, imageBitmap)
            photo.setImageBitmap(imageBitmap)

            if(bool)
                Toast.makeText(this, "Sorry, no faces found", Toast.LENGTH_SHORT).show()
        }

        yes.setOnClickListener {
            var newUri = saveImageToInternalStorage(photo,this)
            bitmapStore.addBitmap(imageView2Bitmap(photo))
            switchActivity(newUri)
        }
        no.setOnClickListener {
            if(isChanged)
                quitDialog()
            else
                switchActivity(imageUri)
        }

    }

    override fun onBackPressed() {
        quitDialog()
    }

    private fun quitDialog() {
        val quitDialog = AlertDialog.Builder(this)
        quitDialog.setTitle(resources.getString(R.string.leave))
        quitDialog.setPositiveButton(resources.getString(R.string.yes)) {
                dialog, which -> switchActivity(imageUriUri!!)
        }
        quitDialog.setNegativeButton(resources.getString(R.string.no)){
                dialog, which ->

        }
        quitDialog.show()
    }

    public override fun onResume() {
        super.onResume()
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback)
    }

    private fun switchActivity(imageUri: Uri){
        val i = Intent()
        i.putExtra("newImageUri", imageUri.toString())
        setResult(Activity.RESULT_OK, i)
        finish()
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isChanged)
                quitDialog()
            else
                finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}
