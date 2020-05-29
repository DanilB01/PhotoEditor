package com.example.experience

class Matrix3x3 {
    private var a11: Float
    private var a12: Float
    private var a13: Float
    private var a21: Float
    private var a22: Float
    private var a23: Float
    private var a31: Float
    private var a32: Float
    private var a33: Float

    constructor(
        a11: Float, a12: Float, a13: Float,
        a21: Float, a22: Float, a23: Float,
        a31: Float, a32: Float, a33: Float
    ) {
        this.a11 = a11
        this.a12 = a12
        this.a13 = a13
        this.a21 = a21
        this.a22 = a22
        this.a23 = a23
        this.a31 = a31
        this.a32 = a32
        this.a33 = a33
    }

    constructor() {
        a13 = 0F
        a12 = a13
        a11 = a12
        a23 = 0F
        a22 = a23
        a21 = a22
        a33 = 0F
        a32 = a33
        a31 = a32
    }

    operator fun set(i: Int, j: Int, `val`: Float) {
        if (i == 0 && j == 0) a11 = `val`
        if (i == 0 && j == 1) a12 = `val`
        if (i == 0 && j == 2) a13 = `val`
        if (i == 1 && j == 0) a21 = `val`
        if (i == 1 && j == 1) a22 = `val`
        if (i == 1 && j == 2) a23 = `val`
        if (i == 2 && j == 0) a31 = `val`
        if (i == 2 && j == 1) a32 = `val`
        if (i == 2 && j == 2) a33 = `val`
    }

    operator fun get(i: Int, j: Int): Float {
        if (i == 0 && j == 0) return a11
        if (i == 0 && j == 1) return a12
        if (i == 0 && j == 2) return a13
        if (i == 1 && j == 0) return a21
        if (i == 1 && j == 1) return a22
        if (i == 1 && j == 2) return a23
        if (i == 2 && j == 0) return a31
        if (i == 2 && j == 1) return a32
        if (i == 2 && j == 2) return a33
        else return 404F
    }

    fun det2(i: Int, j: Int): Float {
        val f = intArrayOf(1, 2, 0, 2, 0, 1)
        return get(f[2 * i], f[2 * j]) * get(f[2 * i + 1], f[2 * j + 1]) -
                get(f[2 * i], f[2 * j + 1]) * get(f[2 * i + 1], f[2 * j])
    }

    fun det3(): Float {
        return get(0, 0) * det2(0, 0) -
                get(0, 1) * det2(0, 1) +
                get(0, 2) * det2(0, 2)
    }
}
