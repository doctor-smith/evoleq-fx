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
package org.drx.evoleq.fx.flow

import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.Pane
import kotlinx.coroutines.withTimeout
import org.drx.evoleq.dsl.conditions
import org.drx.evoleq.dsl.flow
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.flow.SuspendedFlow
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.component.FxNoStubComponent
import org.drx.evoleq.fx.component.FxTunnelComponent
import org.drx.evoleq.fx.dsl.FxComponentConfiguration
import org.drx.evoleq.fx.dsl.isFxParent
import org.drx.evoleq.fx.exception.FxConfigurationException
import org.drx.evoleq.fx.phase.FxComponentPhase
import org.drx.evoleq.fx.runtime.FxRunTime
import org.drx.evoleq.fx.stub.NoStub
import org.drx.evoleq.stub.DefaultIdentificationKey
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow

class FxComponentFlow
@Suppress("unchecked_cast")
fun<N,D> fxComponentStub(): Stub<FxComponentPhase> = stub{
    id(FxComponentFlow::class)
    evolve{
        when(it) {
            /**
             * launching phase
             */
            is FxComponentPhase.Launch<*,*> -> Parallel{
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
                            stubActions = it.stubActions as ArrayList<Parallel<Stub<D>.() -> Unit>>,
                            log = it.log
                    )
                }
            }
            /**
             * PreConfiguration
             */
            is FxComponentPhase.PreConfiguration<*,*> -> Parallel{
                val id = try {
                    withTimeout(it.timeout) { it.id.get() }
                } catch (exception: Exception) {
                    it.errors.add(FxConfigurationException.IdNotSet(it))
                    DefaultIdentificationKey::class
                }
                val stub = try {
                    withTimeout(it.timeout) { it.stub.get() as Stub<D> }
                } catch (exception: Exception) {
                    it.errors.add(FxConfigurationException.StubNotSet(it))
                    NoStub<D>()
                }
                val view =  it.view as Parallel<() -> N>
                if(it.errors.isNotEmpty()){
                    view.job().cancel()
                    it.fxChildren.forEach{child -> child.job().cancel()}
                    it.fxSpecials.forEach{child -> child.job().cancel()}
                    it.fxRunTime.forEach{child -> child.job().cancel()}
                    it.stubActions.forEach{child -> child.job().cancel()}
                    it.view.job().cancel()
                    FxComponentPhase.TerminationPhase.TerminateWithErrors(it.errors, it.log)
                } else{
                    it.log.add("$id")
                    FxComponentPhase.Configuration.Setup(
                            configuration = it.configuration as FxComponentConfiguration<N, D>,
                            id = id,
                            stub = stub,
                            view = view,
                            fxChildren = it.fxChildren,
                            fxSpecials = it.fxSpecials,
                            fxRunTime = it.fxRunTime as ArrayList<Parallel<N.() -> Unit>>,
                            stubActions = it.stubActions as ArrayList<Parallel<Stub<D>.() -> Unit>>,
                            log = it.log
                    )
                }
            }
            /**
             * Configuration
             */
            is FxComponentPhase.Configuration -> when(it) {
                /**
                 * Setup
                 */
                is FxComponentPhase.Configuration.Setup<*,*> ->Parallel{
                    withTimeout(it.timeout) {
                        val fxChildren = arrayListOf(*it.fxChildren.map { c -> c.get() }.toTypedArray()) //.filter { child -> child.id != NoStub::class }
                        val fxSpecials = arrayListOf(*it.fxSpecials.map { c -> c.get() }.toTypedArray())
                        FxComponentPhase.Configuration.AddFxChildren<N, D>(
                                configuration = it.configuration as FxComponentConfiguration<N, D>,
                                id = it.id,
                                stub = it.stub as Stub<D>,
                                view = it.view as Parallel<() -> N>,
                                fxChildren = fxChildren,
                                fxSpecials = fxSpecials,
                                fxRunTime = it.fxRunTime as ArrayList<Parallel<N.() -> Unit>>,
                                stubActions = it.stubActions as ArrayList<Parallel<Stub<D>.() -> Unit>>,
                                log = it.log
                        )
                    }
                }
                /**
                 * AddFxChildren
                 */
                is FxComponentPhase.Configuration.AddFxChildren<*,*> ->Parallel{
                    /* TODO Error management -> Termination*/
                    val stub = it.stub as Stub<D>
                    if(stub !is NoStub) {
                        it.fxChildren.filter{child -> child !is FxNoStubComponent<*, *> }.forEach { child ->
                            // forward all children of tunnels
                            if(child is FxTunnelComponent){
                                child.stubs.forEach { t, u -> stub.stubs[t] = u }
                            }
                            else {
                                stub.stubs[child.id] = child
                            }
                        }
                        it.fxSpecials.filter{child -> child !is FxNoStubComponent<*, *> }.forEach { child ->
                            // forward all children of tunnels
                            if(child is FxTunnelComponent){
                                child.stubs.forEach { t, u -> stub.stubs[t] = u }
                            }
                            else {
                                stub.stubs[child.id] = child
                            }
                        }
                    }
                    FxComponentPhase.Configuration.ExtendStub<N, D>(
                            configuration = it.configuration as FxComponentConfiguration<N,D>,
                            id = it.id,
                            stub = stub,
                            view = it.view as Parallel<()->N>,
                            fxChildren = it.fxChildren,
                            fxSpecials = it.fxSpecials,
                            fxRunTime =  it.fxRunTime as ArrayList<Parallel<N.()->Unit>>,
                            stubActions = it.stubActions as ArrayList<Parallel<Stub<D>.() -> Unit>>,
                            log = it.log
                    )
                }
                /**
                 * ExtendStub
                 */
                is FxComponentPhase.Configuration.ExtendStub<*,*> ->Parallel{
                    val actions = it.stubActions.map { action -> action.get() as Stub<D>.()->Unit }
                    val stub = it.stub as Stub<D>
                    //val errors : ArrayList<Exception> by lazy { arrayListOf<Exception>() }
                    if(stub !is NoStub) {
                        actions.forEach {
                            action -> try {
                                 stub.action()
                            } catch ( exception: Exception ) {
                                it.errors.add(exception)
                            }
                        }
                    }
                    else { if ( actions.isNotEmpty() ) {
                        it.errors.add( FxConfigurationException.IsNoStub(phase = it ,componentId =  it.id) )
                    }}
                    if(it.errors.isNotEmpty()) {
                        FxComponentPhase.TerminationPhase.TerminateWithErrors(it.errors, it.log)
                    } else {
                        FxComponentPhase.Configuration.Configure<N, D>(
                                configuration = it.configuration as FxComponentConfiguration<N, D>,
                                id = it.id,
                                stub = stub,
                                view = it.view as Parallel<() -> N>,
                                fxChildren = it.fxChildren,
                                fxSpecials = it.fxSpecials,
                                fxRunTime = it.fxRunTime as ArrayList<Parallel<N.() -> Unit>>,
                                log = it.log
                        )
                    }
                }
                /**
                 * Configure
                 */
                is FxComponentPhase.Configuration.Configure<*, *> -> Parallel{
                    // collect data
                    it.configuration.idConfiguration = it.id
                    (it.configuration as FxComponentConfiguration<N,D>).stubConfiguration = it.stub as Stub<D>
                    try{
                        withTimeout(it.timeout) {
                            (it.configuration).viewConfiguration = (it.view as Parallel<() -> N>).get()
                        }
                    } catch(exception : Exception) {
                        it.errors.add( FxConfigurationException.ViewNotSet(phase = it, componentId =it.id))
                    }
                    // treat errors
                    if(it.errors.isNotEmpty() ) {
                        FxComponentPhase.TerminationPhase.TerminateWithErrors(it.errors, it.log)
                    }
                    else {
                        val component = it.configuration.configure()
                        it.configuration.component = component
                        it.configuration.finish = true

                        FxComponentPhase.RunTimeConfiguration<N, D>(
                                fxChildren = it.fxChildren,
                                configuration = it.configuration,
                                fxRunTime = it.fxRunTime as ArrayList<Parallel<N.()->Unit>>,
                                log = it.log
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
                val fxChildren = it.fxChildren

                // wait for fxRuntimeView
                // fxRuntimeView is set when the show-function of the component is called
                var view: N? = null
                try{
                     view = withTimeout(it.timeout) {
                        Parallel<N> {
                            while (configuration.fxRunTimeView == null) {
                                kotlinx.coroutines.delay(1)
                            }
                            configuration.fxRunTimeView!!
                        }.get()
                    }
                } catch(exception: Exception){
                    it.errors.add(FxConfigurationException.RunTimeViewTimeout(componentId = component.id, component = component))
                    it.errors.add(exception)

                }
                if(it.errors.isNotEmpty()){
                    FxComponentPhase.TerminationPhase.TerminateWithErrors(it.errors, it.log)
                }
                else {
                    // setup fxRuntime
                    val fxRunTime = object : FxRunTime<N, D>() {
                        override val phase: FxComponentPhase.RunTimePhase<N, D>
                            get() = FxComponentPhase.RunTimePhase.RunTime(this, it.log)
                        override val view: N
                            get() = view!!
                        override val component: FxComponent<N, D>
                            get() = component

                    }

                    // add children
                    if (view!!.isFxParent()) {
                        fxRunTime.fxRunTime {
                            fxChildren.forEach { child ->
                                // call child.show()
                                // This also sets the runTimeView of the child and
                                // hence the child-configuration enters the runtime-phase
                                val childView = child.show()
                                when (childView) {
                                    is Node -> when (view!!) {
                                        is Group -> (view!! as Group).children.add(childView)
                                        is Pane -> (view!! as Pane).children.add(childView)
                                        is Parent -> try {
                                            val children = (view!! as Parent)::class.java.getMethod("getChildren")
                                            (children.invoke(this) as ObservableList<Node>).add(childView)
                                        } catch (exception: Exception) {
                                            it.errors.add(exception)
                                        }
                                    }
                                }
                            }

                        }
                    }
                    // set handleRequests time variable of configuration
                    // -
                    configuration.fxRunTime = fxRunTime
                    //}
                    FxComponentPhase.RunTimePhase.RunTime(fxRunTime, it.log)
                }
            }
            is FxComponentPhase.RunTimePhase.RunTime<*,*> -> {
                /* TODO Error management -> Termination*/
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
                FxComponentPhase.TerminationPhase.Terminate(log = it.log)
            }
            is FxComponentPhase.TerminationPhase.TerminateWithErrors -> Immediate{
                Parallel<Unit>{
                    it.log.forEach { println(it) }
                    it.errors.forEach { error -> println( error )
                }}
                FxComponentPhase.TerminationPhase.Terminate(log = it.log)
            }
            is FxComponentPhase.TerminationPhase.Terminate -> Immediate{
                FxComponentPhase.TerminationPhase.Terminate(log = it.log)
            }
        }
    }
}

fun <N,D> fxComponentFlow(): SuspendedFlow<FxComponentPhase, Boolean> = fxComponentStub<N,D>()
        .toFlow( conditions {
            testObject(true)
            check{ b -> b}
            updateCondition{phase -> phase !is FxComponentPhase.TerminationPhase.TerminateWithErrors && phase !is FxComponentPhase.TerminationPhase.Terminate}
        }
)