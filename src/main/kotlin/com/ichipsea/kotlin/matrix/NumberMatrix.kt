package com.ichipsea.kotlin.matrix

import kotlin.math.*

operator fun <M : Number, N : Number> Matrix<M>.plus(other: Matrix<N>): Matrix<Double> {
    if (rows != other.rows || cols != other.cols)
        throw IllegalArgumentException("Matrices not match")

    return mapIndexed { x, y, value -> value.toDouble() + other[x, y].toDouble() }
}

operator fun <N : Number> Matrix<N>.unaryMinus(): Matrix<Double> {
    return map { -it.toDouble() }
}

operator fun <M : Number, N : Number> Matrix<M>.minus(other: Matrix<N>): Matrix<Double> {
    return this + (-other)
}

operator fun <M : Number, N : Number> Matrix<M>.times(other: Matrix<N>): Matrix<Double> {
    if (rows != other.rows || cols != other.cols)
        throw IllegalArgumentException("Matrices not match")

    return mapIndexed { x, y, value -> value.toDouble() * other[x, y].toDouble() }
}

operator fun <M : Number> Matrix<M>.times(other: Number): Matrix<Double> {
    return map { it.toDouble() * other.toDouble() }
}

operator fun <M : Number> Number.times(other: Matrix<M>): Matrix<Double> {
    return other * this
}

operator fun <M : Number, N : Number> Matrix<M>.div(other: Matrix<N>): Matrix<Double> {
    if (rows != other.rows || cols != other.cols)
        throw IllegalArgumentException("Matrices not match")

    return mapIndexed { x, y, value -> value.toDouble() / other[x, y].toDouble() }
}

operator fun <M : Number> Matrix<M>.div(other: Number): Matrix<Double> {
    assert(other.toDouble() != 0.0) { println("Cannot divide by zero") }
    return map { it.toDouble() / other.toDouble() }
}

infix fun <M : Number, N : Number> Matrix<M>.x(other: Matrix<N>): Matrix<Double> {
    if (other.rows != cols)
        throw IllegalArgumentException("Matrices not match first ${this.rows}x${this.cols} second ${other.rows}x${other.cols}")
    return createMatrix(other.cols, rows) { x, y ->
        var value = .0
        for (i in 0 until cols)
            value += this[i, y].toDouble() * other[x, i].toDouble()
        value
    }
}

infix fun <M: Number, N: Number> Matrix<M>.xDeprecated(other: Matrix<N>): Matrix<Double> {
    if (rows !== other.cols)
        throw IllegalArgumentException("Matrices not match")

    return createMatrix(cols, other.rows) { x, y ->
        var value = .0
        for (i in 0..rows-1)
            value += this[x, i].toDouble() * other[i, y].toDouble()
        value
    }
}

fun <M : Number> Matrix<M>.sum(): Double {
    var sum = 0.0
    forEach { sum += it.toDouble() }
    return sum
}

fun <M : Number> Matrix<M>.cofactor(): Matrix<Double> {
    return createMatrix(cols, rows) { x, y ->
        var value = determinantOfSubMatrix(x, y)
        value = if ((x + y)%2!=0) -value else value
        value
    }
}

fun <M : Number> Matrix<M>.adjugate(): Matrix<Double> {
    return cofactor().asTransposed()
}

fun <M : Number> Matrix<M>.inverse(): Matrix<Double> {
    val det = determinant()
    assert(det != 0.0) { throw IllegalArgumentException("cannot calculate an inverse of a zero-determined matrix") }
    return adjugate() / det
}

fun <M : Number> Matrix<M>.rightInverse(): Matrix<Double> {
    return asTransposed() x (this x asTransposed()).inverse()
}

fun <M : Number> Matrix<M>.leftInverse(): Matrix<Double> {
    return (asTransposed() x this).inverse() x asTransposed()
}

fun <M : Number> Matrix<M>.determinant(): Double {
    assert(this.cols == this.rows) { println("Can't calculate determinant for a non-cubic matrix") }
    return if (this.cols == 1 && this.rows == 1) {
        this[0, 0].toDouble()
    }
    else if (this.cols == 2 && this.rows == 2) {
        this[0, 0].toDouble() * this[1, 1].toDouble() - (this[1, 0].toDouble() * this[0, 1].toDouble())
    }
    else if (this.cols > 2 && this.rows > 2) {
        var mvalue = 0.0
        var negative: Boolean = false
        for (i in 0 until this.cols) {
            val posValue = this[i, 0].toDouble() * determinantOfSubMatrix(i, 0)
            mvalue += if (negative) -posValue else posValue
            negative = !negative
        }
        mvalue
    }
    else Double.NaN
}

private fun <M : Number> Matrix<M>.determinantOfSubMatrix(col: Int, row: Int): Double {
    val list: MutableList<Double> = mutableListOf()
    mapIndexed { x, y, value ->
        if (x != col && y != row) list.add(value.toDouble())
        value
    }
    return matrixOf(cols - 1, rows - 1, *list.toTypedArray()).determinant()
}

fun <M : Number> log(matrix: Matrix<M>, sub: M): Matrix<Double> = matrix.map { log(it.toDouble(), sub.toDouble()) }

fun <M : Number> loge(matrix: Matrix<M>): Matrix<Double> = matrix.map { log(it.toDouble(), exp(1.0)) }

fun <M : Number> log10(matrix: Matrix<M>): Matrix<Double> = matrix.map { log10(it.toDouble()) }

fun <M : Number> Matrix<M>.pow(sup: M): Matrix<Double> = map { it.toDouble().pow(sup.toDouble()) }

fun <M : Number> Matrix<M>.pow(sup: Matrix<M>): Matrix<Double> = mapIndexed { x, y, value ->
    value.toDouble().pow(sup[x, y].toDouble())
}

fun <M : Number> round(matrix: Matrix<M>, decimalPlaces: Int = 3): Matrix<Double> =
        matrix.map { round(x = it.toDouble() * 10.0.pow(decimalPlaces)) / 10.0.pow(decimalPlaces) }

fun <M : Number> ceil(matrix: Matrix<M>): Matrix<Double> = matrix.map { ceil(it.toDouble()) }

fun <M : Number> sin(matrix: Matrix<M>): Matrix<Double> = matrix.map { sin(it.toDouble()) }

fun <M : Number> tan(matrix: Matrix<M>): Matrix<Double> = matrix.map { tan(it.toDouble()) }

fun <M : Number> floor(matrix: Matrix<M>): Matrix<Double> = matrix.map { floor(it.toDouble()) }

fun <M : Number> sqrt(matrix: Matrix<M>): Matrix<Double> = matrix.map { sqrt(it.toDouble()) }

fun <M : Number> solveEquations(coefs: Matrix<M>, cons: Matrix<M>): Matrix<Double> = coefs.inverse() x cons
