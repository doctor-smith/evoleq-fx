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

import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import org.drx.evoleq.fx.component.FxComponent

@Suppress("unused")
fun <D> FxComponentConfiguration<out Any, *>.fxTabPane(configuration: FxComponentConfiguration<TabPane, D>.()->Unit): FxComponent<TabPane, D> = fxComponent(configuration)

@Suppress("unused")
fun <D> fxTabPane(configuration: FxComponentConfiguration<TabPane, D>.()->Unit): FxComponent<TabPane, D> = fxComponent(configuration)

@Suppress("unused")
fun <D> FxComponentConfiguration<TabPane, D>.tab(component: FxComponent<Tab, D>) {
    fxSpecial(component)
    fxRunTime { tabs.add(component.show()) }
}

@Suppress("unused")
fun <D> FxComponentConfiguration<TabPane, D>.side(side: Side) {
    fxRunTime { this.side = side  }
}

@Suppress("unused")
fun <D> Any?.fxTab(configuration: FxComponentConfiguration<Tab, D>.()->Unit): FxComponent<Tab, D> = fxComponent(configuration)

@Suppress("unused")
fun<D> FxComponentConfiguration<Tab, D>.content(component: FxComponent<out Node,*>) {
    fxSpecial(component)
    fxRunTime { content = component.show() }
}
