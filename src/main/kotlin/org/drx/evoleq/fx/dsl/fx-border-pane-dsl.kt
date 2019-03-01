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
import javafx.scene.layout.Pane
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.fx.component.FxBorderPaneComponent
import org.drx.evoleq.fx.component.FxNodeComponent
import org.drx.evoleq.fx.component.FxPaneComponent
import org.drx.evoleq.fx.component.FxParentComponent
import org.drx.evoleq.stub.Stub
import java.lang.Thread.sleep
import kotlin.reflect.KClass

open class FxBorderPaneComponentConfiguration<B: BorderPane,D> : FxNodeComponentConfiguration<B, D>() {

    private val childComponents = ArrayList<FxNodeComponent<*, *>>()

    fun<N : Node, E> child(component: FxNodeComponent<N, E>) {
        childComponents.add(component)
    }


    var topComponent: FxNodeComponent<*, *>? = null
    var rightComponent: FxNodeComponent<*, *>? = null
    var bottomComponent: FxNodeComponent<*, *>? = null
    var leftComponent: FxNodeComponent<*, *>? = null
    var centerComponent: FxNodeComponent<*, *>? = null

    override fun configure(): FxBorderPaneComponent<B,D>  = object: FxBorderPaneComponent<B, D>(){

        override val bottomComponent: FxNodeComponent<*, *>?
            get() = this@FxBorderPaneComponentConfiguration.bottomComponent

        override val centerComponent: FxNodeComponent<*, *>?
            get() = this@FxBorderPaneComponentConfiguration.centerComponent

        override val leftComponent: FxNodeComponent<*, *>?
            get() = this@FxBorderPaneComponentConfiguration.leftComponent

        override val rightComponent: FxNodeComponent<*, *>?
            get() = this@FxBorderPaneComponentConfiguration.rightComponent

        override val topComponent: FxNodeComponent<*, *>?
            get() = this@FxBorderPaneComponentConfiguration.topComponent

        override val node = viewDef

        override val children = childComponents

        override val id: KClass<*>
            get() = this@FxBorderPaneComponentConfiguration.idDef

        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = stubDef.stubs

        override suspend fun evolve(d: D): Evolving<D> = stubDef.evolve(d)
    }

    fun <N : Node, D> top(component: FxNodeComponent<N,D>) {
        topComponent = component
    }

    fun <N : Node, D> right(component: FxNodeComponent<N,D>) {
        rightComponent = component
    }

    fun <N : Node, D> bottom(component: FxNodeComponent<N,D>) {
        bottomComponent = component
    }

    fun <N : Node, D> left(component: FxNodeComponent<N,D>) {
        leftComponent = component
    }

    fun <N : Node, D> center(component: FxNodeComponent<N,D>) {
        centerComponent = component
    }
}


fun <G : BorderPane, D> fxBorderPane(configuration: FxBorderPaneComponentConfiguration<G, D>.()->Unit) : FxParentComponent<G, D> = configure(configuration) as FxBorderPaneComponent<G,D>