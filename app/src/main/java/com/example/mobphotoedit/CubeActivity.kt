package com.example.mobphotoedit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.android.synthetic.main.activity_cube.*
import kotlinx.android.synthetic.main.activity_cube.view.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class CubeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cube)

        /*turnRight.setOnClickListener {
            (cubeView as MySurfaceView).rotateCubPos = 1
            cubeView.invalidate()
        }
        turnLeft.setOnClickListener {
            (cubeView as MySurfaceView).rotateCubPos = 2
            cubeView.invalidate()
        }
        turnUp.setOnClickListener {
            (cubeView as MySurfaceView).rotateCubPos = 3
            cubeView.invalidate()
        }
        turnDown.setOnClickListener {
            (cubeView as MySurfaceView).rotateCubPos = 4
            cubeView.invalidate()
        }*/
        turnClockwise.setOnClickListener {
            (cubeView as MySurfaceView).rotateCubPos = 5
            cubeView.invalidate()
        }
        turnСounterСlockwise.setOnClickListener {
            (cubeView as MySurfaceView).rotateCubPos = 6
            cubeView.invalidate()
        }

    }

    class MySurfaceView (context: Context, attrs: AttributeSet? = null) : SurfaceView(context, attrs), SurfaceHolder.Callback {
        var rotateCubPos = 0
        private val nodes = arrayOf(
            floatArrayOf(-200f, -200f, -200f),
            floatArrayOf(-200f, -200f,  200f),
            floatArrayOf(-200f,  200f, -200f),
            floatArrayOf(-200f,  200f,  200f),
            floatArrayOf( 200f, -200f, -200f),
            floatArrayOf( 200f, -200f,  200f),
            floatArrayOf( 200f,  200f, -200f),
            floatArrayOf( 200f,  200f,  200f)
        )
        val edges = arrayOf(
            intArrayOf(0, 1),
            intArrayOf(1, 3),
            intArrayOf(3, 2),
            intArrayOf(2, 0),
            intArrayOf(4, 5),
            intArrayOf(5, 7),
            intArrayOf(7, 6),
            intArrayOf(6, 4),
            intArrayOf(0, 4),
            intArrayOf(1, 5),
            intArrayOf(2, 6),
            intArrayOf(3, 7)
        )
        val cube = arrayOf( //грани
            surfacePlan(arrayOf(0,1,3,2), Paint()),
            surfacePlan(arrayOf(4,5,7,6), Paint()),
            surfacePlan(arrayOf(0,1,5,4), Paint()),
            surfacePlan(arrayOf(2,3,7,6), Paint()),
            surfacePlan(arrayOf(1,3,7,5), Paint()),
            surfacePlan(arrayOf(0,2,6,4), Paint())
        )
        var pathes = arrayOf(
            pathCharacteristic(Path(), Paint()),
            pathCharacteristic(Path(), Paint()),
            pathCharacteristic(Path(), Paint())
        )

        var angle: Float = (PI / 48).toFloat()
        override fun surfaceChanged(
            holder: SurfaceHolder, format: Int, width: Int,
            height: Int
        ) {
        }

        override fun surfaceCreated(holder: SurfaceHolder) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {

        }


        var curX: Float = 0f
        var curY: Float = 0f

        override fun onTouchEvent(event: MotionEvent): Boolean {
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    curX = event.x
                    curY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                        if(abs(event.x - curX) > 10 && abs(event.y - curY) > 10){
                            if(event.x > curX){
                                changeRotationY(1)//right
                                if(event.y > curY)
                                    (cubeView as MySurfaceView).rotateCubPos = 4
                                else
                                    (cubeView as MySurfaceView).rotateCubPos = 3
                            }
                            else{
                                changeRotationY(0)//left
                                if(event.y > curY)
                                    (cubeView as MySurfaceView).rotateCubPos = 4
                                else
                                    (cubeView as MySurfaceView).rotateCubPos = 3
                            }
                            invalidate()
                            curX = event.x
                            curY = event.y
                        }
                        else if (abs(event.x - curX) > 10) {
                            if (event.x > curX)
                                (cubeView as MySurfaceView).rotateCubPos = 1
                            else
                                (cubeView as MySurfaceView).rotateCubPos = 2
                            invalidate()
                            curX = event.x
                            curY = event.y
                        }
                        else if (abs(event.y - curY) > 10) {
                            if (event.y > curY)
                                (cubeView as MySurfaceView).rotateCubPos = 4
                            else
                                (cubeView as MySurfaceView).rotateCubPos = 3
                            invalidate()
                            curX = event.x
                            curY = event.y
                        }
                }
            }
            return true
        }

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            val transX = (width * 0.5).toFloat()
            val transY = (height * 0.5).toFloat()
            canvas?.translate(transX, transY)
            when(this.rotateCubPos){ //rotate left, down, counterclockwise = 0, rotate right, up, clockwise = 1
                1 -> changeRotationY(1)
                2 -> changeRotationY(0)
                3 -> changeRotationX(1)
                4 -> changeRotationX(0)
                5 -> {
                    angle = (PI / 16).toFloat()
                    changeRotationZ(1)
                    angle = (PI / 48).toFloat()
                }
                6 -> {
                    angle = (PI / 16).toFloat()
                    changeRotationZ(0)
                    angle = (PI / 48).toFloat()
                }
            }
            var curMin = 0f
            var curMinIndex = 0
            for(i in 0..7){
                if(nodes[i][2] < curMin){
                    curMin = nodes[i][2]
                    curMinIndex = i
                }
            }
            when(curMinIndex){
                0->drawFilledPath(arrayOf(1,3,4))
                1->drawFilledPath(arrayOf(1,3,5))
                2->drawFilledPath(arrayOf(1,2,4))
                3->drawFilledPath(arrayOf(1,2,5))
                4->drawFilledPath(arrayOf(0,3,4))
                5->drawFilledPath(arrayOf(0,3,5))
                6->drawFilledPath(arrayOf(0,2,4))
                7->drawFilledPath(arrayOf(0,2,5))
            }
            for(i in 0..2){
                canvas?.drawPath(this.pathes[i].path, this.pathes[i].paint)
                pathes[i].path.reset()
            }
        }

        fun changeRotationX(side:Int){
            if(side == 1){
                for (node in this.nodes) {
                    val x = node[0]
                    val y = node[1]
                    val z = node[2]
                    node[0] = x
                    node[1] = y * cos(this.angle) - z * sin(this.angle)
                    node[2] = y * sin(this.angle) + z * cos(this.angle)
                }
            }
            else{
                for (node in this.nodes) {
                    val x = node[0]
                    val y = node[1]
                    val z = node[2]
                    node[0] = x
                    node[1] = y * cos(this.angle) + z * sin(this.angle)
                    node[2] = - y * sin(this.angle) + z * cos(this.angle)
                }
            }
        }

        fun changeRotationY(side:Int){
            if(side == 1){
                for (node in this.nodes) {
                    val x = node[0]
                    val y = node[1]
                    val z = node[2]
                    node[0] = x * cos(this.angle) + z * sin(this.angle)
                    node[1] = y
                    node[2] = - x * sin(this.angle) + z * cos(this.angle)
                }
            }
            else{
                for (node in this.nodes) {
                    val x = node[0]
                    val y = node[1]
                    val z = node[2]
                    node[0] = x * cos(this.angle) - z * sin(this.angle)
                    node[1] = y
                    node[2] = x * sin(this.angle) + z * cos(this.angle)
                }
            }
        }

        fun changeRotationZ(side:Int){
            if(side == 1){
                for (node in this.nodes) {
                    val x = node[0]
                    val y = node[1]
                    val z = node[2]
                    node[0] = x * cos(this.angle) - y * sin(this.angle)
                    node[1] = x * sin(this.angle) + y * cos(this.angle)
                    node[2] = z
                }
            }
            else{
                for (node in this.nodes) {
                    val x = node[0]
                    val y = node[1]
                    val z = node[2]
                    node[0] = x * cos(this.angle) + y * sin(this.angle)
                    node[1] = - x * sin(this.angle) + y * cos(this.angle)
                    node[2] = z
                }
            }
        }

        fun drawFilledPath(planes: Array<Int>){
            for(i in 0..2){
                pathes[i].paint = cube[planes[i]].curPaint
                var kx = nodes[cube[planes[i]].nodeList[0]][0]
                var ky = nodes[cube[planes[i]].nodeList[0]][1]
                pathes[i].path.moveTo(kx, ky)
                for(j in 1..4){
                    kx = nodes[cube[planes[i]].nodeList[j%4]][0]
                    ky = nodes[cube[planes[i]].nodeList[j%4]][1]
                    pathes[i].path.lineTo(kx, ky)
                }
                pathes[i].path.close()
            }
        }

        init {
            cube[0].curPaint.color = resources.getColor(R.color.surfPlane1)
            cube[1].curPaint.color = resources.getColor(R.color.surfPlane2)
            cube[2].curPaint.color = resources.getColor(R.color.surfPlane3)
            cube[3].curPaint.color = resources.getColor(R.color.surfPlane4)
            cube[4].curPaint.color = resources.getColor(R.color.surfPlane5)
            cube[5].curPaint.color = resources.getColor(R.color.surfPlane6)

            holder.addCallback(this)
            setWillNotDraw(false)
        }
    }
}

data class surfacePlan(
    var nodeList: Array<Int>,
    var curPaint: Paint
)

data class pathCharacteristic(
    var path: Path,
    var paint: Paint
)