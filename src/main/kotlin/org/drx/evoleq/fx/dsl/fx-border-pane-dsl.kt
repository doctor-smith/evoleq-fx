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

import javafx.scene.Node
import javafx.scene.layout.BorderPane
import kotlinx.coroutines.CoroutineScope
import org.drx.evoleq.fx.component.FxComponent

/**
 * BorderPane
 */
@Suppress("unused")
fun <D> FxComponentConfiguration<out Any, *>.fxBorderPane(scope: CoroutineScope = this.scope, configuration: FxComponentConfiguration<BorderPane,D>.()->Unit): FxComponent<BorderPane, D> {
    return fxComponent(scope,configuration)
}
@Suppress("unused")
fun <D> fxBorderPane(scope: CoroutineScope = DEFAULT_FX_COMPONENT_SCOPE(),configuration: FxComponentConfiguration<BorderPane,D>.()->Unit): FxComponent<BorderPane, D> {
    return fxComponent(scope,configuration)
}
@Suppress("unused")
fun <C : Node, D> FxComponentConfiguration<BorderPane, D>.top(component: FxComponent<C, D>)  {
    fxSpecial( component )
    fxRunTime {
        top =  component.show()
    }
}
@Suppress("unused")
fun <C : Node, D> FxComponentConfiguration<BorderPane, D>.bottom(component: FxComponent<C, D>)  {
    fxSpecial( component )
    fxRunTime {
        bottom =  component.show()
    }
}
@Suppress("unused")
fun <C : Node, D> FxComponentConfiguration<BorderPane, D>.left(component: FxComponent<C, D>)  {
    fxSpecial( component )
    fxRunTime {
        left =  component.show()
    }
}
@Suppress("unused")
fun <C : Node, D> FxComponentConfiguration<BorderPane, D>.right(component: FxComponent<C, D>)  {
    fxSpecial( component )
    fxRunTime {
        right =  component.show()
    }
}
@Suppress("unused")
fun <C : Node, D> FxComponentConfiguration<BorderPane, D>.center(component: FxComponent<C, D>)  {
    fxSpecial( component )
    fxRunTime {
        center =  component.show()
    }
}
