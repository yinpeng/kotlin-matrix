package com.ichipsea.kotlin.matrix

import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

interface SparseMatrix<out T> : Matrix<T> {
    val zero: T
    val nonZeroIndices: Set<Pair<Int, Int>>
}

interface MutableSparseMatrix<T> : SparseMatrix<T>, MutableMatrix<T> {
    override val nonZeroIndices: MutableSet<Pair<Int, Int>>
}

internal open class MapSparseMatrix<out T>(override val cols: Int, override val rows: Int, override val zero: T,
                                        open protected val elements: Map<Pair<Int, Int>, T>) :
        AbstractMatrix<T>(), SparseMatrix<T> {
    override val nonZeroIndices: Set<Pair<Int, Int>>
        get() = elements.keys

    override operator fun get(x: Int, y: Int): T {
        return if (elements.containsKey(x to y))
            elements[x to y] as T
        else
            zero
    }
}

internal class MutableMapSparseMatrix<T>(cols: Int, rows: Int, zero: T,
                                         elements:MutableMap<Pair<Int, Int>, T>) :
        MapSparseMatrix<T>(cols, rows, zero, elements), MutableSparseMatrix<T> {
    override val elements: MutableMap<Pair<Int, Int>, T>
        get() = super.elements as MutableMap<Pair<Int, Int>, T>

    override val nonZeroIndices: MutableSet<Pair<Int, Int>>
        get() = elements.keys

    override fun set(x: Int, y: Int, value: T) {
        if (value == zero) {
            elements.remove(x to y)
        } else {
            elements[x to y] = value
        }
    }
}

class SparseMatrixBuilder<T>(private val cols: Int, private val rows: Int, private val zero: T) {
    private val elements = mutableMapOf<Pair<Int, Int>, T>()


    fun entry(x: Int, y: Int, value: T) {
        if (value == zero) {
            elements.remove(x to y)
        } else {
            elements[x to y] = value
        }
    }

    internal fun build(): SparseMatrix<T> {
        return MapSparseMatrix(cols, rows, zero, elements)
    }

    internal fun buildMutable(): MutableSparseMatrix<T> {
        return MutableMapSparseMatrix(cols, rows, zero, elements)
    }
}

@Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
inline fun <T> sparseMatrix(cols: Int, rows: Int, zero: T,
                            init: SparseMatrixBuilder<T>.() -> Unit): SparseMatrix<T> {
    val builder = SparseMatrixBuilder(cols, rows, zero)
    builder.init()
    return builder.build()
}

@Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
inline fun <T> mutableSparseMatrix(cols: Int, rows: Int, zero: T,
                                   init: SparseMatrixBuilder<T>.() -> Unit): MutableSparseMatrix<T> {
    val builder = SparseMatrixBuilder(cols, rows, zero)
    builder.init()
    return builder.buildMutable()
}

@Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
inline fun <T, U> SparseMatrix<T>.mapIndexedNonZero(newZero: U, transform: (Int, Int, T) -> U): SparseMatrix<U> {
    val builder = SparseMatrixBuilder(cols, rows, newZero)
    nonZeroIndices.forEach {
        builder.entry(it.first, it.second, transform(it.first, it.second, this[it.first, it.second]))
    }
    return builder.build()
}

inline fun <T> SparseMatrix<T>.mapIndexedNonZero(transform: (Int, Int, T) -> T): SparseMatrix<T> {
    return mapIndexedNonZero(zero, transform)
}

inline fun <T, U> SparseMatrix<T>.mapNonZero(newZero: U, transform: (T) -> U): SparseMatrix<U> {
    return mapIndexedNonZero(newZero, { x, y, value -> transform(value) })
}

inline fun <T> SparseMatrix<T>.mapNonZero(transform: (T) -> T): SparseMatrix<T> {
    return mapNonZero(zero, transform)
}

inline fun <T> SparseMatrix<T>.forEachIndexedNonZero(action: (Int, Int, T) -> Unit) {
    nonZeroIndices.forEach {
        action(it.first, it.second, this[it.first, it.second])
    }
}

inline fun <T> SparseMatrix<T>.forEachNonZero(action: (T) -> Unit) {
    forEachIndexedNonZero { x, y, value -> action(value)  }
}
