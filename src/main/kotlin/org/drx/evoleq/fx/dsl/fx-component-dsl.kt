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
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.sendBlocking
import org.drx.evoleq.dsl.ArrayListConfiguration
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.HashMapConfiguration
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.application.IdProvider
import org.drx.evoleq.fx.application.PreId
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.component.FxNoStubComponent
import org.drx.evoleq.fx.component.FxTunnelComponent
import org.drx.evoleq.fx.exception.FxConfigurationException
import org.drx.evoleq.fx.flow.fxComponentFlow
import org.drx.evoleq.fx.phase.FxComponentPhase
import org.drx.evoleq.fx.phase.ComponentPhaseLauncher
import org.drx.evoleq.fx.runtime.FxRunTime
import org.drx.evoleq.fx.stub.NoStub
import org.drx.evoleq.fx.stub.Tunnel
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.time.Change
import org.drx.evoleq.time.happen
import java.lang.Thread.sleep
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

typealias ID = KClass<*>



abstract class FxComponentConfiguration<N, D> :  Configuration<FxComponent<N, D>> {
    // Component Data
    lateinit var idConfiguration: ID
    lateinit var stubConfiguration: Stub<D>
    lateinit var viewConfiguration: ()->N

    // Data related to configuration process
    val launcher = ComponentPhaseLauncher<N,D>()
    var fxRunTime: FxRunTime<N, D>? = null

    var finish: Boolean = false
    var cancel: Boolean = false
    var component: FxComponent<N, D>? = null

    var fxRunTimeView: N? = null

    /* TODO improve id-provider-stuff */
    val idProvider: SendChannel<Change<ID>> = IdProvider
    private var keepIdProvider = true

    private val properties: HashMap<String, Any?> by lazy { HashMap<String, Any?>() }



    override fun configure(): FxComponent<N, D> = when(stubConfiguration) {
        is Tunnel<D> ->  object : FxTunnelComponent<N, D> {

            init{
                if(!keepIdProvider) {
                    idProvider.close()
                }
            }

            override val id: ID
                get() = idConfiguration

            override val stubs: HashMap<ID, Stub<*>>
                get() = stubConfiguration.stubs

            override fun show(): N {
                fxRunTimeView = viewConfiguration()
                return fxRunTimeView!!
            }

            override suspend fun evolve(d: D): Evolving<D> = stubConfiguration.evolve(d)
        }
        is NoStub<D> ->  object : FxNoStubComponent<N, D> {
            init{
                if(!keepIdProvider) {
                    idProvider.close()
                }
            }
            override val id: ID
                get() = idConfiguration

            override val stubs: HashMap<ID, Stub<*>>
                get() = stubConfiguration.stubs

            override fun show(): N {
                fxRunTimeView = viewConfiguration()
                return fxRunTimeView!!
            }

            override suspend fun evolve(d: D): Evolving<D> = stubConfiguration.evolve(d)
        }
        else ->  object : FxComponent<N, D> {
            init{
                if(!keepIdProvider) {
                    idProvider.close()
                }
            }
            override val id: ID
                get() = idConfiguration

            override val stubs: HashMap<ID, Stub<*>>
                get() = stubConfiguration.stubs

            override fun show(): N {
                fxRunTimeView = viewConfiguration()
                return fxRunTimeView!!
            }

            override suspend fun evolve(d: D): Evolving<D> = stubConfiguration.evolve(d)
        }
    }

    /******************************************************************************************************************
     *
     * Configuration functions
     *
     ******************************************************************************************************************/

    /**
     * Set id
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.id(id: ID) : ID{
        launcher.id = id
        return id
    }

    /**
     * Set Id
     */
    @Suppress("unused")
    inline fun <reified Id> FxComponentConfiguration<N, D>.id() {
        launcher.id = Id::class
    }

    /**
     * Take next  Id
     */
    @Suppress("unused")
    fun  FxComponentConfiguration<N, D>.nextId() = Parallel<Unit> {
        val change = Change<ID>(PreId::class)
        val changing = change.happen()
        Parallel<Unit>{
            idProvider.sendBlocking(change)
        }
        launcher.id = changing.get()
    }

    /**
     *
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.keepIdProvider() {
        keepIdProvider = true
    }
    /**
     * Configure stub
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.stub(stub: Stub<D>) {
        launcher.stub = stub
    }

    /**
     * No stub configuration
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.noStub() = Parallel<Unit>{
        val stub = NoStub<D>()

        val change = Change<ID>(PreId::class)
        val changing = change.happen()
        Parallel<Unit>{
            idProvider.sendBlocking(change)
        }
        launcher.id = changing.get()
        launcher.stub = stub
    }

    /**
     * Underlying stub is a tunnel
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.tunnel() = Parallel<Unit>{
        val stub = Tunnel<D>()

        val change = Change<ID>(PreId::class)
        val changing = change.happen()
        Parallel<Unit>{
            idProvider.sendBlocking(change)
        }
        launcher.id = changing.get()
        launcher.stub = stub
    }


    /**
     * Configure the view
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.view(view: ()->N) {
        launcher.view = view
    }

    /**
     * Add a child-component
     */
    @Suppress("unused")
    fun <M,E> FxComponentConfiguration<N, D>.child(child: FxComponent<M, E>) {
        launcher.fxChildren.add( Parallel { child } )
    }

