package com.example.mobphotoedit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_line_drawing.view.*


class LineDrawingActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_line_drawing)

    }

    class MySurfaceView(context: Context?, attrs: AttributeSet? = null) : View(context, attrs) {
        private val kx = mutableListOf<Float>()
        private val ky = mutableListOf<Float>()
        private var count : Int? = null
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val paint1 = Paint(Paint.ANTI_ALIAS_FLAG)
        private var flag : Boolean = false
        private var flag1 : Boolean = false
        private var path: Path?
        private var path1: Path?
        override fun onTouchEvent(event: MotionEvent): Boolean {
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                if(!flag){
                    kx.add(event.x) // x0
                    ky.add(event.y) // y0
                    count = 0
                    path1?.addCircle(event.x, event.y, 10F, Path.Direction.CW)
                    path?.moveTo(event.x, event.y)
                }
            }
            if (action == MotionEvent.ACTION_UP) {
                if(!flag) {
                    flag = true
                } else {
                    kx.add(event.x) // xi
                    ky.add(event.y) // yi
                    count = count?.plus(1)
                    if(count == 1){
                        path?.moveTo(kx[0], ky[0])
                        path?.lineTo(event.x, event.y)
                        path1?.addCircle(event.x, event.y, 10F, Path.Direction.CW)
                        path?.moveTo(event.x, event.y)
                    }
                    else {
                        path1?.addCircle(event.x, event.y, 10F, Path.Direction.CW)
                        path?.moveTo(event.x, event.y)
                        SplineLag()
                    }
                }
            }
            invalidate()
            return true
        }

        private fun SplineLag(){
            var t = kx[0]
            var ty : Float
            path?.moveTo(kx[0], ky[0])
            while(t < kx[count!!]){
                t += 1F
                ty = SumSpline(t)
                path?.lineTo(t, ty)
            }
            path?.lineTo(kx[count!!], ky[count!!])
        }

        private fun SumSpline(t : Float): Float {
            var S = 0F
            var Si : Float
            var A : Float
            for(i in 0..count!!){
                A = 1F
                for(j in 0..count!!){
                    if(j != i){
                        A *= (t - kx[j]) / (kx[i] - kx[j])
                    }
                }
                Si = ky[i] * A
                S += Si
            }
            return S
        }

        override fun onDraw(canvas: Canvas) {
            if(path != null) {
                canvas.drawPath(path!!, paint)
                path?.reset()
            }
            if(path1 != null){
                canvas.drawPath(path1!!, paint1)
                path?.reset()
            }
        }

        init {
            path = Path()
            path1 = Path()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            paint.color = Color.BLACK
            paint1.strokeWidth = 10f
            paint1.color = Color.RED
        }
    }

}