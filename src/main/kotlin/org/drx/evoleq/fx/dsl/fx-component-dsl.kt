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

import javafx.scene.Group
import javafx.scene.Parent
import javafx.scene.layout.*
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.flow.fxComponentFlow
import org.drx.evoleq.fx.phase.FxComponentPhase
import org.drx.evoleq.fx.phase.PhaseLauncher
import org.drx.evoleq.fx.runtime.FxRunTime
import org.drx.evoleq.fx.stub.NoStub
import org.drx.evoleq.fx.stub.Tunnel
import org.drx.evoleq.stub.Stub
import java.lang.Thread.sleep
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

typealias ID = KClass<*>



abstract class FxComponentConfiguration<N, D> :  Configuration<FxComponent<N, D>> {
    //
    lateinit var idConfiguration: KClass<*>
    lateinit var stubConfiguration: Stub<D>
    lateinit var viewConfiguration: ()->N

    var finish: Boolean = false
    var cancel: Boolean = false
    var component: FxComponent<N, D>? = null
    var fxRunTime: FxRunTime<N, D>? = null
    var fxRunTimeView: N? = null

    val launcher = PhaseLauncher<N,D>()

    /**
     *
     */
    override fun configure(): FxComponent<N, D> {
        Parallel<Unit> {
            val l = launcher.launch(this@FxComponentConfiguration)
            val terminate = fxComponentFlow<N, D>().evolve(l).get()
            assert(terminate is FxComponentPhase.Terminate)
        }
        while (!(finish || cancel) ) {
            sleep(1)
        }
        if(cancel) {
            throw Exception()
        }
        val comp = object : FxComponent<N, D> {

            override val id: ID
                get() = idConfiguration
            override val stubs: HashMap<ID, Stub<*>>
                get() = stubConfiguration.stubs

            override fun show(): N  {
                fxRunTimeView = viewConfiguration()
                return fxRunTimeView!!
            }

            override suspend fun evolve(d: D): Evolving<D> = stubConfiguration.evolve(d)

        }
        component = comp
        return comp
    }

    /******************************************************************************************************************
     *
     * Configuration functions
     *
     ******************************************************************************************************************/

    fun FxComponentConfiguration<N, D>.id(id: ID) : ID{
        launcher.id = id
        return id
    }

    inline fun <reified Id> FxComponentConfiguration<N, D>.id() {
        launcher.id = Id::class
    }

    fun FxComponentConfiguration<N, D>.stub(stub: Stub<D>) {
        launcher.stub = stub
    }

    fun FxComponentConfiguration<N, D>.noStub() {
        launcher.stub = NoStub()
    }

    fun FxComponentConfiguration<N, D>.tunnel(){
        launcher.stub = Tunnel()
    }

    fun FxComponentConfiguration<N, D>.view(view: ()->N) {
        launcher.view = view
    }
    fun <M,E> FxComponentConfiguration<N, D>.child(child: FxComponent<M, E>) {
        launcher.fxChildren.add( Parallel { child } )
    }
    fun <M,E> FxComponentConfiguration<N, D>.fxSpecial(child: FxComponent<M, E>) {
        launcher.fxSpecials.add( Parallel{child} )
    }
    fun FxComponentConfiguration<N, D>.stubAction(action: Stub<D>.()->Unit) {
        launcher.stubActions.add(Parallel{action})
    }
    fun FxComponentConfiguration<N, D>.fxRunTime(action: N.()->Unit) = Parallel<Unit>{
        while(fxRunTime == null) {
            kotlinx.coroutines.delay(1)
        }
        fxRunTime!!.fxRunTime(action)
    }
    fun FxComponentConfiguration<N, D>.shutdown() = Parallel<Unit> {
        while(fxRunTime == null) {
            kotlinx.coroutines.delay(1)
        }
        fxRunTime!!.shutdown()
    }
}

fun<N,D> Any?.fxComponent(configuration: FxComponentConfiguration<N,D>.()->Unit): FxComponent<N, D> {
    val conf = (object : FxComponentConfiguration<N, D>(){})
    conf.configuration()
    return conf.configure()
}
/*
fun<N,D> Any?.fxParentComponent(configuration: FxComponentConfiguration<N,D>.()->Unit): FxComponent<N, D> {
    val conf = (object : FxComponentConfiguration<N, D>(){
        public override fun <M,E> FxComponentConfiguration<N, D>.child(child: FxComponent<M,E>) {
            this.child(child)
        }
    })
    conf.configuration()
    return conf.configure()
}
*/
/**
 * Generic
 */
inline fun <reified N : Any, D> FxComponentConfiguration<N, D>.configure(noinline configure: N.()->Unit): N {
    val n = N::class.createInstance()
    n.configure()
    return n
}

/**
 * Generic
 */
fun<V> V.isFxParent(): Boolean = when(this) {
    is Group,
    is Pane -> true
    is Parent -> this.hasModifiableChildren()
    else -> false

}

fun < P: Parent>P.hasModifiableChildren(): Boolean = try {
    this::class.java.getMethod("getChildren") != null
} catch(exception : Exception){ false }





