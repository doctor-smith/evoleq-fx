package org.drx.evoleq.fx.geometry

import javafx.beans.property.SimpleDoubleProperty

/**
 * Derivation
 */
data class Derivation(
    val x0: Number,
    val y0: Number,
    val dX0: Number = 0.0,
    val dY0: Number = 0.0
) {
    val x: SimpleDoubleProperty by lazy { SimpleDoubleProperty() }
    val y: SimpleDoubleProperty by lazy { SimpleDoubleProperty() }
    val dX: SimpleDoubleProperty by lazy { SimpleDoubleProperty() }
    val dY: SimpleDoubleProperty by lazy { SimpleDoubleProperty() }

    init{
        x.value = x0.toDouble()
        y.value = y0.toDouble()
        dX.value = dX0.toDouble()
        dY.value = dY0.toDouble()
    }
}

fun Derivation.scale(lambda: Number): Derivation {

    dX.value = lambda.toDouble() * dX.value
    dY.value = lambda.toDouble() * dY.value

    return this
}
