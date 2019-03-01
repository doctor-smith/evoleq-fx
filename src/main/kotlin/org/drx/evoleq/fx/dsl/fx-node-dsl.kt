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
import kotlin.reflect.full.createInstance


open class FxNodeComponentConfiguration< N: Node, D> : Configuration<FxNodeComponent<N, D>> {

    lateinit var viewDef: ()->N
    lateinit var stubDef: Stub<D>
    lateinit var idDef: KClass<*>


    override fun configure(): FxNodeComponent<N, D> = object: FxNodeComponent<N, D> {
        init{
            waitForData()
        }

        override val id: KClass<*>
            get() = this@FxNodeComponentConfiguration.idDef

        override fun show(): N = viewDef()

        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = stubDef.stubs

        override suspend fun evolve(d: D): Evolving<D> = stubDef.evolve(d)
    }

    fun waitForData(){
        GlobalScope.launch{
            while (!(::viewDef.isInitialized &&
                     ::stubDef.isInitialized &&
                     ::idDef.isInitialized)) {
                delay(1)
            }
        }
    }

    fun view(conf : FxNodeLazyConfiguration<N>.()->Unit) {
        viewDef =  configure(conf)
    }

    fun stub(conf: StubConfiguration<D>.()->Unit) {
        stubDef = configure(conf)
        idDef = stubDef.id
    }

    fun stub(stub: Stub<D>) {
        stubDef = stub
        idDef = stub.id
    }



}
fun <N: Node, D> fxNode(configuration: FxNodeComponentConfiguration<N,D>.()->Unit) = configure(configuration)

open class FxNodeLazyConfiguration<N : Node> : Configuration<()->N> {

    //lateinit var view : N
    lateinit var configure: () -> N
    private  var style: String? = null

    override fun configure(): ()->N = configure//{view}

    fun node(node: N, configuration: N.()->Unit = {}) {
        configure = {
            node.configuration()
            node.style()// = style
            //this.view = node
            node
        }
    }

    inline fun <reified M : N> node(noinline configuration: M.()->Unit = {}){
        configure = {
            val node = M::class.createInstance()
            node.configuration()

            node.style() // = style
            node
        }
    }
    fun N.style() {
        GlobalScope.launch {
            while(this@FxNodeLazyConfiguration.style == null) {
                delay(1)
            }
            this@style.style = this@FxNodeLazyConfiguration.style
            this@style.applyCss()
        }
    }
    fun style(css: String) { style = css }


}

//open class LazyFxNodeConfiguration<N: Node, D> : Configuration<FxNodeComponent<N,D>> {

//}




fun Button.action(action: ActionEvent.()->Unit): Button {
    this.setOnAction{
        it.action()
    }
    return this
}

