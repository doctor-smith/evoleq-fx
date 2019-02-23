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
import javafx.scene.Node
import javafx.scene.control.Button
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.StubConfiguration
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.fx.component.FxNodeComponent
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass


open class FxNodeComponentConfiguration< N: Node, D> : Configuration<FxNodeComponent<N, D>> {

    lateinit var view: N
    lateinit var stub: Stub<D>
    lateinit var id: KClass<*>

    override fun configure(): FxNodeComponent<N, D> = object: FxNodeComponent<N, D> {
        override val id: KClass<*>
            get() = this@FxNodeComponentConfiguration.id

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
        id = stub.id
    }



}
fun <N: Node, D> fxNode(configuration: FxNodeComponentConfiguration<N,D>.()->Unit) = configure(configuration)

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





fun Button.action(action: ActionEvent.()->Unit): Button {
    this.setOnAction{
        it.action()
    }
    return this
}

