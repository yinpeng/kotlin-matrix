package com.ichipsea.kotlin.matrix

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class MatrixTest: Spek({
    describe("a character matrix") {
        val m = createMatrix(2, 3) { x, y -> if (x===0) 'A' else 'B' }

        it("should return a matrix with 2 cols and 3 rows") {
            assertEquals(2, m.cols)
            assertEquals(3, m.rows)
        }

        it("size should be 6") {
            assertEquals(6, m.size)
        }

        it("should have 'A' in first column and 'B' in second column") {
            m.forEach { x, y, value ->
                assertEquals(if (x===0) 'A' else 'B', value)
            }
        }

        it("should equal to another matrix with same contents") {
            assertEquals(
                    ListMatrix(2, 3, "ABABAB".toList()),
                    m
            )
        }

        describe("transpose view") {
            val t = m.transposedView()
            it ("transpose view should have 3 cols and 2 rows") {
                assertEquals(3, t.cols)
                assertEquals(2, t.rows)
            }

            it("transpose view should have 'A' in first row and 'B' in second row") {
                t.forEach { x, y, value ->
                    assertEquals(if (y === 0) 'A' else 'B', value)
                }
            }
        }
    }

    describe("matrix numeric operations") {
        val a = matrixOf(3, 2,
                1, 2, 3,
                4, 5, 6
        )
        val b = matrixOf(2, 4,
                3, 7,
                2, 2,
                1, 0,
                9, 5
        )

        it("cross product should be correct") {
            assertEquals(
                    matrixOf(3, 4,
                            31.0, 41.0, 51.0,
                            10.0, 14.0, 18.0,
                            1.0, 2.0, 3.0,
                            29.0, 43.0, 57.0
                    ),
                    a x b
            )
        }
    }

})

