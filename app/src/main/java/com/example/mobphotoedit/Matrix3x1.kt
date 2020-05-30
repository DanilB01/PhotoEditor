package com.example.experience

class Matrix3x1 {
    private var a11: Float
    private var a21: Float
    private var a31: Float

    constructor(a11: Float, a21: Float, a31: Float) {
        this.a11 = a11
        this.a21 = a21
        this.a31 = a31
    }

    operator fun get(i: Int, j: Int): Float {
        if (i == 0 && j == 0) return a11
        if (i == 1 && j == 0) return a21
        if (i == 2 && j == 0) return a31
        else return 404F
    }

    operator fun get(row: Int): Float {
        if (row == 0) return a11
        if (row == 1) return a21
        if (row == 2) return a31
        else return 404F
    }

    operator fun set(i: Int, j: Int, `val`: Float) {
        if (i == 0 && j == 0) a11 = `val`
        if (i == 1 && j == 0) a21 = `val`
        if (i == 2 && j == 0) a31 = `val`
    }

    constructor() {
        a31 = 0F
        a21 = a31
        a11 = a21
    }
}
