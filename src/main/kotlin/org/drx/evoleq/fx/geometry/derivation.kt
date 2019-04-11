/**
 * Copyright (c) 2019 Dr. Florian Schmidt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
