package com.ichipsea.kotlin.matrix

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class SparseMatrixTest: Spek({
    describe("a sparse double matrix") {
        val m = sparseDoubleMatrix(1000, 800) {
            entry(0, 0, 5.5)
            entry(100, 100, 4.5)
            entry(300, 500, 12.3)
        }

        it("has correct dimension") {
            assertEquals(1000, m.cols)
            assertEquals(800, m.rows)
            assertEquals(1000*800, m.size)
        }

        it("has correct zero value") {
            assertEquals(0.0, m.zero)
        }

        it("has correct nonZeroIndices") {
            assertEquals(setOf(0 to 0, 100 to 100, 300 to 500), m.nonZeroIndices)
        }

        it("has correct content") {
            for (x in 0..m.cols-1) {
                for (y in 0..m.rows-1) {
                    assertEquals(
                            when(x to y) {
                                0 to 0 -> 5.5
                                100 to 100 -> 4.5
                                300 to 500 -> 12.3
                                else -> 0.0
                            },
                            m[x, y])
                }
            }
        }

        it("map non-zero") {
            val n = m.mapNonZero { it + 2 }
            assertEquals(7.5, n[0, 0])
            assertEquals(0.0, n[0, 1])
            val o = m.mapIndexedNonZero { x: Int, y: Int, value: Double -> value + x + y }
            assertEquals(204.5, o[100, 100])
            assertEquals(0.0, o[100, 101])
        }

        it("forEach non-zero") {
            val s = mutableSetOf<Double>()
            m.forEachNonZero { s.add(it) }
            assertEquals(mutableSetOf(5.5, 4.5, 12.3), s)
            val s2 = mutableSetOf<Triple<Int, Int, Double>>()
            m.forEachIndexedNonZero { x, y, value -> s2.add(Triple(x, y, value)) }
            assertEquals(mutableSetOf(Triple(0, 0, 5.5), Triple(100, 100, 4.5), Triple(300, 500, 12.3)), s2)
        }
    }

    describe("A mutable sparse String? matrix") {
        val m = mutableSparseMatrix<String>(20, 800) {}

        it("has 0 non-zero elements") {
            assertEquals(0, m.nonZeroIndices.size)
        }

        it("adds new elements") {
            m[10, 100] = "Good"
            m[15, 500] = "Timing"
            assertEquals(2, m.nonZeroIndices.size)
            assertEquals("Good", m[10, 100])
            assertEquals("Timing", m[15, 500])
            assertEquals(null, m[1, 1])
        }

        it("set an elements to zero") {
            m[10, 100] = null
            assertEquals(1, m.nonZeroIndices.size)
            assertEquals(null, m[10, 100])
        }

        it("remove an elements by nonZeroIndices") {
            m.nonZeroIndices.remove(15 to 500)
            assertEquals(null, m[15, 500])
            assertEquals(0, m.nonZeroIndices.size)
        }
    }

    describe("special internal tests") {
        it("has null value as non-zero") {
            val m2 = sparseMatrix<Int?>(100, 100, 1) {
                for (i in 0..99) {
                    entry(i, i, null)
                }
            }
            assertEquals(9900, m2.toList().sumBy { it ?: 0 })
        }
    }
})
