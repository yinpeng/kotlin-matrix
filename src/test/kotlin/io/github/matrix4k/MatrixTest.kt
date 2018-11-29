package io.github.matrix4k

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

@RunWith(JUnitPlatform::class)
class MatrixTest: Spek({
    describe("a character matrix") {
        val m = createMatrix(2, 3) { x, _ -> if (x == 0) 'A' else 'B' }

        it("should return a matrix with 2 cols and 3 rows") {
            assertEquals(2, m.cols)
            assertEquals(3, m.rows)
        }

        it("size should be 6") {
            assertEquals(6, m.size)
        }

        it("should have 'A' in first column and 'B' in second column") {
            m.forEachIndexed { x, _, value ->
                assertEquals(if (x == 0) 'A' else 'B', value)
            }
        }

        it("should be correct") {
            assertEquals(
                    "ABABAB".toList().toMatrix(2, 3),
                    m
            )
            assertNotEquals(
                    "ABABAB".toList().toMatrix(3, 2),
                    m
            )
        }

        it("should convert to correct string") {
            assertEquals(
                    "[[A, B], [A, B], [A, B]]",
                    m.toString()
            )
        }

        describe("as transposed") {
            val t = m.asTransposed()
            it ("transpose view should have 3 cols and 2 rows") {
                assertEquals(3, t.cols)
                assertEquals(2, t.rows)
            }

            it("transpose view should have 'A' in first row and 'B' in second row") {
                t.forEachIndexed { _, y, value ->
                    assertEquals(if (y == 0) 'A' else 'B', value)
                }
            }
        }
    }

    describe("a mutable Pair<Int, Double> matrix") {
        val m = mutableMatrixOf(4, 2,
                3 to 3.1, 5 to 8.0, 6 to 2.2, 1 to 3.5,
                0 to 7.0, 0 to 1.8, 9 to 1000.35, 972345 to 11.3
        )

        it("should return a matrix with 4 cols and 2 rows") {
            assertEquals(4, m.cols)
            assertEquals(2, m.rows)
        }

        it ("size should be 8") {
            assertEquals(8, m.size)
        }

        it ("should be mutable") {
            val old = m[2, 1]
            val new = 100 to 0.0
            m[2, 1] = new
            assert(m[2, 1] === new)
            m[2, 1] = old
            assert(m[2, 1] === old)
        }

        it ("should be mutable by asTransposed()") {
            val old = m[3, 0]
            val new = 888 to 111.0
            m.asTransposed()[0, 3] = new
            assert(m[3, 0] === new)
            m[3, 0] = old
            assert(m.asTransposed()[0, 3] === old)
        }
    }

    describe("equation and hash code") {
        val m1 = (1..6).toMatrix(3, 2)
        val m2 = (1..6).toMutableMatrix(3, 2)
        val m3 = matrixOf(3, 2, 1, 2, 3, 4, 5, null)
        val m4 = matrixOf(3, 2, 1, 2, 3, 4, 5, null)

        it("equation test") {
            assertEquals(m1, m2)
            assertEquals(m3, m4)
            assertNotEquals(m1, m3)
            assertNotEquals(m1, m1.toList().toMatrix(2, 3))
            val m = m1.toList().toMutableMatrix(3, 2)
            assertEquals(m1, m)
            m[0, 0] += 5
            assertNotEquals(m1, m)
            m[0, 0] -= 5
            assertEquals(m1, m)
        }

        it("have same hash codes for equal matrices") {
            assertEquals(m1.hashCode(), m2.hashCode())
            assertEquals(m3.hashCode(), m4.hashCode())
        }
    }

    describe("conversions") {
        val mm = createMutableMatrix(2, 2) { x, y -> x + y * 10 }

        val itr: Iterable<Int> = 0..9

        it("should convertible from Matrix to List") {
            val l = mm.toList()
            assertEquals(listOf(0, 1, 10, 11), l)
        }

        it("should convertible from Matrix to MutableList") {
            val l = mm.toMutableList()
            assertEquals(mutableListOf(0, 1, 10, 11), l)
            val old = mm[0, 0]
            l[0] = mm[0, 0] + 123
            assertEquals(old, mm[0, 0], "it should not change the original matrix through the mutable list")
        }

        it("should be convertible from Iterable to Matrix") {
            val m = itr.toMatrix(5, 2)
            assertEquals(m, matrixOf(5, 2, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
            assertFails { itr.toMatrix(6, 2) }
        }

        it("should be convertible from Iterable to MutableMatrix") {
            val m = itr.toMutableMatrix(5, 2)
            assertEquals(m, mutableMatrixOf(5, 2, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
            m[0, 0] = 10000
            assertFalse(itr.contains(10000), "it should not change the original Iterable through the mutable matrix)")
        }
    }

    describe("getters") {
        val m = matrixOf(2, 3, 1, 2, 3, 4, 5, 6)

        it("should get rows and columns") {
            val firstRow = listOf(1, 2)
            val secondRow = listOf(3, 4)
            val thirdRow = listOf(5, 6)
            val firstColumn = listOf(1, 3, 5)
            val secondColumn = listOf(2, 4, 6)
            assertEquals(firstRow, m[0])
            assertEquals(firstRow, m.getRow(0))
            assertEquals(secondRow, m[1])
            assertEquals(secondRow, m.getRow(1))
            assertEquals(thirdRow, m[2])
            assertEquals(thirdRow, m.getRow(2))
            assertEquals(firstColumn, m.getColumn(0))
            assertEquals(secondColumn, m.getColumn(1))
        }
    }

    describe("map and forEach") {
        val m = matrixOf(2, 3, 1, 2, 3, 4, 5, 6)

        it("test map()") {
            assertEquals(matrixOf(2, 3, "1", "2", "3", "4", "5", "6"),
                    m.map { it.toString() } )
            assertEquals(createMatrix(2, 3) { x, y -> m[x, y] + x + y },
                    m.mapIndexed { x, y, value -> value + x + y } )
        }

        it("test forEach()") {
            var s = 0
            m.forEach {
                s += it
            }
            assertEquals(21, s)
            m.forEachIndexed { x, y, value ->
                if (x == y) s -= value
            }
            assertEquals(16, s)
        }
    }

    describe("matrix numeric operations") {
        val a = (1..6).toMatrix(3, 2)

        val b = createMatrix(3, 2) { _, _ -> 0.5 }

        val c = matrixOf(2, 4,
                3, 7,
                2, 2,
                1, 0,
                9, 5
        )

        it("addition") {
            assertEquals(
                    matrixOf(3, 2,
                            1.5, 2.5, 3.5,
                            4.5, 5.5, 6.5
                    ),
                    a + b
            )
            assertFails { a + c }
        }

        it("subtraction") {
            assertEquals(
                    matrixOf(3, 2,
                            0.5, 1.5, 2.5,
                            3.5, 4.5, 5.5
                    ),
                    a - b
            )
            assertFails { a - c }
        }

        it("multiplication by number") {
            assertEquals(
                    matrixOf(3, 2,
                            2.0, 4.0, 6.0,
                            8.0, 10.0, 12.0
                    ),
                    a * 2.0
            )
            assertEquals(c * 0.8, 0.8 * c )
        }

        it("dot product") {
            assertEquals(
                    matrixOf(3, 2,
                            0.5, 1.0, 1.5,
                            2.0, 2.5, 3.0
                    ),
                    a * b
            )
            assertFails { a * c }
        }

        it("cross product") {
            assertEquals(
                    matrixOf(3, 4,
                            31.0, 41.0, 51.0,
                            10.0, 14.0, 18.0,
                            1.0, 2.0, 3.0,
                            29.0, 43.0, 57.0
                    ),
                    a x c
            )
            assertFails { a x b }
        }

        it("matrix multiplication") {
            assertEquals(
                    matrixOf(2, 2,
                            3.0, 3.0,
                            7.5, 7.5
                    ),
                    a dot b.asTransposed()
            )
            assertFails { a dot c }
        }
    }

})

