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
import kotlinx.coroutines.withTimeout
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.StubConfiguration
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.FxNodeComponent
import org.drx.evoleq.fx.evolving.ParallelFx
import org.drx.evoleq.stub.Stub
import java.lang.Thread.sleep
import kotlin.Exception
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance


open class FxNodeComponentConfiguration< N: Node, D> : Configuration<FxNodeComponent<N, D>> {

    lateinit var viewConfiguration: ()->N
    lateinit var stubConfiguration: Stub<D>
    lateinit var idConfiguration: KClass<*>

    var fxRunTimeAction: N.()->Unit = {}

    // data related to configuration process
    // states
    var viewReady : Boolean = false
    var stubReady : Boolean = false
    var idSet: Boolean = false
    var usingStub: Boolean = false
    // timeouts
    val stubTimeout: Long = 1_000
    val viewTimeout: Long = 1_000
    val idTimeout: Long = 1_000

    lateinit var component: FxNodeComponent<N,D>
    val componentInitializedTimeout: Long = 1_000

    init{
        Parallel<Unit> {
            // stub interface provides a default impl
            try {
                withTimeout(stubTimeout) {
                    while (!(::stubConfiguration.isInitialized)) {
                        kotlinx.coroutines.delay(1)
                    }
                    stubReady = true
                }
            } catch (exception: Exception) {
            }
        }
        //  ::idConfiguration.isInitialized
        Parallel<Unit> {
            withTimeout(stubTimeout) {
                while (!::idConfiguration.isInitialized) {
                    kotlinx.coroutines.delay(idTimeout)
                }
                idSet = true
            }
        }


        Parallel<Unit> {
            // cannot use gui, without viewConfiguration being initialized
            withTimeout(viewTimeout) {
                while (!::viewConfiguration.isInitialized) {
                    kotlinx.coroutines.delay(1)
                }
                viewReady = true
            }
        }

    }


    override fun configure(): FxNodeComponent<N, D> = object: FxNodeComponent<N, D> {
        init{
            if(!usingStub) {
                //println("not using stub !!!")
                stubConfiguration = object: Stub<D>{
                    override val id: KClass<*>
                        get() = this@FxNodeComponentConfiguration::class
                    override val stubs: HashMap<KClass<*>, Stub<*>>
                        get() = HashMap()
                }
                //stubReady = true
                //idSet = true
            } else {
                //println("using configured stub !!!")
            }
            component = this
        }
        override val id: KClass<*>
            get() = this@FxNodeComponentConfiguration.idConfiguration

        override fun show(): N = viewConfiguration()

        override val stubs: HashMap<KClass<*>, Stub<*>>
            get() = stubConfiguration.stubs

        //override fun fxRunTime(N.() -> Unit)fxRunTimeAction

        override suspend fun evolve(d: D): Evolving<D> = stubConfiguration.evolve(d)
    }

    fun view(conf : FxNodeLazyConfiguration<N>.()->Unit) {
        viewConfiguration =  configure(conf)
    }

    fun stub(conf: StubConfiguration<D>.()->Unit) {
        usingStub = true
        stubConfiguration = configure(conf)
        idConfiguration = stubConfiguration.id
    }

    fun stub(stub: Stub<D>) {
        usingStub = true
        stubConfiguration = stub
        idConfiguration = stub.id
    }

    fun whenViewIsReady(postConfigure: N.()->N): Parallel<Boolean> = Parallel{
        while(!::viewConfiguration.isInitialized){
            delay(1)
        }
        viewConfiguration = {viewConfiguration().postConfigure()}
        true
    }

    fun fxRunTime(perform: N.()->Unit) : Parallel<Unit> = Parallel {
        withTimeout(componentInitializedTimeout) {
            while (!::component.isInitialized) {
                delay(1)
            }
            component.fxRunTime(perform)
        }
    }

    fun whenStubIsReady(postConfigure: Stub<D>.()->Unit): Parallel<Boolean> = Parallel {
        //try{
            withTimeout(stubTimeout) {
                while (!(stubReady )) { //&& idSet
                    delay(1)
                }
                stubConfiguration.postConfigure()
                true
            }
        //} catch(exception: Exception) {
        //    cancel(false).get()
        //}
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

