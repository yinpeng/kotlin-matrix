package com.ichipsea.kotlin.matrix

import java.util.*

interface Matrix<out T> {
    val cols: Int
    val rows: Int

    fun row(row: Int): List<T>
    fun col(col: Int): List<T>

    operator fun get(x: Int, y: Int): T

    fun <T> indexOf(element: T): Pair<Int, Int>

}

val <T> Matrix<T>.size: Int
    get() = this.cols * this.rows


interface MutableMatrix<T> : Matrix<T> {
    operator fun set(x: Int, y: Int, value: T)
}

abstract class AbstractMatrix<out T> : Matrix<T> {

    override fun row(row: Int): List<T> {
        val rowElements: MutableList<T> = mutableListOf()
        forEachIndexed { _, y, value ->
            if (y == row) rowElements += value
        }
        return rowElements
    }

    override fun col(col: Int): List<T> {
        val colElements: MutableList<T> = mutableListOf()
        forEachIndexed { x, _, value ->
            if (x == col) colElements += value
        }
        return colElements
    }


    override fun <T> indexOf(element: T): Pair<Int, Int> {
        var index = Pair(-1,-1)
        forEachIndexed { x, y, value ->
            if (value == element)
                index = Pair(x,y)
                return@forEachIndexed
        }
        return index
    }


    override fun toString(): String {
        val sb = StringBuilder()
        sb.append('[')
        forEachIndexed { x, y, value ->
            if (x == 0)
                sb.append('[')
            sb.append(value.toString())
            if (x == cols - 1) {
                sb.append(']')
                if (y < rows - 1)
                    sb.append(", ")
            } else {
                sb.append(", ")
            }
        }
        sb.append(']')
        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Matrix<*>) return false
        if (rows != other.rows || cols != other.cols) return false

        var eq = true
        forEachIndexed { x, y, value ->
            if (value === null) {
                if (other[x, y] !== null) {
                    eq = false
                    return@forEachIndexed
                }
            } else {
                if (value != other[x, y]) {
                    eq = false
                    return@forEachIndexed
                }
            }
        }
        return eq
    }

    override fun hashCode(): Int {
        var h = 17
        h = h * 39 + cols
        h = h * 39 + rows
        forEach { h = h * 37 + (it?.hashCode() ?: 1) }
        return h
    }
}

internal open class TransposedMatrix<out T>(protected val original: Matrix<T>) : AbstractMatrix<T>() {
    override val cols: Int
        get() = original.rows

    override val rows: Int
        get() = original.cols

    override fun get(x: Int, y: Int): T = original[y, x]
}

internal class TransposedMutableMatrix<T>(original: MutableMatrix<T>) :
        TransposedMatrix<T>(original), MutableMatrix<T> {
    override fun set(x: Int, y: Int, value: T) {
        (original as MutableMatrix<T>)[y, x] = value
    }
}

fun <T> Matrix<T>.asTransposed(): Matrix<T> = TransposedMatrix(this)

fun <T> MutableMatrix<T>.asTransposed(): MutableMatrix<T> = TransposedMutableMatrix(this)

internal open class ListMatrix<T>(override val cols: Int, override val rows: Int,
                                      protected val list: List<T>) :
        AbstractMatrix<T>() {
    override operator fun get(x: Int, y: Int): T = list[y * cols + x]

}

internal class MutableListMatrix<T>(cols: Int, rows: Int, list: MutableList<T>) :
        ListMatrix<T>(cols, rows, list), MutableMatrix<T> {
    override fun set(x: Int, y: Int, value: T) {
        (list as MutableList<T>)[y * cols + x] = value
    }
}

fun <T> matrixOf(cols: Int, rows: Int, vararg elements: T): Matrix<T> {
    return ListMatrix(cols, rows, elements.asList())
}

fun <T> mutableMatrixOf(cols: Int, rows: Int, vararg elements: T): MutableMatrix<T> {
    return MutableListMatrix(cols, rows, elements.toMutableList())
}

private inline fun <T> prepareListForMatrix(cols: Int, rows: Int, init: (Int, Int) -> T): ArrayList<T> {
    val list = ArrayList<T>(cols * rows)
    for (y in 0 until rows) {
        for (x in 0 until cols) {
            list.add(init(x, y))
        }
    }
    return list
}

@Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
inline fun <T> createMatrix(cols: Int, rows: Int, init: (Int, Int) -> T): Matrix<T> {
    return ListMatrix(cols, rows, prepareListForMatrix(cols, rows, init))
}

@Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
inline fun <T> createMutableMatrix(cols: Int, rows: Int, init: (Int, Int) -> T): MutableMatrix<T> {
    return MutableListMatrix(cols, rows, prepareListForMatrix(cols, rows, init))
}

inline fun <T, U> Matrix<T>.mapIndexed(transform: (Int, Int, T) -> U): Matrix<U> {
    return createMatrix(cols, rows) { x, y -> transform(x, y, this[x, y]) }
}

inline fun <T, U> Matrix<T>.map(transform: (T) -> U): Matrix<U> {
    return mapIndexed { _, _, value -> transform(value) }
}

inline fun <T> Matrix<T>.forEachIndexed(action: (Int, Int, T) -> Unit) {
    for (y in 0 until rows) {
        for (x in 0 until cols) {
            action(x, y, this[x, y])
        }
    }
}

inline fun <T> Matrix<T>.forEach(action: (T) -> Unit) {
    forEachIndexed { _, _, value -> action(value) }
}

fun <T> Matrix<T>.toList(): List<T> {
    return prepareListForMatrix(cols, rows) { x, y -> this[x, y] }
}

fun <T> Matrix<T>.toMutableList(): MutableList<T> {
    return prepareListForMatrix(cols, rows) { x, y -> this[x, y] }
}

private fun <T> Iterable<T>.toArrayList(size: Int): ArrayList<T> {
    val list = ArrayList<T>(size)
    val itr = iterator()

    for (i in 0 until size) {
        if (itr.hasNext()) {
            list.add(itr.next())
        } else {
            throw IllegalArgumentException("Not enough elements")
        }
    }
    return list
}

fun <T> Iterable<T>.toMatrix(cols: Int, rows: Int): Matrix<T> {
    val list = toArrayList(cols * rows)
    return ListMatrix(cols, rows, list)
}

fun <T> Iterable<T>.toMutableMatrix(cols: Int, rows: Int): MutableMatrix<T> {
    val list = toArrayList(cols * rows)
    return MutableListMatrix(cols, rows, list)
}

