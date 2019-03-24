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

import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.WindowEvent
import org.drx.evoleq.fx.component.FxComponent

/**
 * Stage
 */
@Suppress("unused")
fun <D> Any?.fxStage(configuration: FxComponentConfiguration<Stage, D>.()->Unit): FxComponent<Stage, D> = fxComponent ( configuration )

fun <D> fxStage(configuration: FxComponentConfiguration<Stage, D>.()->Unit): FxComponent<Stage, D> = fxComponent ( configuration )

fun <D> FxComponentConfiguration<Stage, D>.scene(component: FxComponent<Scene, D>): FxComponentConfiguration<Stage, D> {
    child(component)
    fxRunTime { scene = component.show() }
    return this
}