    /**
     * Add children
     */
    @Suppress("unused")
    fun  FxComponentConfiguration<N, D>.children(children: ArrayListConfiguration<FxComponent<*, *>>.()->Unit) {
        val list = org.drx.evoleq.dsl.configure(children)
        launcher.fxChildren.addAll( list.map{ Parallel<FxComponent<*,*>> { it }} )
    }
    @Suppress("unused")
    fun <M,E> ArrayListConfiguration<FxComponent<*, *>>.child(child: FxComponent<M, E>) = item(child)

    /**
     * Add an fx-special component
     */
    @Suppress("unused")
    fun <M,E> FxComponentConfiguration<N, D>.fxSpecial(child: FxComponent<M, E>) {
        launcher.fxSpecials.add( Parallel{child} )
    }

    /**
     * Perform an action on the stub and its children
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.stubAction(action: Stub<D>.()->Unit) {
        launcher.stubActions.add(Parallel{action})
    }

    /**
     * Perform an action on the component during fx-runtime
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.fxRunTime(action: N.()->Unit) = Parallel<Unit>{
        while(fxRunTime == null) {
            kotlinx.coroutines.delay(1)
        }
        fxRunTime!!.fxRunTime(action)
    }

    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.fxRunTimeConfig(action: N.()->Unit) = Parallel<Unit>{
        launcher.fxRunTime.add(Parallel{action})
    }


    /**
     * Shutdow the component-flow
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.shutdown() = Parallel<Unit> {
        while(fxRunTime == null) {
            kotlinx.coroutines.delay(1)
        }
        fxRunTime!!.shutdown()
    }

    /**
     * Add a property
     */
    @Suppress("unused")
    fun <E> FxComponentConfiguration<N, D>.property(name: String,prop: FxComponentConfiguration<N,D>.()->E) {
        properties[name] = prop()
    }

    /**
     * Add properties
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.properties(properties: HashMapConfiguration<String, Any?>.()->Unit) {
        this@FxComponentConfiguration.properties.putAll(org.drx.evoleq.dsl.configure(properties))
    }

    /**
     * Get property
     */
    @Suppress("unused", "unchecked_cast")
    fun <E> FxComponentConfiguration<N, D>.property(name: String): E = properties[name] as E


}

/**
 * Configure an FxComponent
 */
@Suppress("unused")
fun <N,D> fxComponent(configuration: FxComponentConfiguration<N,D>.()->Unit): FxComponent<N, D> {
    val conf = (object : FxComponentConfiguration<N, D>(){})

    conf.configuration()
    var component : FxComponent<N, D>? = null
    Parallel<Unit> {
        val l = conf.launcher.launch(conf)
        val terminate = fxComponentFlow<N, D>().evolve(l).get()
        //conf.idProvider.close()
        // print log
        terminate.log.forEach {
            println(it)
        }
        assert(
                terminate is FxComponentPhase.TerminationPhase.TerminateWithErrors
                        || terminate is FxComponentPhase.TerminationPhase.Terminate
        )



        if(terminate is FxComponentPhase.TerminationPhase.TerminateWithErrors){
            conf.cancel = true
            throw FxConfigurationException.ConfigurationFailed(errors = terminate.errors)
        }


    }
    Parallel<Unit>{
        while(!conf.finish && !conf.cancel){
            kotlinx.coroutines.delay(1)
        }
        if(!conf.cancel) {
            //println("finished: ${conf.idConfiguration}")
            component = conf.component
        }
    }

    while(component == null && !conf.cancel){
        sleep(1)
        //delay(1)
    }
    if(conf.cancel) {
        //conf.
        sleep(100)
        throw FxConfigurationException.ConfigurationCancelled()
    }
    return component!!
}

/**
 * Configure the view
 */
@Suppress("unused")
inline fun <reified N : Any, D> FxComponentConfiguration<N, D>.configure(noinline configure: N.()->Unit): N {
    val n = N::class.createInstance()
    n.configure()
    return n
}
/**
 * Configure the view
 */
@Suppress("unused")
inline fun <reified N : Any, reified C, D> FxComponentConfiguration<N, D>.configure(constructorData: C, noinline configure: N.()->Unit): N {
    var n: N? = null
    N::class.constructors.forEach {
        try {
            n = it.call(constructorData)
        }
        catch (exception: Exception){/* unimportant */}
    }
    if(n == null) {
        throw Exception("Constructor does not take arguments of type ${C::class}")
    }
    n!!.configure()
    return n!!
}
/**
 * Configure the view
 */
@Suppress("unused")
inline fun <reified N : Any, D> FxComponentConfiguration<N, D>.configure(constructorData: Array<out Any>, noinline configure: N.()->Unit): N {
    var n: N? = null
    N::class.constructors.forEach {
        try {
            n = it.call(*constructorData)
        }
        catch (exception: Exception){/* unimportant */}
    }
    if(n == null) {
        throw Exception("Constructor does not take arguments of these types")
    }
    n!!.configure()
    return n!!
}

fun constructor(vararg args : Any): Array<out Any> = args


fun <N> N.style(css: String): N {
    when(this) {
        is Node -> this.style = css
    }
    return this
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





