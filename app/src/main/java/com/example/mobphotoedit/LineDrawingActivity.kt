package com.example.mobphotoedit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_line_drawing.*
import kotlin.math.*
import java.util.*


class LineDrawingActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_drawing)

        trash.setOnClickListener {
            (LineDrawing as MySurfaceView).removePoints()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    class MySurfaceView (context: Context, attrs: AttributeSet? = null) : View(context, attrs){
        private val cur = mutableListOf<DPoint>() // массив точек
        private var flag : Boolean = false
        private var count = 0
        private var N : Int? = null
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val paint1 = Paint(Paint.ANTI_ALIAS_FLAG)
        private val paint2 = Paint(Paint.ANTI_ALIAS_FLAG)
        private var path: Path?
        private var path1: Path?
        private var path2: Path?
        private var pw = 0f
        private var ph = 0f
        var ind = -1

        fun removePoints(){
            cur.clear()
            flag = false
            count = 0
            path?.reset()
            path1?.reset()
            path2?.reset()
            invalidate()
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            pw = (width - paddingLeft - paddingRight).toFloat()
            ph = (height - paddingTop - paddingEnd).toFloat()
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                ind = searchPoint(event.x, event.y)
                if(!flag){
                    cur.add(DPoint(event.x, event.y))
                    count = 0
                    path1?.addCircle(event.x, event.y, 5F, Path.Direction.CW)
                    path2?.addCircle(event.x, event.y, 50F, Path.Direction.CW)
                    path?.moveTo(event.x, event.y)
                }
            }
            if(action == MotionEvent.ACTION_MOVE){
                if(ind != -1){
                    cur[ind].x = event.x
                    cur[ind].y = event.y
                    path?.reset()
                    path1?.reset()
                    path2?.reset()
                    for(i in 0 until cur.size){
                        path1?.addCircle(cur[i].x, cur[i].y, 5F, Path.Direction.CW)
                        path2?.addCircle(cur[i].x, cur[i].y, 50F, Path.Direction.CW)
                    }
                    path?.moveTo(cur[0].x, cur[0].y)
                    if(cur.size > 2){
                        DrawSpline()
                    }
                    else if(cur.size == 2){
                        path?.moveTo(cur[0].x, cur[0].y)
                        path?.lineTo(cur[1].x, cur[1].y)
                    }
                }
            }
            if (action == MotionEvent.ACTION_UP) {
                if(ind == -1){
                    if(!flag) {
                        flag = true
                    } else {
                        cur.add(DPoint(event.x, event.y))
                        count++
                        if(count == 1){
                            path?.moveTo(cur[0].x, cur[0].y)
                            path?.lineTo(event.x, event.y)
                            path1?.addCircle(event.x, event.y, 5F, Path.Direction.CW)
                            path2?.addCircle(event.x, event.y, 50F, Path.Direction.CW)
                            path?.moveTo(event.x, event.y)
                        }
                        else {
                            path?.reset()
                            path1?.addCircle(event.x, event.y, 5F, Path.Direction.CW)
                            path2?.addCircle(event.x, event.y, 50F, Path.Direction.CW)
                            path?.moveTo(cur[0].x, cur[0].y)
                            DrawSpline()
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

        private fun thomasAlgorithm(): MutableList<DPoint> {
            val D = mutableListOf<DPoint>()
            val B = mutableListOf<Float>()
            val dX = mutableListOf<DPoint>()
            for (i in 0 until this!!.N!!) {
                dX.add(DPoint(0F, 0F))
                B.add(0F)
            }
            B[0] = 2F
            B.add(0F)
            D.add(pntSum(cur[0], pntMul(2F, cur[1])))
            for (i in 1 until N!! - 1) {
                D.add(
                    pntSum(
                        pntMul(4F, cur[i]),
                        pntMul(2F, cur[i + 1])
                    )
                )
            }
            D.add(pntSum(pntMul(8F, cur[N!! - 1]), cur[N!!]))
            for (i in 1 until N!!) {
                val m: Float = a(i) / b(i - 1)
                B[i] = b(i) - m * c(i - 1)
                D[i] = pntSum(D[i], pntMul(-m, D[i - 1]))
            }
            //D(N) / B(N)
            var xnnx = D[N!! - 1].x / B[N!! - 1]
            var xnny = D[N!! - 1].y / B[N!! - 1]
            dX[N!! - 1] = DPoint(xnnx, xnny)
            for (i in N!! - 2 downTo 0) {
                //X(i) = (D(i) - C(i) * X(i + 1)) / B(i)
                xnnx = (D[i].x - c(i) * dX[i + 1].x) / B[i]
                xnny = (D[i].y - c(i) * dX[i + 1].y) / B[i]
                dX[i] = DPoint(xnnx, xnny)
            }
            return dX
        }

        private fun DrawSpline() {
            // algorithm explaining:
            //
            // 2 * P(1, 0  ) + 1 * P(1, 1 )                 = mPointsArray(0)+2*mPointsArray(1)
            // 1 * P(1, i-1) + 4 * P(1, i ) + 1 * P(1, i+1) = 4*mPointsArray(i)+2*mPointsArray(i+1), for i in [1, N-2]
            //                 2 * P(1,N-2) + 7 * P(1, N-1) = 8*mPointsArray(N-1)+mPointsArray(N)
            N = cur.size - 1
            var P1: MutableList<DPoint> = thomasAlgorithm()
            var P2: MutableList<DPoint> = otherMath(P1)
            drawSpline(P1, P2 as ArrayList<DPoint>)
        }

        private fun pntSum(a: DPoint, b: DPoint): DPoint {
            return DPoint(a.x + b.x, a.y + b.y)
        }

        private fun pntMul(a: Float, b: DPoint): DPoint {
            return DPoint(a * b.x, a * b.y)
        }

        private fun a(ind: Int): Float {
            if (ind == 0) return 0F
            return if (ind == N!! - 1) 2F else 1F
        }

        private fun b(ind: Int): Float {
            if (ind == 0) return 2F
            return if (ind == N!! - 1) 7F else 4F
        }

        private fun c(ind: Int): Float {
            if (ind == 0) return 1F
            return if (ind == N!! - 1) 0F else 1F
        }

        private fun drawSpline(
            p1: MutableList<DPoint>,
            p2: MutableList<DPoint>
        ) {
            for (i in 0 until N!!) {
                val step = 1000
                for (j in 0..step) {
                    // B(t) = (1-t)^3 * P0 + 3*t(t-1)^2*P1 + 3*t^2*(t-1)*P2 + t^3*P3
                    val t = j.toFloat() / step
                    val r1 = pntMul((1 - t) * (1 - t) * (1 - t), cur[i])
                    val r2 = pntMul((1 - t) * (1 - t) * t, p1[i])
                    val r3 = pntMul((1 - t) * t * t, p2[i])
                    val r4 = pntMul(t * t * t, cur[i + 1])
                    val nx = (r1.x + 3 * r2.x + 3 * r3.x + r4.x)
                    val ny = (r1.y + 3 * r2.y + 3 * r3.y + r4.y)
                    if (nx < 0 || nx >= pw) continue
                    if (ny < 0 || ny >= ph) continue
                    path?.lineTo(nx, ny)
                }
            }
        }

        private fun otherMath(P1: MutableList<DPoint>): MutableList<DPoint> {
            val answer = mutableListOf<DPoint>()
            for (i in 0..N!! - 2) {
                val nx: Float = 2 * cur[i + 1].x - P1[i + 1].x
                val ny: Float = 2 * cur[i + 1].y - P1[i + 1].y
                answer.add(DPoint(nx, ny))
            }
            val nx: Float = (cur[N!!].x + P1[N!! - 1].x) / 2
            val ny: Float = (cur[N!!].y + P1[N!! - 1].y) / 2
            answer.add(DPoint(nx, ny))
            return answer
        }

        private fun searchPoint(x : Float, y : Float): Int {
            for(i in 0 until cur.size){
                if(abs(x - cur[i].x) < 50 && abs(y - cur[i].y) < 50) {
                    return i
                }
            }
            return -1
        }

        override fun onDraw(canvas: Canvas) {
            if(path != null) {
                canvas.drawPath(path!!, paint)
                //path?.reset()
            }
            if(path1 != null){
                canvas.drawPath(path1!!, paint1)
                canvas.drawPath(path2!!, paint2)
            }
        }

        init {
            path = Path()
            path1 = Path()
            path2 = Path()
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            paint.color = resources.getColor(R.color.lightPink)
            paint1.strokeWidth = 5f
            paint1.color = resources.getColor(R.color.knot_in)
            paint2.style = Paint.Style.STROKE
            paint2.strokeWidth = 2f
            paint2.color = resources.getColor(R.color.knot_out)
        }
    }
}

class DPoint {
    var x = 0F
    var y = 0F

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}
