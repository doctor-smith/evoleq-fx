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

import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import org.drx.evoleq.fx.component.FxComponent
@Suppress("unused")
fun <D> Any?.fxTooltip(configuration: FxComponentConfiguration<Tooltip, D>.()->Unit): FxComponent<Tooltip, D> = fxComponent(configuration)

@Suppress("unused")
fun <D> fxTooltip(configuration: FxComponentConfiguration<Tooltip, D>.()->Unit): FxComponent<Tooltip, D> = fxComponent(configuration)

@Suppress("unused")
fun <C : Control, D> FxComponentConfiguration<C, D>.tooltip(component: FxComponent<Tooltip, D>) {
    fxSpecial(component)
    fxRunTime{
        tooltip = component.show()
    }
}