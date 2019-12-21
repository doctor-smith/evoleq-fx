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

import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.*
import kotlinx.coroutines.*
import org.drx.evoleq.coroutines.BaseReceiver
import org.drx.evoleq.coroutines.suspended
import org.drx.evoleq.dsl.ArrayListConfiguration
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.HashMapConfiguration
import org.drx.evoleq.dsl.parallel
import org.drx.evoleq.evolving.Cancellable
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.application.configration.PreId
import org.drx.evoleq.fx.component.*
import org.drx.evoleq.fx.exception.FxConfigurationException
import org.drx.evoleq.fx.flow.fxComponentFlow
import org.drx.evoleq.fx.phase.FxComponentPhase
import org.drx.evoleq.fx.phase.ComponentPhaseLauncher
import org.drx.evoleq.fx.runtime.FxRunTime
import org.drx.evoleq.fx.stub.*
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.findByKey
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

    private val processes: HashMap<ID, Evolving<Any>> by lazy { hashMapOf<ID, Evolving<Any>>() }

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
        is InputStub<*, D> -> object : FxInputComponent<Any, N, D>((stubConfiguration as InputStub<*,D>).inputReceiver as BaseReceiver<Any>) {
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

    /******************************************************************************************************************
     *
     * Input component structure
     *
     ******************************************************************************************************************/
    /**
     * Usage requires to set id and not to set the stub
     */
    @Suppress("unused")
    fun <I> FxComponentConfiguration<N,D>.onInput(onInput: (I, D)->Evolving<FxInputPhase<D>> ) {
        val stub = InputStub(onInput)
        launcher.stub = stub
    }
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.onStart(onStart: suspend (D)->D) {
        stubAction {
            if (launcher.stub is InputStub<*, *>) {
                (launcher.stub as InputStub<Any, D>).onStart = onStart
            }
        }
    }
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.onStop(onStop: suspend (D)->D) {
        stubAction {
            if (launcher.stub is InputStub<*, *>) {
                (launcher.stub as InputStub<Any, D>).onStop = onStop
            }
        }
    }

    /******************************************************************************************************************
     *
     * Process management
     *
     ******************************************************************************************************************/

    @Suppress("unused")
    suspend fun FxComponentConfiguration<N, D>.processes(put: suspend HashMap<ID, Evolving<Any>>.()->Pair<ID, Evolving<Any>>): Unit {
        val pair = processes.put()
        processes[pair.first] = pair.second as Evolving<Any>
    }
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.processes(id: ID) : Evolving<Any> = processes[id]!!

    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.removeProcess(id: ID) = processes.remove(id)

    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.processes() = processes


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
    suspend fun <M,E> FxComponentConfiguration<N, D>.child(child: FxComponent<M, E>) {
        launcher.fxChildren.add( scope.parallel { child } )
    }

    /**
     * Add a child-component
     */
    @Suppress("unused")
    suspend fun <M,E> FxComponentConfiguration<N, D>.child(child: suspend CoroutineScope.(CoroutineScope)->FxComponent<M, E>) {
        launcher.fxChildren.add( scope.parallel { child(scope) } )
    }

    /**
     * Add children
     */
    @Suppress("unused")
    suspend fun  FxComponentConfiguration<N, D>.children(children: suspend ArrayListConfiguration<FxComponent<*, *>>.()->Unit) {
        val list = org.drx.evoleq.dsl.configureSuspended(children)
        launcher.fxChildren.addAll( list.map{ scope.parallel { it }} )
    }
    @Suppress("unused")
    suspend fun <M,E> ArrayListConfiguration<FxComponent<*, *>>.child(child: FxComponent<M, E>) = itemSuspended(child)

    private suspend fun <M,E> ArrayListConfiguration<FxComponent<*, *>>.itemSuspended(child: FxComponent<M, E>) {
        item(child)
    }

    /**
     * Add an fx-special component
     */
    @Suppress("unused")
    fun <M,E> FxComponentConfiguration<N, D>.fxSpecial(child: FxComponent<M, E>) {
        launcher.fxSpecials.add( scope.parallel{child} )
    }

    /**
     * Perform an action on the stub and its children
     * during phase [FxComponentPhase.Configuration.ExtendStub]
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.stubAction(action: Stub<D>.()->Unit) {
        launcher.stubActions.add(scope.parallel{action})
    }

    /**
     * Find child-stub by id
     * Hint: works only during configuration phase
     */
    @Suppress("unused", "unchecked_cast")
    suspend inline fun <reified I> FxComponentConfiguration<N, D>.child(): Stub<*> {
        var stub: Stub<*>? = null
        stubAction{
            stub = findByKey(I::class)!!
        }
        while(stub == null) {
            delay(1)
        }
        return stub!!
    }

    /**
     * Register actions which have to executed on the FX Thread
     * but before the configuration flow enters the Runtime Phase
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.fxRunTimeConfig(index: Int = Int.MAX_VALUE,action: N.()->Unit) = scope.parallel<Unit>{
        launcher.fxRunTimeConfiguration.add(Parallel{index to action})
    }
    /******************************************************************************************************************
     *
     * Runtime actions
     *
     ******************************************************************************************************************/


    @Suppress("unused")
    suspend fun FxComponentConfiguration<N, D>.runtimeChild(pair: Pair<ID,Stub<*>>) {
        while(component == null) {
            delay(1)
        }
        component!!.stubs[pair.first] = pair.second
    }
    @Suppress("unused")
    suspend fun FxComponentConfiguration<N, D>.removeRuntimeChild(id: ID) {
        while(component == null) {
            delay(1)
        }
        component!!.stubs.remove(id)
    }
    @Suppress("unused")
    suspend fun FxComponentConfiguration<N, D>.runtimeChild(id : ID): Stub<*>? {
        while (component == null) {
            delay(1)
        }
        return component!!.stubs[id]
    }
    @Suppress("unused")
    suspend fun FxComponentConfiguration<N, D>.fxRuntimeChild(pair: Pair<ID,FxComponent<out Node,*>>) {
        while(component == null) {
            delay(1)
        }
        component!!.stubs[pair.first] = pair.second
        (component!!.show() as Node).fxChildren()!!.add(pair.second.show())
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


    /**
     * Shutdown the component-flow
     */
    @Suppress("unused")
    fun FxComponentConfiguration<N, D>.shutdown() = scope.parallel<Unit> {
        while(fxRunTime == null) {
            kotlinx.coroutines.delay(1)
        }
        fxRunTime!!.shutdown()
    }

    /******************************************************************************************************************
     *
     * Properties
     *
     ******************************************************************************************************************/
    /**
     * Add / set a property
     */
    @Suppress("unused", "unchecked_cast")
    fun <E> FxComponentConfiguration<N, D>.property(name: String, prop: FxComponentConfiguration<N,D>.()->E) {
        if(properties.containsKey(name)) {
            val prop = properties["name"]
            try{
                prop as E
            } catch(exception: Exception){
                throw Exception("Property-Type-Safety Exception: Cannot Change Type of $prop")
            }
        }
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
suspend
fun <N,D> fxComponent(scope: CoroutineScope = DEFAULT_FX_COMPONENT_SCOPE(),configuration: suspend  FxComponentConfiguration<N,D>.()->Unit): FxComponent<N, D> {

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
            //println("finished: ${conf.idConfiguration}")
            component = conf.component
        }
    }
    while(component == null && !cancelled){
        delay(1)
        //delay(1)
    }
    if(cancelled) {
        delay(100)
        throw FxConfigurationException.ConfigurationCancelled()
    }
    return component!!
}
suspend
fun <N,D> FxComponentConfiguration<N,D>.fxComponent(configuration: suspend  FxComponentConfiguration<N,D>.()->Unit): FxComponent<N, D> = fxComponent(CoroutineScope(this.scope.coroutineContext), configuration)

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

fun <V: Node> V.fxChildren(): ObservableList<Node>? = when(this) {
    is Group -> children
    is Pane -> children
    is Parent -> when {
        this.hasModifiableChildren() -> this::class.java.getMethod("getChildren").invoke(this) as ObservableList<Node>
        else -> null
    }
    else -> null
}




