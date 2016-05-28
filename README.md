# kotlin-matrix
A matrix library for Kotlin language

## Status
Under development, API may change.

## Getting Started

### Create a matrix

```kotlin
// Use literals
val m1 = matrixOf(4, 3,         // numbers of columns and rows
                3, 5, 6, 9,     // elements in the matrix
                8, 8, 2, 4,
                0, 5, 5, 8
)

// By lambdas
val m2 = createMatrix(4, 3) { x, y -> "$x-$y" }

// From Iterable
val m3 = (1..100).toMatrix(20, 5)

// Mutable matrices
val m4 = mutableMatrixOf(2, 2, 1.5, 2.3, 4.4, 3.6)
val m5 = createMutableMatrix(100, 100) { x, y -> x * y }
val m6 = ('A'..'Z').toMutableMatrix(2, 13)
```

### Access elements in a matrix

```kotlin
val m = (1..12).toMutableMatrix(4, 3)
val a = m[0, 0]         // index start from 0
m[2, 2] = 5             // change an element of the mutable matrix
```

### Use *map* to transform a matrix

```kotlin
val m1 = (1..100).toMatrix(20, 5)
val m2 = m1.map { it / 2.0 }
val m3 = m1.mapIndexed { x, y, value -> "${'A'+x-1}${y+1}: $value" }
```

### Use *forEach* to apply actions to all elements

```kotlin
val m1 = (1..12).toMatrix(4, 3)
m1.forEach {
    println(it)
}

val m2 = createMatrix(9, 9) { x, y -> (x+1)*(y+1) }
m2.forEachIndexed { x, y, value ->
    println("$x*$y=$value")
}
```

### The transposed matrix

```kotlin
val m1 = "ABCDEF".toList().toMatrix(3, 2)
val m2 = m1.asTransposed()
```

### Calculations (for matrices with number elements)

```kotlin
val a = (1..10).toMatrix(5, 2)
val b = createMatrix(5, 2) { x, y -> 0.5 }
println(a + b)
println(a - b)
println(a * b)      // dot product
println(2 * b + a)
val c = (1..6).toMatrix(2, 3)
println(a x c)      // cross product
```
