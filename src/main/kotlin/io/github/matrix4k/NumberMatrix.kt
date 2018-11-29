package io.github.matrix4k

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

@Deprecated(message = "This function will be removed in a future version", replaceWith = ReplaceWith("dot"))
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

fun <M: Number> Matrix<M>.determinant(): Double = when {
    rows != cols -> throw IllegalArgumentException("Cannot compute the determinant for non-squared matrices")
    rows == 1 -> this[0, 0].toDouble()
    rows == 2 -> this[0, 0].toDouble() * this[1, 1].toDouble() - (this[1, 0].toDouble() * this[0, 1].toDouble())
    else -> {
        var mvalue = 0.0
        var negative = false
        for (column in 0 until cols) {
            val posValue = this[column, 0].toDouble() * determinantOfSubMatrix(0, column)
            mvalue += if (negative) -posValue else posValue
            negative = !negative
        }
        mvalue
    }
}

private fun <M : Number> Matrix<M>.determinantOfSubMatrix(row: Int, col: Int): Double {
    val list: MutableList<Double> = mutableListOf()
    mapIndexed { x, y, value ->
        if (x != col && y != row) list.add(value.toDouble())
        value
    }
    return matrixOf(cols - 1, rows - 1, *list.toTypedArray()).determinant()
}
