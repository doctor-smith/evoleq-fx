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
import kotlinx.coroutines.CoroutineScope
import org.drx.evoleq.dsl.ArrayListConfiguration
import org.drx.evoleq.fx.component.FxComponent

@Suppress("unused")
@EvoleqFxDsl
suspend fun <D> FxComponentConfiguration<out Any, *>.fxTabPane(scope: CoroutineScope = this.scope, configuration: suspend FxComponentConfiguration<TabPane, D>.()->Unit): FxComponent<TabPane, D> = fxComponent(scope,configuration)

@Suppress("unused")
@EvoleqFxDsl
suspend fun <D> fxTabPane(scope: CoroutineScope = DEFAULT_FX_COMPONENT_SCOPE(),configuration: suspend FxComponentConfiguration<TabPane, D>.()->Unit): FxComponent<TabPane, D> = fxComponent(scope,configuration)

@Suppress("unused")
@EvoleqFxDsl
suspend fun <D, E> FxComponentConfiguration<TabPane, D>.tab(index: Int,component: FxComponent<Tab, E>) {
    fxSpecial(component)
    fxRunTimeConfig(index) { tabs.add(component.show()) }
}

@Suppress("unused")
@EvoleqFxDsl
suspend fun <D, E> FxComponentConfiguration<TabPane, D>.tab(component: FxComponent<Tab, E>) {
    fxSpecial(component)
    fxRunTimeConfig { tabs.add(component.show()) }
}

@Suppress("unused")
@EvoleqFxDsl
fun <D> FxComponentConfiguration<TabPane, D>.side(side: Side) {
    fxRunTimeConfig { this.side = side  }
}

@Suppress("unused")
@EvoleqFxDsl
suspend fun <D> CoroutineScope.fxTab(configuration: suspend FxComponentConfiguration<Tab, D>.()->Unit): FxComponent<Tab, D> = fxComponent(this,configuration)

@Suppress("unused")
@EvoleqFxDsl
suspend fun <D, E> FxComponentConfiguration<TabPane, D>.fxTab(configuration: suspend FxComponentConfiguration<Tab, E>.()->Unit): FxComponent<Tab, E> = fxComponent(scope,configuration)

@Suppress("unused")
@EvoleqFxDsl
suspend fun <D> FxComponentConfiguration<TabPane, D>.tabs(configuration: ArrayListConfiguration<FxComponent<Tab,*>>.()->Unit) {
    val tabs = org.drx.evoleq.dsl.configure(configuration)
    tabs.forEach {
        fxSpecial(it)
    }
    fxRunTime{
        this.tabs.addAll(tabs.map{it.show()})
    }
}

@Suppress("unused")
@EvoleqFxDsl
suspend fun <D> ArrayListConfiguration<FxComponent<Tab,D>>.tab(tab: FxComponent<Tab, D>) = item(tab)

@Suppress("unused")
@EvoleqFxDsl
suspend fun<D> FxComponentConfiguration<Tab, D>.content(component: FxComponent<out Node,*>) {
    fxSpecial(component)
    fxRunTimeConfig { content = component.show() }
}
