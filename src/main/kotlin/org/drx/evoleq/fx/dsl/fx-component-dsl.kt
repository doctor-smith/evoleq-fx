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
import kotlinx.coroutines.*
import org.drx.evoleq.dsl.ArrayListConfiguration
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.HashMapConfiguration
import org.drx.evoleq.dsl.parallel
import org.drx.evoleq.evolving.Cancellable
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.application.configration.PreId
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
import java.lang.Thread.sleep
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

typealias ID = KClass<*>

val DEFAULT_FX_COMPONENT_SCOPE: ()-> CoroutineScope = { CoroutineScope(SupervisorJob()) }//GlobalScope }

abstract class FxComponentConfiguration<N, D>() :  Configuration<FxComponent<N, D>> {

    constructor(scope: CoroutineScope) : this() {
        this.scope = scope
    }

    // Component Data
    lateinit var idConfiguration: ID
    lateinit var stubConfiguration: Stub<D>
    lateinit var viewConfiguration: ()->N

    var scope: CoroutineScope = DEFAULT_FX_COMPONENT_SCOPE()

    // Data related to configuration process
    val launcher = ComponentPhaseLauncher<N,D>()
    var fxRunTime: FxRunTime<N, D>? = null

    var finish: Boolean = false
    var cancel: Boolean = false
    var component: FxComponent<N, D>? = null

    var fxRunTimeView: N? = null


    private var keepIdProvider = true

    private val properties: HashMap<String, Any?> by lazy { HashMap<String, Any?>() }



    override fun configure(): FxComponent<N, D> = when(stubConfiguration) {
        is Tunnel<D> ->  object : FxTunnelComponent<N, D> {
            override val scope: CoroutineScope
                get() = this@FxComponentConfiguration.scope
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

            override val scope: CoroutineScope
                get() = this@FxComponentConfiguration.scope
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
            override val scope: CoroutineScope
                get() = this@FxComponentConfiguration.scope
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
    fun  FxComponentConfiguration<N, D>.nextId() = scope.parallel<Unit> {

        /*val change = Change<ID>(PreId::class)
        val changing = change.happen()
        parallel<Unit>{
            idProvider.sendBlocking(change)
        }
        launcher.id = changing.get()
        */
        launcher.id = PreId::class
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
    fun FxComponentConfiguration<N, D>.noStub() = scope.parallel<Unit>{
        val stub = NoStub<D>()
        launcher.id = PreId::class
        launcher.stub = stub
    }

    /**
     * Underlying stub is a tunnel
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.tunnel() = scope.parallel<Unit>{
        val stub = Tunnel<D>()
        launcher.id = PreId::class
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
        launcher.fxChildren.add( scope.parallel { child } )
    }

    /**
     * Add a child-component
     */
    @Suppress("unused")
    fun <M,E> FxComponentConfiguration<N, D>.child(child: CoroutineScope.(CoroutineScope)->FxComponent<M, E>) {
        launcher.fxChildren.add( scope.parallel { child(scope) } )
    }

    /**
     * Add children
     */
    @Suppress("unused")
    fun  FxComponentConfiguration<N, D>.children(children: ArrayListConfiguration<FxComponent<*, *>>.()->Unit) {
        val list = org.drx.evoleq.dsl.configure(children)
        launcher.fxChildren.addAll( list.map{ scope.parallel { it }} )
    }
    @Suppress("unused")
    fun <M,E> ArrayListConfiguration<FxComponent<*, *>>.child(child: FxComponent<M, E>) = item(child)

    /**
     * Add an fx-special component
     */
    @Suppress("unused")
    fun <M,E> FxComponentConfiguration<N, D>.fxSpecial(child: FxComponent<M, E>) {
        launcher.fxSpecials.add( scope.parallel{child} )
    }

    /**
     * Perform an action on the stub and its children
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.stubAction(action: Stub<D>.()->Unit) {
        launcher.stubActions.add(scope.parallel{action})
    }

    /**
     * Perform an action on the component during fx-runtime
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.fxRunTime(action: N.()->Unit) = scope.parallel<Unit>{
        while(fxRunTime == null) {
            kotlinx.coroutines.delay(1)
        }
        fxRunTime!!.fxRunTime(action)
    }

    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.fxRunTimeConfig(action: N.()->Unit) = scope.parallel<Unit>{
        launcher.fxRunTime.add(Parallel{action})
    }


    /**
     * Shutdow the component-flow
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.shutdown() = scope.parallel<Unit> {
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
fun <N,D> fxComponent(scope: CoroutineScope = DEFAULT_FX_COMPONENT_SCOPE(),configuration: FxComponentConfiguration<N,D>.()->Unit): FxComponent<N, D> {

    var component : FxComponent<N, D>? = null
    var cancelled = false
    val conf = (object : FxComponentConfiguration<N, D>(scope) {})

    conf.configuration()
    // launch component flow and await termination
    scope.parallel {
        val l = conf.launcher.launch(conf)
        val terminationPhase = parallel{ fxComponentFlow<N, D>().evolve(l) .get() }
        val terminate = terminationPhase.get()
        terminate.log.forEach {
            println(it)
        }
        assert(
            terminate is FxComponentPhase.TerminationPhase.TerminateWithErrors ||
            terminate is FxComponentPhase.TerminationPhase.Terminate
        )
        if (terminate is FxComponentPhase.TerminationPhase.TerminateWithErrors) {
            if (terminationPhase is Cancellable<*>) {
                (terminationPhase as Cancellable<FxComponentPhase>).cancel(terminate).get()
            }
            conf.cancel = true
            cancelled = true
            throw FxConfigurationException.ConfigurationFailed(errors = terminate.errors)
        }
    }
    // await component
    scope.parallel {
        while (!conf.finish && !conf.cancel) {
            kotlinx.coroutines.delay(1)
        }
        if (!conf.cancel) {
            println("finished: ${conf.idConfiguration}")
            component = conf.component
        }
    }
    while(component == null && !cancelled){
        sleep(1)
        //delay(1)
    }
    if(cancelled) {
        sleep(100)
        throw FxConfigurationException.ConfigurationCancelled()
    }
    return component!!
}

fun <N,D> FxComponentConfiguration<N,D>.fxComponent(configuration: FxComponentConfiguration<N,D>.()->Unit): FxComponent<N, D> = fxComponent(CoroutineScope(this.scope.coroutineContext), configuration)

/*{
   return scope.fxComponent(configuration)(scope)
}
*/
/*
fun <N,D> CoroutineScope.fxComponent( configuration: FxComponentConfiguration<N,D>.()->Unit): CoroutineScope.()->FxComponent<N,D> = {
    this@fxComponent + this.coroutineContext
    fxComponent(this, configuration)
}
*/

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
fun <N> N.cssClass(vararg classes: String): N {
    when(this){
        is Node -> classes.forEach { this.styleClass.add(it) }
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





