package com.example.mobphotoedit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cube.*
import kotlinx.android.synthetic.main.activity_cube.view.*
import kotlin.math.*

class CubeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cube)

        turnClockwise.setOnClickListener {
            (cubeView as MySurfaceView).rotationPos = 5
            cubeView.invalidate()
        }
        turnСounterСlockwise.setOnClickListener {
            (cubeView as MySurfaceView).rotationPos = 6
            cubeView.invalidate()
        }

        changeView.setOnClickListener{
            (cubeView as MySurfaceView).figure++
            if ((cubeView as MySurfaceView).figure == 3)
                (cubeView as MySurfaceView).figure = 0
            cubeView.invalidate()
        }

    }

    class MySurfaceView (context: Context, attrs: AttributeSet? = null) : SurfaceView(context, attrs), SurfaceHolder.Callback {
        var figure = 0 // 0 - cube, 1 - tetrahedron, 2 - octahedron
        var a = 550f // side length
        var rotationPos = 0
        private val cubeNodes = arrayOf(
            floatArrayOf(-200f, -200f, -200f),
            floatArrayOf(-200f, -200f,  200f),
            floatArrayOf(-200f,  200f, -200f),
            floatArrayOf(-200f,  200f,  200f),
            floatArrayOf( 200f, -200f, -200f),
            floatArrayOf( 200f, -200f,  200f),
            floatArrayOf( 200f,  200f, -200f),
            floatArrayOf( 200f,  200f,  200f)
        )
        val cubeEdges = arrayOf(
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

        var tetraNodes = arrayOf(
            floatArrayOf(-a/6*sqrt(3f), -a/2, -a/3*sqrt(2/3f)),
            floatArrayOf(-a/6*sqrt(3f), a/2, -a/3*sqrt(2/3f)),
            floatArrayOf(a/3*sqrt(3f), 0f, -a/3*sqrt(2/3f)),
            floatArrayOf(0f, 0f, 2*a/3*sqrt(2/3f))
        )
        var tetraEdges = arrayOf(
            intArrayOf(0, 1),
            intArrayOf(1, 2),
            intArrayOf(2, 0),
            intArrayOf(3, 0),
            intArrayOf(3, 1),
            intArrayOf(3, 2)
        )

        var octNodes = arrayOf(//tetra nodes, fig[1][0]
            floatArrayOf(230f, 230f, 0f),
            floatArrayOf(230f, -230f, 0f),
            floatArrayOf(-230f, -230f, 0f),
            floatArrayOf(-230f, 230f, 0f),
            floatArrayOf(0f, 0f, 230f*sqrt(2f)),
            floatArrayOf(0f, 0f, -230f*sqrt(2f))
        )

        var octEdges = arrayOf(
            intArrayOf(0, 1),
            intArrayOf(1, 2),
            intArrayOf(2, 3),
            intArrayOf(3, 0),
            intArrayOf(4, 0),
            intArrayOf(4, 1),
            intArrayOf(4, 2),
            intArrayOf(4, 3),
            intArrayOf(5, 0),
            intArrayOf(5, 1),
            intArrayOf(5, 2),
            intArrayOf(5, 3)
        )

        var angle = (PI / 48).toFloat()
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
                                (cubeView as MySurfaceView).rotationPos = 4
                            else
                                (cubeView as MySurfaceView).rotationPos = 3
                        }
                        else{
                            changeRotationY(0)//left
                            if(event.y > curY)
                                (cubeView as MySurfaceView).rotationPos = 4
                            else
                                (cubeView as MySurfaceView).rotationPos = 3
                        }
                        invalidate()
                        curX = event.x
                        curY = event.y
                    }
                    else if (abs(event.x - curX) > 10) {
                        if (event.x > curX)
                            (cubeView as MySurfaceView).rotationPos = 1
                        else
                            (cubeView as MySurfaceView).rotationPos = 2
                        invalidate()
                        curX = event.x
                        curY = event.y
                    }
                    else if (abs(event.y - curY) > 10) {
                        if (event.y > curY)
                            (cubeView as MySurfaceView).rotationPos = 4
                        else
                            (cubeView as MySurfaceView).rotationPos = 3
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
            val p = Paint()
            p.color = resources.getColor(R.color.lightPink)
            p.strokeWidth = 5f
            when(this.rotationPos){
                1 -> changeRotationY(1)//right
                2 -> changeRotationY(0)//left
                3 -> changeRotationX(1)//up
                4 -> changeRotationX(0)//down
                5 -> {//clockwise
                    angle = (PI / 16).toFloat()
                    changeRotationZ(1)
                    angle = (PI / 48).toFloat()
                }
                6 -> {//counterclockwise
                    angle = (PI / 16).toFloat()
                    changeRotationZ(0)
                    angle = (PI / 48).toFloat()
                }
            }
            rotationPos = 0
            when(figure){
                0->{
                    var curMin = 0f
                    var curMinIndex = 0
                    for(i in 0..7){
                        if(cubeNodes[i][2] < curMin){
                            curMin = cubeNodes[i][2]
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
                1->{
                    for (edge in tetraEdges) {
                        val xy1 = tetraNodes[edge[0]]
                        val xy2 = tetraNodes[edge[1]]
                        canvas?.drawLine(xy1[0], xy1[1], xy2[0], xy2[1], p)
                    }
                }
                2->{
                    for (edge in octEdges) {
                        val xy1 = octNodes[edge[0]]
                        val xy2 = octNodes[edge[1]]
                        canvas?.drawLine(xy1[0], xy1[1], xy2[0], xy2[1], p)
                    }
                }
            }
        }

        fun changeRotationX(side:Int){
            when(figure){
                0->{
                    if(side == 1){
                        for (node in this.cubeNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x
                            node[1] = y * cos(this.angle) - z * sin(this.angle)
                            node[2] = y * sin(this.angle) + z * cos(this.angle)
                        }
                    }
                    else{
                        for (node in this.cubeNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x
                            node[1] = y * cos(this.angle) + z * sin(this.angle)
                            node[2] = - y * sin(this.angle) + z * cos(this.angle)
                        }
                    }
                }
                1->{
                    if(side == 1){
                        for (node in this.tetraNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x
                            node[1] = y * cos(this.angle) - z * sin(this.angle)
                            node[2] = y * sin(this.angle) + z * cos(this.angle)
                        }
                    }
                    else{
                        for (node in this.tetraNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x
                            node[1] = y * cos(this.angle) + z * sin(this.angle)
                            node[2] = - y * sin(this.angle) + z * cos(this.angle)
                        }
                    }
                }
                2->{
                    if(side == 1){
                        for (node in this.octNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x
                            node[1] = y * cos(this.angle) - z * sin(this.angle)
                            node[2] = y * sin(this.angle) + z * cos(this.angle)
                        }
                    }
                    else{
                        for (node in this.octNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x
                            node[1] = y * cos(this.angle) + z * sin(this.angle)
                            node[2] = - y * sin(this.angle) + z * cos(this.angle)
                        }
                    }
                }
            }
        }

        fun changeRotationY(side:Int){
            when(figure) {
                0 -> {
                    if (side == 1) {
                        for (node in this.cubeNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x * cos(this.angle) + z * sin(this.angle)
                            node[1] = y
                            node[2] = -x * sin(this.angle) + z * cos(this.angle)
                        }
                    } else {
                        for (node in this.cubeNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x * cos(this.angle) - z * sin(this.angle)
                            node[1] = y
                            node[2] = x * sin(this.angle) + z * cos(this.angle)
                        }
                    }
                }
                1 -> {
                    if (side == 1) {
                        for (node in this.tetraNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x * cos(this.angle) + z * sin(this.angle)
                            node[1] = y
                            node[2] = -x * sin(this.angle) + z * cos(this.angle)
                        }
                    }
                    else {
                        for (node in this.tetraNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x * cos(this.angle) - z * sin(this.angle)
                            node[1] = y
                            node[2] = x * sin(this.angle) + z * cos(this.angle)
                        }
                    }
                }
                2->{
                    if (side == 1) {
                        for (node in this.octNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x * cos(this.angle) + z * sin(this.angle)
                            node[1] = y
                            node[2] = -x * sin(this.angle) + z * cos(this.angle)
                        }
                    }
                    else {
                        for (node in this.octNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x * cos(this.angle) - z * sin(this.angle)
                            node[1] = y
                            node[2] = x * sin(this.angle) + z * cos(this.angle)
                        }
                    }
                }
            }
        }

        fun changeRotationZ(side:Int){
            when(figure) {
                0 -> {
                    if (side == 1) {
                        for (node in this.cubeNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x * cos(this.angle) - y * sin(this.angle)
                            node[1] = x * sin(this.angle) + y * cos(this.angle)
                            node[2] = z
                        }
                    } else {
                        for (node in this.cubeNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x * cos(this.angle) + y * sin(this.angle)
                            node[1] = -x * sin(this.angle) + y * cos(this.angle)
                            node[2] = z
                        }
                    }
                }
                1 -> {
                    if (side == 1) {
                        for (node in this.tetraNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x * cos(this.angle) - y * sin(this.angle)
                            node[1] = x * sin(this.angle) + y * cos(this.angle)
                            node[2] = z
                        }
                    } else {
                        for (node in this.tetraNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x * cos(this.angle) + y * sin(this.angle)
                            node[1] = -x * sin(this.angle) + y * cos(this.angle)
                            node[2] = z
                        }
                    }
                }
                2 -> {
                    if (side == 1) {
                        for (node in this.octNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x * cos(this.angle) - y * sin(this.angle)
                            node[1] = x * sin(this.angle) + y * cos(this.angle)
                            node[2] = z
                        }
                    } else {
                        for (node in this.octNodes) {
                            val x = node[0]
                            val y = node[1]
                            val z = node[2]
                            node[0] = x * cos(this.angle) + y * sin(this.angle)
                            node[1] = -x * sin(this.angle) + y * cos(this.angle)
                            node[2] = z
                        }
                    }
                }
            }
        }

        fun drawFilledPath(planes: Array<Int>){
            for(i in 0..2){
                pathes[i].paint = cube[planes[i]].curPaint
                var kx = cubeNodes[cube[planes[i]].nodeList[0]][0]
                var ky = cubeNodes[cube[planes[i]].nodeList[0]][1]
                pathes[i].path.moveTo(kx, ky)
                for(j in 1..4){
                    kx = cubeNodes[cube[planes[i]].nodeList[j%4]][0]
                    ky = cubeNodes[cube[planes[i]].nodeList[j%4]][1]
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