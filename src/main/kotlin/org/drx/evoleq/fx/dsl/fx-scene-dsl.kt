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

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.drx.evoleq.dsl.parallel
import org.drx.evoleq.fx.component.FxComponent
import java.lang.Thread.sleep

@Suppress("unused")
@EvoleqFxDsl
suspend fun <D> FxComponentConfiguration<Stage, D>.fxScene(scope: CoroutineScope = this.scope, configuration: suspend  FxComponentConfiguration<Scene, D>.()->Unit): FxComponent<Scene, D> {
    return fxComponent(scope,configuration)
}

@Suppress("unused")
@EvoleqFxDsl
suspend fun <D> fxScene(scope: CoroutineScope = DEFAULT_FX_COMPONENT_SCOPE(),configuration: suspend FxComponentConfiguration<Scene, D>.()->Unit): FxComponent<Scene, D> {
    return fxComponent(scope,configuration)
}

/**
 * Scene
 */
@Suppress("unused")
@EvoleqFxDsl
suspend fun <P: Parent, D> FxComponentConfiguration<Scene, D>.root(component: FxComponent<P, D>, inject:(P)->Scene = { p -> Scene(p)}) : FxComponentConfiguration<Scene, D> {
    child(component)
    val root = component.show()
    val scene = inject(root)
    view{scene}
    return this
}

@Suppress("unused")
@EvoleqFxDsl
suspend fun <P: Parent, D> FxComponentConfiguration<Scene, D>.root(component: CoroutineScope.(CoroutineScope)->FxComponent<P, D>, inject:(P)->Scene = { p -> Scene(p)}) : FxComponentConfiguration<Scene, D> {
    var comp: FxComponent<P, D>? = null
    scope.parallel{
        comp = component(this)
    }
    while(comp == null){
        delay(1)
    }
    child(comp!!)
    val root = comp!!.show()
    val scene = inject(root)
    view{scene}
    return this
}

@Suppress("unused")
@EvoleqFxDsl
fun < D> FxComponentConfiguration<Scene, D>.stylesheet(stylesheet: String) {
    fxRunTimeConfig {
        stylesheets.add(stylesheet)
    }
}

@Suppress("unused")
@EvoleqFxDsl
fun < D> FxComponentConfiguration<Scene, D>.stylesheets(stylesheets: ArrayList<String>) {
    fxRunTimeConfig {
        stylesheets.addAll(stylesheets)
    }
}
