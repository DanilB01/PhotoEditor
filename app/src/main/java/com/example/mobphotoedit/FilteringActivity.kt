package com.example.mobphotoedit

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_filtering.*
import kotlin.math.abs

class FilteringActivity : AppCompatActivity() {

    private var imageUriUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering)

        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        imageUriUri = imageUri
        yes.setOnClickListener {
            switchActivity(imageUri)
        }
        no.setOnClickListener {
            quitDialog()
        }

        startpoints.setOnClickListener{
            (Filtering as MySurfaceView).flag = false
            (Filtering as MySurfaceView).invalidate()
        }

        finishpoints.setOnClickListener{
            (Filtering as MySurfaceView).flag = true
            (Filtering as MySurfaceView).invalidate()
        }

        filter.setOnClickListener{
            //(Filtering as MySurfaceView).filterFun
        }
    }

    class MySurfaceView (context: Context, attrs: AttributeSet? = null) : View(context, attrs){
        private val StartPoints = mutableListOf<Points>() // массив точек
        private val FinishPoints = mutableListOf<Points>() // массив точек
        var flag : Boolean = false
        private var countSt = 0
        private var countFin = 0
        private var paints = arrayOf(Paint(), Paint(), Paint())
        private var pathSt = arrayOf(Path(), Path(), Path())
        private var pathFin= arrayOf(Path(), Path(), Path())
        private var pw = 0f
        private var ph = 0f
        var ind = -1

        fun removePoints(){
            for (i in 0..2){
                pathSt[i].reset()
                pathFin[i].reset()
            }
            invalidate()
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

        private fun ScalarProduct(A: Points, B: Points, C: Points): Float {
            val ABx: Float = B.x - A.x
            val ABy: Float = B.y - A.y
            val ACx: Float = C.x - A.x
            val ACy: Float = C.y - A.y
            return ABx * ACx + ABy * ACy
        }

        //fun filterFun(): Bitmap? {
        //}



        /*private fun algorithm(): Bitmap? {
            val solver: AffineSystemeCoord
            solver = AffineSystemeCoord(
                p11, p12, p13, p21, p22, p23
            )
            if (!solver.prepare()) {
                Toast.makeText(
                    mainActivity.getApplicationContext(),
                    mainActivity.getResources().getString(R.string.points_on_one_line),
                    Toast.LENGTH_SHORT
                ).show()
                return mainActivity.getBitmap()
            }
            val btmp: Bitmap = mainActivity.getBitmap().copy(
                Bitmap.Config.ARGB_8888, true
            )
            btmp.eraseColor(Color.WHITE)
            var cnt = 0
            for (i in 0 until mainActivity.getBitmap().getWidth()) {
                for (j in 0 until mainActivity.getBitmap().getHeight()) {
                    val image: Points = solver.calc(i, j)
                    val w = Math.round(image.x).toInt()
                    val h = Math.round(image.y).toInt()
                    if (0 > w || w >= btmp.width) continue
                    if (0 > h || h >= btmp.height) continue
                    btmp.setPixel(i, j, mainActivity.getBitmap().getPixel(w, h))
                    if (w != i && h != j) cnt++
                }
            }
            var a1: Points = solver.calc(0.0, 0.0)
            var a2: Points = solver.calc(mainActivity.getBitmap().getWidth(), 0.0)
            val w =
                Math.sqrt((a1.x - a2.x) * (a1.x - a2.x) + (a1.y - a2.y) * (a1.y - a2.y)).toInt()
            a1 = solver.calc(0, 0)
            a2 = solver.calc(0, mainActivity.getBitmap().getHeight())
            val h =
                Math.sqrt((a1.x - a2.x) * (a1.x - a2.x) + (a1.y - a2.y) * (a1.y - a2.y)).toInt()
            return if (ScalarProduct(p11, p12, p13) < ScalarProduct(p21, p22, p23)) {
                ColorFIltersCollection.resizeBilinear(
                    btmp.copy(
                        Bitmap.Config.ARGB_8888,
                        true
                    ), mainActivity.getBitmap().getWidth(),
                    mainActivity.getBitmap().getHeight(), w, h
                )
            } else {
                ColorFIltersCollection.resizeBicubic(
                    btmp.copy(Bitmap.Config.ARGB_8888, true), w,
                    mainActivity.getApplicationContext()
                )
            }
        }*/

        init {
            paints[0].color = Color.RED
            paints[1].color = Color.BLUE
            paints[2].color = Color.GREEN
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
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}