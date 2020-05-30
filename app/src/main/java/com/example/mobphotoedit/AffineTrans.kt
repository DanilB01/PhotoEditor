package com.example.experience

import android.util.Log
import com.example.mobphotoedit.Points

class AffineTrans(
    p21: Points, p22: Points, p23: Points,
    p11: Points, p12: Points, p13: Points
) {
    private var a: Float
    private var b: Float
    private var c: Float
    private var d: Float
    private var e: Float
    private var f: Float
    private val mSecondX: Matrix3x1 = Matrix3x1(p21.x, p22.x, p23.x)
    private val mSecondY: Matrix3x1 = Matrix3x1(p21.y, p22.y, p23.y)
    private val mFirst: Matrix3x3 = Matrix3x3(
        p11.x, p11.y, 1F,
        p12.x, p12.y, 1F,
        p13.x, p13.y, 1F
    )
    private var mError = true
    fun calc(m: Points): Points? {
        return if (mError) null else Points(
            a * m.x + c * m.y + e,
            b * m.x + d * m.y + f
        )
    }

    fun calc(x: Int, y: Int): Points? {
        return if (mError) null else Points(
            a * x + c * y + e,
            b * x + d * y + f
        )
    }

    fun prepare(): Boolean {
        val multiplier: Matrix3x3? = inverse(mFirst)
        if (multiplier == null) {
            Log.i("upd", "det == 0!")
            return false
        }
        val ace: Matrix3x1 = mulMatrix(multiplier, mSecondX)
        val bdf: Matrix3x1 = mulMatrix(multiplier, mSecondY)
        a = ace.get(0)
        c = ace.get(1)
        e = ace.get(2)
        b = bdf.get(0)
        d = bdf.get(1)
        f = bdf.get(2)
        mError = false
        return true
    }

    //matrix of algebraic complements
    private fun moac3(A: Matrix3x3): Matrix3x3 {
        val finish = Matrix3x3()
        for (i in 0..2) {
            for (j in 0..2) {
                finish.set(i, j, A.det2(i, j) * Math.pow(-1.0, (i + j).toDouble()).toFloat())
            }
        }
        return finish
    }

    private fun inverse(start: Matrix3x3): Matrix3x3? {
        val det: Float = start.det3()
        if (det == 0F) {
            // catch this!
            return null
        }
        val finish: Matrix3x3 = reverse(moac3(start))
        for (i in 0..2) {
            for (j in 0..2) {
                finish.set(i, j, finish.get(i, j) / det)
            }
        }
        return finish
    }

    private fun reverse(A: Matrix3x3): Matrix3x3 {
        val finish = Matrix3x3()
        for (i in 0..2) {
            for (j in 0..2) {
                finish.set(i, j, A.get(j, i))
            }
        }
        return finish
    }

    private fun mulMatrix(A: Matrix3x3, B: Matrix3x1): Matrix3x1 {
        val C = Matrix3x1()
        for (i in 0..2) for (j in 0..2) for (r in 0..2) C.set(
            i,
            j,
            C.get(i, j) + A.get(i, r) * B.get(r, j)
        )
        return C
    }

    init {
        // Solver's initialization
        a = p21.x
        c = p22.x
        e = p23.x
        b = p21.y
        d = p22.y
        f = p23.y
    }
}