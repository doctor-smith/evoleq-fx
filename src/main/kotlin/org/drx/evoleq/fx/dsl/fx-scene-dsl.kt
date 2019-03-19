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
import org.drx.evoleq.fx.component.FxComponent


fun <D> FxComponentConfiguration<Stage, D>.fxScene(configuration: FxComponentConfiguration<Scene, D>.()->Unit): FxComponent<Scene, D> {
    return fxComponent(configuration)
}

/**
 * Scene
 */
fun <P: Parent, D> FxComponentConfiguration<Scene, D>.root(component: FxComponent<P, D>, inject:(P)->Scene = { p -> Scene(p)}) : FxComponentConfiguration<Scene, D> {
    child(component)
    val root = component.show()
    val scene = inject(root)
    view{scene}
    return this
}
fun < D> FxComponentConfiguration<Scene, D>.stylesheet(stylesheet: String) {
    fxRunTime {
        stylesheets.add(stylesheet)
    }
}
fun < D> FxComponentConfiguration<Scene, D>.stylesheets(stylesheets: ArrayList<String>) {
    fxRunTime {
        stylesheets.addAll(stylesheets)
    }
}
