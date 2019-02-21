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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.StubConfiguration
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass

class FxComponentConfiguration< N: Node, D> : Configuration<FxComponent<N, D>> {

    lateinit var view: N
    lateinit var stub: Stub<D>
    lateinit var id: KClass<*>

    override fun configure(): FxComponent<N,D> = object: FxComponent<N,D>{
        override val id: KClass<*>
            get() = this@FxComponentConfiguration.id

        override fun show(): N = view

        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = stub.stubs

        override suspend fun evolve(d: D): Evolving<D> = stub.evolve(d)
    }


    fun view(conf : FxNodeConfiguration<N>.()->Unit) {
          view = configure(conf)
    }

    fun stub(conf: StubConfiguration<D>.()->Unit) {
        stub = configure(conf)
    }

    //fun<M : Node, E> child()


}

fun <N: Node, D> fxComponent(configuration: FxComponentConfiguration<N,D>.()->Unit) = configure(configuration)

open class FxNodeConfiguration<N : Node> : Configuration<N> {

    lateinit var view : N

    override fun configure(): N = view

    fun node(node: N, configure: N.()->Unit = {}) {
        node.configure()
        this.view = node
    }

    fun style(css: String) {
        GlobalScope.launch {
            while (!::view.isInitialized) {
                delay(1)
            }
            view.style = css
            view.applyCss()
        }
    }


}