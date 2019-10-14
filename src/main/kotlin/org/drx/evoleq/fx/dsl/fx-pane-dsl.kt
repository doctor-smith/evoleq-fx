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

import javafx.scene.layout.Pane
import kotlinx.coroutines.CoroutineScope
import org.drx.evoleq.fx.component.FxComponent

/**
 * Pane
 */
@Suppress("unused")
fun <D> FxComponentConfiguration<out Any, *>.fxPane(scope: CoroutineScope = this.scope, configuration: FxComponentConfiguration<Pane, D>.()->Unit): FxComponent<Pane, D> {
    return fxComponent(scope,configuration)
}

@Suppress("unused")
fun <D> fxPane(scope: CoroutineScope = DEFAULT_FX_COMPONENT_SCOPE(),configuration: FxComponentConfiguration<Pane, D>.()->Unit): FxComponent<Pane, D> {
    return fxComponent(scope,configuration)
}