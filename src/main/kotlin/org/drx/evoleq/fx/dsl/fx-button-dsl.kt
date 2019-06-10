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

import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.ButtonBase
import kotlinx.coroutines.CoroutineScope
import org.drx.evoleq.fx.component.FxComponent

/**
 * Button
 */
@Suppress("unused")
fun <D> FxComponentConfiguration<out Any, D>.fxButton(scope: CoroutineScope = this.scope, configuration: FxComponentConfiguration<Button, D>.()->Unit): FxComponent<Button, D> {
    return  fxComponent(scope,configuration)
}

@Suppress("unused")
fun <D> fxButton(scope: CoroutineScope = DEFAULT_FX_COMPONENT_SCOPE,configuration: FxComponentConfiguration<Button, D>.()->Unit): FxComponent<Button, D> {
    return fxComponent(scope,configuration)
}

//fun <D> CoroutineScope.fxButton(configuration: FxComponentConfiguration<Button, D>.()->Unit): CoroutineScope.()->FxComponent<Button, D> = this.fxComponent(configuration)


@Suppress("unused")
fun <B : ButtonBase> B.action(action: ActionEvent.()->Unit): B {
    this.setOnAction{
        it.action()
    }
    return this
}
