package com.example.mobphotoedit

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.renderscript.*
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.experience.AffineTrans
import kotlinx.android.synthetic.main.activity_filtering.*
import kotlinx.android.synthetic.main.activity_filtering.no
import kotlinx.android.synthetic.main.activity_filtering.photo
import kotlinx.android.synthetic.main.activity_filtering.yes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt


class FilteringActivity : AppCompatActivity() {

    var imageBitmap : Bitmap? = null
    var btmp: Bitmap? = null

    private val StartPoints = mutableListOf<Points>()
    private val FinishPoints = mutableListOf<Points>()
    var flag : Boolean = false
    private var countSt = 0
    private var countFin = 0
    private var paints = arrayOf(Paint(), Paint(), Paint())
    private var pathSt = arrayOf(Path(), Path(), Path())
    private var pathFin= arrayOf(Path(), Path(), Path())
    var ind = -1
  
    private var imageUriUri: Uri? = null
    private var isChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering)

        pBarFil.visibility = View.GONE
        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        photo.setImageURI(imageUri)
        imageBitmap = (photo.getDrawable() as BitmapDrawable).bitmap
        //workImageBitmap = imageBitmap!!.copy(imageBitmap!!.config, true)
        imageUriUri = imageUri
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

        startpoints.setOnClickListener {
            (Filtering as MySurfaceView).flag = false
            (Filtering as MySurfaceView).invalidate()
        }

        finishpoints.setOnClickListener {
            (Filtering as MySurfaceView).flag = true
            (Filtering as MySurfaceView).invalidate()
        }

        filter.setOnClickListener {
            countSt = (Filtering as MySurfaceView).getcountSt()
            countFin = (Filtering as MySurfaceView).getcountFin()

            if (countSt == 3 && countFin == 3)
            {
                pBarFil.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Default).async {
                    filterFun(this@FilteringActivity)
                    launch(Dispatchers.Main) {
                        photo.setImageBitmap(btmp)
                        pBarFil.visibility = View.GONE
                    }
                }
                isChanged = true
            }
            else {
                Toast.makeText(this, "Set 3 start and 3 finish points", Toast.LENGTH_LONG).show()
            }
            /*(Filtering as MySurfaceView).removePoints()
            countSt = 0
            countFin = 0
            StartPoints.clear()
            FinishPoints.clear()*/
        }
    }

    class MySurfaceView (context: Context, attrs: AttributeSet? = null) : View(context, attrs){
        private val StartPoints = mutableListOf<Points>()
        private val FinishPoints = mutableListOf<Points>()
        var flag : Boolean = false
        private var paints = arrayOf(Paint(), Paint(), Paint())
        private var pathSt = arrayOf(Path(), Path(), Path())
        private var pathFin= arrayOf(Path(), Path(), Path())
        private var pw = 0f
        private var ph = 0f
        private var countSt = 0
        private var countFin = 0
        var ind = -1

        fun getcountSt(): Int {
            return countSt
        }

        fun getcountFin(): Int {
            return countFin
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            pw = (width - paddingLeft - paddingRight).toFloat()
            ph = (height - paddingTop - paddingEnd).toFloat()
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                ind = searchPoint(event.x, event.y)
            }
            if(action == MotionEvent.ACTION_MOVE){
                if(ind != -1){
                    if(flag){
                        FinishPoints[ind].x = event.x
                        FinishPoints[ind].y = event.y
                        for(i in 0..2){
                            pathFin[i].reset()
                        }
                        for(i in 0 until FinishPoints.size) {
                            pathFin[i].addCircle(
                                FinishPoints[i].x,
                                FinishPoints[i].y,
                                30F,
                                Path.Direction.CW
                            )
                        }
                    }
                    else{
                        StartPoints[ind].x = event.x
                        StartPoints[ind].y = event.y
                        for(i in 0..2){
                            pathSt[i].reset()
                        }
                        for(i in 0 until StartPoints.size) {
                            pathSt[i].addCircle(
                                StartPoints[i].x,
                                StartPoints[i].y,
                                30F,
                                Path.Direction.CW
                            )
                        }
                    }
                }
            }
            if (action == MotionEvent.ACTION_UP) {
                if(ind == -1){
                    when(flag){
                        false -> {
                            if(countSt < 3){
                                StartPoints.add(Points(event.x, event.y))
                                pathSt[countSt].addCircle(event.x, event.y, 30F, Path.Direction.CW)
                                countSt++
                            }
                        }
                        true -> {
                            if(countFin < 3){
                                FinishPoints.add(Points(event.x, event.y))
                                pathFin[countFin].addCircle(event.x, event.y, 30F, Path.Direction.CW)
                                countFin++
                            }
                        }
                    }
                }
                else{
                    ind = -1
                }
            }
            invalidate()
            return true
        }

        private fun searchPoint(x : Float, y : Float): Int {
            if(flag){
                for(i in 0 until FinishPoints.size){
                    if(abs(x - FinishPoints[i].x) < 50 && abs(y - FinishPoints[i].y) < 50) {
                        return i
                    }
                }
            }
            else{
                for(i in 0 until StartPoints.size){
                    if(abs(x - StartPoints[i].x) < 50 && abs(y - StartPoints[i].y) < 50) {
                        return i
                    }
                }
            }
            return -1
        }

        override fun onDraw(canvas: Canvas) {
            if(flag) {
                for(i in 0 until FinishPoints.size){
                    canvas.drawPath(pathFin[i], paints[i])
                }
            }
            else{
                for(i in 0 until StartPoints.size){
                    canvas.drawPath(pathSt[i], paints[i])
                }
            }
        }

        /*fun removePoints(){
            for (i in 0..2){
                pathSt[i].reset()
                pathFin[i].reset()
            }
            invalidate()
        }*/

        fun getStartPoint(): MutableList<Points> {
            return StartPoints
        }

        fun getFinishPoint(): MutableList<Points> {
            return FinishPoints
        }

        init {
            paints[0].color = Color.RED
            paints[1].color = Color.BLUE
            paints[2].color = Color.GREEN
        }
    }

    private fun ScalarProduct(A: Points, B: Points, C: Points): Float {
        val ABx: Float = B.x - A.x
        val ABy: Float = B.y - A.y
        val ACx: Float = C.x - A.x
        val ACy: Float = C.y - A.y
        return ABx * ACx + ABy * ACy
    }

    private fun filterFun(context: Context): Bitmap? {
        var StartPoints = mutableListOf<Points>()
        var FinishPoints = mutableListOf<Points>()
        StartPoints = (Filtering as MySurfaceView).getStartPoint()
        FinishPoints = (Filtering as MySurfaceView).getFinishPoint()
        val solver: AffineTrans
        solver = AffineTrans(
            StartPoints[0], StartPoints[1], StartPoints[2], FinishPoints[0], FinishPoints[1], FinishPoints[2]
        )
        if (!solver.prepare()) {
            return imageBitmap
        }
        btmp = imageBitmap!!.copy(
            Bitmap.Config.ARGB_8888, true
        )
        btmp!!.eraseColor(Color.WHITE)
        var cnt = 0
        for (i in 0 until imageBitmap!!.width) {
            for (j in 0 until imageBitmap!!.height) {
                val image: Points? = solver.calc(i, j)
                val w = (image?.x!!).roundToInt().toInt()
                val h = (image.y).roundToInt().toInt()
                if (0 > w || w >= btmp!!.width) continue
                if (0 > h || h >= btmp!!.height) continue
                btmp!!.setPixel(i, j, imageBitmap!!.getPixel(w, h))
                if (w != i && h != j) cnt++
            }
        }
        var a1: Points? = solver.calc(0, 0)
        var a2: Points? = solver.calc(imageBitmap!!.width, 0)
        val w = sqrt(((a1?.x!! - a2?.x!!) * (a1.x - a2.x) + (a1.y - a2.y) * (a1.y - a2.y)).toDouble()).toInt()
        a1 = solver.calc(0, 0)
        a2 = solver.calc(0, imageBitmap!!.height)
        val h = sqrt(((a1?.x!! - a2?.x!!) * (a1?.x!! - a2?.x!!) + (a1?.y!! - a2?.y!!) * (a1.y - a2.y)).toDouble()).toInt()
        return if (ScalarProduct(StartPoints[0], StartPoints[1], StartPoints[2]) < ScalarProduct(FinishPoints[0], FinishPoints[1], FinishPoints[2])) {
            resizeBilinear(
                btmp!!.copy(
                    Bitmap.Config.ARGB_8888,
                    true
                ), imageBitmap!!.width,
                imageBitmap!!.height, w, h
            )
        } else {
            resizeBitmap2(
                btmp!!.copy(Bitmap.Config.ARGB_8888, true), w,
                context
            )
        }
    }

    private fun switchActivity(imageUri: Uri){
        val i = Intent()
        i.putExtra("newImageUri", imageUri.toString())
        setResult(Activity.RESULT_OK, i)
        finish()
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            quitDialog()
        }
        return super.onKeyDown(keyCode, event)
    }

    fun resizeBilinear(pixels: Bitmap, w: Int, h: Int, w2: Int, h2: Int): Bitmap? {
        val temp = Bitmap.createBitmap(w2, h2, Bitmap.Config.ARGB_8888)
        var a: Int
        var b: Int
        var c: Int
        var d: Int
        var x: Int
        var y: Int
        val x_ratio = (w - 1).toFloat() / w2
        val y_ratio = (h - 1).toFloat() / h2
        var x_diff: Float
        var y_diff: Float
        var blue: Float
        var red: Float
        var green: Float
        for (i in 0 until h2) {
            for (j in 0 until w2) {
                x = (x_ratio * j).toInt()
                y = (y_ratio * i).toInt()
                x_diff = x_ratio * j - x
                y_diff = y_ratio * i - y
                try {
                    a = pixels.getPixel(x, y)
                    b = pixels.getPixel(x + 1, y)
                    c = pixels.getPixel(x, y + 1)
                    d = pixels.getPixel(x + 1, y + 1)
                } catch (e: ArrayIndexOutOfBoundsException) {
                    a = pixels.getPixel(x, y)
                    b = pixels.getPixel(x, y)
                    c = pixels.getPixel(x, y)
                    d = pixels.getPixel(x, y)
                }

                // blue element
                // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                blue =
                    (a and 0xff) * (1 - x_diff) * (1 - y_diff) + (b and 0xff) * x_diff * (1 - y_diff) + (c and 0xff) * y_diff * (1 - x_diff) + (d and 0xff) * (x_diff * y_diff)

                // green element
                // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
                green =
                    (a shr 8 and 0xff) * (1 - x_diff) * (1 - y_diff) + (b shr 8 and 0xff) * x_diff * (1 - y_diff) + (c shr 8 and 0xff) * y_diff * (1 - x_diff) + (d shr 8 and 0xff) * (x_diff * y_diff)

                // red element
                // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
                red =
                    (a shr 16 and 0xff) * (1 - x_diff) * (1 - y_diff) + (b shr 16 and 0xff) * x_diff * (1 - y_diff) + (c shr 16 and 0xff) * y_diff * (1 - x_diff) + (d shr 16 and 0xff) * (x_diff * y_diff)
                temp.setPixel(
                    j, i, -0x1000000 or
                            (red.toInt() shl 16 and 0xff0000) or
                            (green.toInt() shl 8 and 0xff00) or
                            blue.toInt()
                )
            }
        }
        StartPoints.clear()
        FinishPoints.clear()
        return temp
    }

}