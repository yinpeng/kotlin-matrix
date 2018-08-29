package com.ichipsea.kotlin.matrix

operator fun <M: Number, N: Number> Matrix<M>.plus(other: Matrix<N>): Matrix<Double> {
    if (rows != other.rows || cols != other.cols)
        throw IllegalArgumentException("The matrices do not match")

    return mapIndexed { x, y, value -> value.toDouble() + other[x, y].toDouble() }
}

operator fun <N: Number> Matrix<N>.unaryMinus(): Matrix<Double> = map { -it.toDouble() }

operator fun <M: Number, N: Number> Matrix<M>.minus(other: Matrix<N>): Matrix<Double> = this + (-other)

operator fun <M: Number, N: Number> Matrix<M>.times(other: Matrix<N>): Matrix<Double> {
    if (rows != other.rows || cols != other.cols)
        throw IllegalArgumentException("The matrices do not match")

    return mapIndexed { x, y, value -> value.toDouble() * other[x, y].toDouble() }
}

operator fun <M: Number> Matrix<M>.times(other: Number): Matrix<Double> = map { it.toDouble() * other.toDouble() }

operator fun <M: Number> Number.times(other: Matrix<M>): Matrix<Double> = other * this

operator fun <M: Number, N: Number> Matrix<M>.div(other: Matrix<N>): Matrix<Double> {
    if (rows != other.rows || cols != other.cols)
        throw IllegalArgumentException("The matrices do not match")

    return mapIndexed { x, y, value -> value.toDouble() / other[x, y].toDouble() }
}

operator fun <M: Number> Matrix<M>.div(other: Number): Matrix<Double> = map { it.toDouble() / other.toDouble() }

infix fun <M: Number, N: Number> Matrix<M>.x(other: Matrix<N>): Matrix<Double> {
    if (rows != other.cols)
        throw IllegalArgumentException("The matrices do not match")

    return createMatrix(cols, other.rows) { x, y ->
        var value = .0
        for (i in 0 until rows)
            value += this[x, i].toDouble() * other[i, y].toDouble()
        value
    }
}

infix fun <M: Number, N: Number> Matrix<M>.dot(other: Matrix<N>): Matrix<Double> {
    if (cols != other.rows)
        throw IllegalArgumentException("The matrices do not match: this has $cols columns, other has ${other.rows} rows")

    return createMatrix(other.cols, rows) { x, y ->
        var value = .0
        for (i in 0 until cols)
            value += this[i, y].toDouble() * other[x, i].toDouble()
        value
    }
}