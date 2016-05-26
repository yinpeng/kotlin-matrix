package com.ichipsea.kotlin.matrix

interface Matrix<out T> {
    val cols: Int
    val rows: Int

    operator fun get(x: Int, y: Int): T
}

val <T> Matrix<T>.size: Int
    get() = this.cols * this.rows

interface MutableMatrix<T>: Matrix<T> {
    operator fun set(x: Int, y: Int, value: T)
}

abstract class AbstractMatrix<out T>: Matrix<T> {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append('[')
        forEach { x, y, value ->
            if (x === 0)
                sb.append('[')
            sb.append(value.toString())
            if (x===cols-1) {
                sb.append(']')
                if (y < rows-1)
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
        if (rows !== other.rows || cols !== other.cols) return false

        var eq = true
        forEach { x, y, value ->
            if (value === null) {
                if (other[x, y] !== null) {
                    eq = false
                    return@forEach
                }
            } else {
                if (!value.equals(other[x, y])) {
                    eq = false
                    return@forEach
                }
            }
        }
        return eq
    }

    override fun hashCode(): Int {
        var h = 17
        forEach { value -> h = h * 37 + (value?.hashCode() ?: 1)}
        return h
    }
}

private open class TransposedMatrix<out T>(private val original: Matrix<T>): AbstractMatrix<T>() {
    override val cols: Int
        get() = original.rows

    override val rows: Int
        get() = original.cols

    override fun get(x: Int, y: Int): T = original[y, x]
}

private class TransposedMutableMatrix<T>(private val original: MutableMatrix<T>) :
        TransposedMatrix<T>(original), MutableMatrix<T> {
    override fun set(x: Int, y: Int, value: T) {
        original[y, x] = value
    }
}

fun <T> Matrix<T>.transposedView() : Matrix<T> = TransposedMatrix(this)

open class ArrayMatrix<out T>(override val cols: Int, override val rows: Int, private val array: Array<T>) :
        AbstractMatrix<T>() {
    protected fun idx(x: Int, y: Int): Int = y * cols + x

    override operator fun get(x: Int, y: Int): T = array[idx(x, y)]
}

class MutableArrayMatrix<T>(override val cols: Int, override val rows: Int, val array: Array<T>):
        ArrayMatrix<T>(cols, rows, array), MutableMatrix<T> {
    override fun set(x: Int, y: Int, value: T) {
        array[idx(x, y)] = value
    }
}

inline fun <reified T> Matrix<T>.toArray(): Array<T> {
    if (this is MutableArrayMatrix<T>) {
        return array.clone()
    } else {
        return Array(size) { this[it % cols, it / cols] }
    }
}

fun <T> matrixOf(cols: Int, rows: Int, vararg elements: T): Matrix<T> {
    return ArrayMatrix(cols, rows, elements)
}

fun <T> mutableMatrixOf(cols: Int, rows: Int, vararg elements: T): Matrix<T> {
    return MutableArrayMatrix(cols, rows, elements)
}

inline fun <reified T> createMatrix(cols: Int, rows: Int, init: (Int, Int) -> T): Matrix<T> {
    val array = Array(cols * rows) { init(it % cols, it / cols) }
    return ArrayMatrix(cols, rows, array)
}

inline fun <reified T> createMutableMatrix(cols: Int, rows: Int, init: (Int, Int) -> T): MutableMatrix<T> {
    val array = Array(cols * rows) { init(it % cols, it / cols) }
    return MutableArrayMatrix(cols, rows, array)
}

inline fun <T, reified U> Matrix<T>.map(f: (T) -> U): Matrix<U> {
    return createMatrix(cols, rows) { x, y -> f(this[x, y]) }
}

inline fun <T, reified U> Matrix<T>.map(f: (Int, Int, T) -> U): Matrix<U> {
    return createMatrix(cols, rows) { x, y -> f(x, y, this[x, y]) }
}

fun <T> Matrix<T>.forEach(f: (T) -> Unit): Unit {
    for (y in 0..rows-1) {
        for (x in 0..cols-1) {
            f(this[x, y])
        }
    }
}

fun <T> Matrix<T>.forEach(f: (Int, Int, T) -> Unit): Unit {
    for (y in 0..rows-1) {
        for (x in 0..cols-1) {
            f(x, y, this[x, y])
        }
    }
}

