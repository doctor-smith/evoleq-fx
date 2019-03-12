package org.drx.evoleq.fx.flow

import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.layout.Pane
import kotlinx.coroutines.withTimeout
import org.drx.evoleq.dsl.conditions
import org.drx.evoleq.dsl.flow
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.flow.SuspendedFlow
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.FxComponentConfiguration
import org.drx.evoleq.fx.dsl.isFxParent
import org.drx.evoleq.fx.evolving.ParallelFx
import org.drx.evoleq.fx.exception.FxConfigurationException
import org.drx.evoleq.fx.phase.FxComponentPhase
import org.drx.evoleq.fx.runtime.FxRunTime
import org.drx.evoleq.fx.stub.NoStub
import org.drx.evoleq.stub.DefaultIdentificationKey
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import org.drx.evoleq.time.waitForValueToBeSet
import kotlin.reflect.KClass

class FxComponentFlow
@Suppress("unchecked_cast")
fun<N,D> fxComponentStub(): Stub<FxComponentPhase> = stub{
    id(FxComponentFlow::class)
    evolve{
        //println("evolve $it")
        when(it) {
            is FxComponentPhase.Launch<*,*> -> Parallel{
                println("launch")
                withTimeout(it.timeout){
                    val configuration = it.configuration.get() as FxComponentConfiguration<N,D>
                    FxComponentPhase.PreConfiguration(
                            configuration = configuration,
                            id = it.id,
                            stub = it.stub as Parallel<Stub<D>>,
                            view = it.view as Parallel<()->N>,
                            fxChildren = it.fxChildren,
                            fxSpecials = it.fxSpecials,
                            fxRunTime =  it.fxRunTime as ArrayList<Parallel<N.()->Unit>>,
                            stubActions = it.stubActions as ArrayList<Parallel<Stub<D>.() -> Unit>>
                    )
                }
            }
            is FxComponentPhase.PreConfiguration<*,*> -> Parallel{
                println("pre-configure")
                val errors: ArrayList<Exception> by lazy { arrayListOf<Exception>() }
                val id = try {
                    withTimeout(it.timeout) { it.id.get() }
                } catch (exception: Exception) {
                    errors.add(FxConfigurationException.IdNotSet(it))
                    DefaultIdentificationKey::class
                }
                val stub = try {
                    withTimeout(it.timeout) { it.stub.get() as Stub<D> }
                } catch (exception: Exception) {
                    errors.add(FxConfigurationException.StubNotSet(it))
                    NoStub<D>()
                }
                val view =  it.view as Parallel<() -> N>
                if(errors.isNotEmpty()){
                    view.job().cancel()
                    it.fxChildren.forEach{child -> child.job().cancel()}
                    it.fxSpecials.forEach{child -> child.job().cancel()}
                    it.fxRunTime.forEach{child -> child.job().cancel()}
                    it.stubActions.forEach{child -> child.job().cancel()}
                    it.view.job().cancel()
                    it.configuration.cancel = true
                    println(errors)
                    FxComponentPhase.TerminateWitErrors(errors)
                    //FxComponentPhase.RunTimePhase.ShutDown<N,D>()
                } else{
                //withTimeout(it.timeout) {
                    FxComponentPhase.Configuration.Setup(
                            configuration = it.configuration as FxComponentConfiguration<N, D>,
                            id = id,
                            stub = stub,
                            view = view,
                            fxChildren = it.fxChildren,
                            fxSpecials = it.fxSpecials,
                            fxRunTime = it.fxRunTime as ArrayList<Parallel<N.() -> Unit>>,
                            stubActions = it.stubActions as ArrayList<Parallel<Stub<D>.() -> Unit>>
                    )
                }
            }
            is FxComponentPhase.Configuration -> when(it) {
                is FxComponentPhase.Configuration.Setup<*,*> ->Parallel{
                    println("Setup ${it.id}")
                    withTimeout(it.timeout) {
                        // only take children with stubs
                        val fxChildren = arrayListOf(*it.fxChildren.map { c -> c.get() }.filter { child -> child.id != NoStub::class }.toTypedArray())
                        val fxSpecials = arrayListOf(*it.fxSpecials.map { c -> c.get() }.toTypedArray())
                        FxComponentPhase.Configuration.AddFxChildren<N, D>(
                                configuration = it.configuration as FxComponentConfiguration<N, D>,
                                id = it.id,
                                stub = it.stub as Stub<D>,
                                view = it.view as Parallel<() -> N>,
                                fxChildren = fxChildren,
                                fxSpecials = fxSpecials,
                                fxRunTime = it.fxRunTime as ArrayList<Parallel<N.() -> Unit>>,
                                stubActions = it.stubActions as ArrayList<Parallel<Stub<D>.() -> Unit>>
                        )
                    }
                }
                is FxComponentPhase.Configuration.AddFxChildren<*,*> ->Parallel{
                    /* TODO Error management -> Termination*/
                    println("AddChildren ${it.id}")
                    val stub = it.stub as Stub<D>
                    if(stub !is NoStub) {
                        it.fxChildren.forEach { entry ->
                            stub.stubs[entry.id] = entry
                        }
                        it.fxSpecials.forEach { entry ->
                            stub.stubs[entry.id] = entry
                        }
                    }
                    //}.get()
                    FxComponentPhase.Configuration.ExtendStub<N, D>(
                            configuration = it.configuration as FxComponentConfiguration<N,D>,
                            id = it.id,
                            stub = stub,
                            view = it.view as Parallel<()->N>,
                            fxChildren = it.fxChildren,
                            fxSpecials = it.fxSpecials,
                            fxRunTime =  it.fxRunTime as ArrayList<Parallel<N.()->Unit>>,
                            stubActions = it.stubActions as ArrayList<Parallel<Stub<D>.() -> Unit>>
                    )
                }
                is FxComponentPhase.Configuration.ExtendStub<*,*> ->Parallel{
                    println("ExtendStub ${it.id} ")
                    val actions = it.stubActions.map { action -> action.get() as Stub<D>.()->Unit }
                    val stub = it.stub as Stub<D>
                    val errors : ArrayList<Exception> by lazy { arrayListOf<Exception>() }
                    if(stub !is NoStub) {
                        actions.forEach {
                            action -> try {
                                 stub.action()
                            } catch ( exception: Exception ) {
                                errors.add(exception)
                            }
                        }
                    }
                    else { if ( actions.isNotEmpty() ) {
                        errors.add( FxConfigurationException.IsNoStub(phase = it ,componentId =  it.id) )
                    }}
                    if(errors.isNotEmpty()) {
                        it.configuration.cancel = true
                        FxComponentPhase.TerminateWitErrors(errors)
                    } else {
                        FxComponentPhase.Configuration.Configure<N, D>(
                                configuration = it.configuration as FxComponentConfiguration<N, D>,
                                id = it.id,
                                stub = stub,
                                view = it.view as Parallel<() -> N>,
                                fxChildren = it.fxChildren,
                                fxSpecials = it.fxSpecials,
                                fxRunTime = it.fxRunTime as ArrayList<Parallel<N.() -> Unit>>
                        )
                    }
                }
                is FxComponentPhase.Configuration.Configure<*, *> -> Parallel{
                    println("Configure ${it.id}")
                    it.configuration.idConfiguration = it.id
                    (it.configuration as FxComponentConfiguration<N,D>).stubConfiguration = it.stub as Stub<D>
                    var error : FxConfigurationException? = null
                    try{
                        withTimeout(it.timeout) {
                            (it.configuration).viewConfiguration = (it.view as Parallel<() -> N>).get()
                        }
                    } catch(exception : Exception) {
                        error = FxConfigurationException.ViewNotSet(phase = it, componentId =it.id)
                    }
                    if(error != null ) {
                        it.configuration.cancel = true
                        FxComponentPhase.TerminateWitErrors(arrayListOf(error!!))
                    }
                    else {
                        it.configuration.finish = true
                        while ((it.configuration).component == null) {
                            kotlinx.coroutines.delay(1)
                        }
                        FxComponentPhase.RunTimeConfiguration<N, D>(
                                fxChildren = it.fxChildren,
                                configuration = it.configuration
                        )
                    }

                }
            }
            /**
             * Here, the fxChildren view must be added as children to the view
             */
            is FxComponentPhase.RunTimeConfiguration<*,*> -> Parallel{
                /* TODO Error management -> Termination*/
                val configuration = it.configuration as FxComponentConfiguration<N, D>
                val component = configuration.component as FxComponent<N, D>
                println("RunTimeConfiguration ${component.id}")
                val fxChildren = it.fxChildren

                val view: N = withTimeout(it.timeout) {
                    Parallel<N> {
                        while (configuration.fxRunTimeView == null) {
                            kotlinx.coroutines.delay(1)
                        }
                        configuration.fxRunTimeView!!
                    }.get()
                }

                /* TODO Error management -> Termination*/
                if(view.isFxParent()) {
                    fxChildren.forEach { child ->
                        val childView = child.show()
                        when(childView) {
                            is Node ->  when (view) {
                                is Group -> (view as Group).children.add(childView)
                                is Pane -> (view as Pane).children.add(childView)
                            }
                        }
                    }
                }
                val fxRunTime = object : FxRunTime<N, D>() {
                    override val phase: FxComponentPhase.RunTimePhase<N, D>
                        get() = FxComponentPhase.RunTimePhase.RunTime<N,D>(this)
                    override val view: N
                        get() = view
                    override val component: FxComponent<N, D>
                        get() = component
                }
                /**
                 * Configuration shall work into fxRunTime
                 */
                configuration.fxRunTime = fxRunTime
                FxComponentPhase.RunTimePhase.RunTime<N,D>(fxRunTime)
            }
            /**
             * Site!
             */
            is FxComponentPhase.RunTimePhase.RunTime<*,*> -> {
                /* TODO Error management -> Termination*/
                println("RunTime ${it.fxRunTime.component.id}")
                val runTimeFlow = flow<FxComponentPhase.RunTimePhase<N, D>, Boolean>{
                    conditions(org.drx.evoleq.dsl.conditions {
                        testObject ( true )
                        check { b ->b }
                        updateCondition { runTimePhase -> (runTimePhase is FxComponentPhase.RunTimePhase.RunTime<*,*>) }

                    })
                    flow{ runTimePhase -> when(runTimePhase){
                        is FxComponentPhase.RunTimePhase.RunTime-> runTimePhase.fxRunTime.fxRun()
                        is FxComponentPhase.RunTimePhase.ShutDown -> Immediate{ runTimePhase }
                    }}
                }
                runTimeFlow.evolve(it as FxComponentPhase.RunTimePhase.RunTime<N,D>)
            }
            is FxComponentPhase.RunTimePhase.ShutDown<*, *> ->Parallel{
                println("ShutDown")

                FxComponentPhase.Terminate()
            }
            is FxComponentPhase.TerminateWitErrors -> Immediate{
                println("Terminate with errors")
                Parallel<Unit>{ it.errors.forEach { error -> println( error )  }}
                FxComponentPhase.Terminate()
            }
            is FxComponentPhase.Terminate -> Immediate{
                println("Terminate")
                FxComponentPhase.Terminate()
            }
        }
    }
}

fun <N,D> fxComponentFlow(): SuspendedFlow<FxComponentPhase, Boolean> = fxComponentStub<N,D>()
        .toFlow( conditions {
            testObject(true)
            check{ b -> b}
            updateCondition{phase -> phase !is FxComponentPhase.Terminate}
        }
)