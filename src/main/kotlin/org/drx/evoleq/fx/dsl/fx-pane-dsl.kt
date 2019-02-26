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
import javafx.scene.layout.Pane
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.fx.component.FxNodeComponent
import org.drx.evoleq.fx.component.FxPaneComponent
import org.drx.evoleq.fx.component.FxParentComponent
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass


open class FxPaneComponentConfiguration<G : Pane, D> : FxNodeComponentConfiguration<G, D>() {

    private val childComponents = ArrayList<FxNodeComponent<*, *>>()

    fun<N : Node, E> child(component: FxNodeComponent<N, E>) {
        childComponents.add(component)
    }

    override fun configure(): FxPaneComponent<G, D> = object: FxPaneComponent<G, D>(){

        init{ waitForData() }

        override val node = viewDef

        override val children = childComponents

        override val id: KClass<*>
            get() = this@FxPaneComponentConfiguration.idDef

        //override fun show(): N = viewDef

        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = stubDef.stubs

        override suspend fun evolve(d: D): Evolving<D> = stubDef.evolve(d)
    }
}

fun <G : Pane, D> fxPane(configuration: FxPaneComponentConfiguration<G, D>.()->Unit) : FxParentComponent<G, D> = configure(configuration) as FxPaneComponent<G,D>
