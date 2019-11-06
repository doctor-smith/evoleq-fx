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
package org.drx.evoleq.fx.dsl

import javafx.scene.shape.CubicCurve
import kotlinx.coroutines.CoroutineScope
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.geometry.Derivation

/* TODO Think about which configuration function shall be used in the future */


/**
 * CubicCurve
 */
@Suppress("unused")
fun <D> FxComponentConfiguration<out Any, *>.fxCubicCurve(scope: CoroutineScope = this.scope, configuration: FxComponentConfiguration<CubicCurve, D>.()->Unit): FxComponent<CubicCurve,D> {
    return fxComponent(scope,configuration)
}

/**
 *
 */
@Suppress("unused")
fun <D> fxCubicCurve(scope: CoroutineScope = DEFAULT_FX_COMPONENT_SCOPE(),configuration: FxComponentConfiguration<CubicCurve, D>.()->Unit): FxComponent<CubicCurve,D> {
    return fxComponent(scope,configuration)
}

fun <D> FxComponentConfiguration<CubicCurve, D>.startVelocity(configuration: Point2DConfiguration.()->Unit) {
    val velocity = point(configuration)
    fxRunTimeConfig {
        controlX1 = startX + velocity.x / 3
        controlY1 = startY + velocity.y / 3
    }
}

fun <D> FxComponentConfiguration<CubicCurve, D>.endVelocity(configuration: Point2DConfiguration.()->Unit) {
    val velocity = point(configuration)
    fxRunTimeConfig {
        controlX1 = endX - velocity.x / 3
        controlY1 = endY - velocity.y / 3
    }
}

fun CubicCurve.startVelocity(configuration: Point2DConfiguration.()->Unit) {
    val velocity = point(configuration)
    controlX1 = startX + velocity.x / 3
    controlY1 = startY + velocity.y / 3
}

fun CubicCurve.endVelocity(configuration: Point2DConfiguration.()->Unit) {
    val velocity = point(configuration)
    controlX1 = endX - velocity.x / 3
    controlY1 = endY - velocity.y / 3
}

fun CubicCurve.start(derivation: Derivation) {

    startX = derivation.x.value
    startY = derivation.y.value

    controlX1 = startX + derivation.dX.value / 3
    controlY1 = startY + derivation.dY.value / 3

    controlX1Property().bind(startXProperty().add(derivation.dX.divide(3.0) ) )
    controlY1Property().bind(startYProperty().add(derivation.dY.divide(3.0) ) )

    startXProperty().bind(derivation.x)
    startYProperty().bind(derivation.y)

}

fun CubicCurve.end(derivation: Derivation) {

    endX = derivation.x.value
    endY = derivation.y.value

    controlX2 = endX - derivation.dX.value / 3
    controlY2 = endY - derivation.dY.value / 3

    controlX2Property().bind(endXProperty().subtract(derivation.dX.divide(3.0) ) )
    controlY2Property().bind(endYProperty().subtract(derivation.dY.divide(3.0) ) )

    endXProperty().bind(derivation.x)
    endYProperty().bind(derivation.y)

}