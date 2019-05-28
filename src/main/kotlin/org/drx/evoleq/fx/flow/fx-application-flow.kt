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

import org.drx.evoleq.dsl.conditions
import org.drx.evoleq.dsl.receiver
import org.drx.evoleq.dsl.receivingStub
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.application.AppManager
import org.drx.evoleq.fx.application.ApplicationManager
import org.drx.evoleq.fx.application.SimpleAppManager
import org.drx.evoleq.fx.evolving.ParallelFx
import org.drx.evoleq.fx.phase.AppFlowMessage
import org.drx.evoleq.fx.phase.FxApplicationPhase
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import kotlin.reflect.KClass

class ApplicationRuntime
class ApplicationManagerStub
/* TODO error handling */
@Suppress("unchecked_cast")
fun <D, A: KClass<out SimpleAppManager<D>>> fxApplicationManagerStub(): Stub<FxApplicationPhase<D>> = stub{
    id(ApplicationManagerStub::class)
    evolve{
        phase -> when(phase) {
            is FxApplicationPhase.Launch<*> -> Parallel{
                println("launch@fxApplicationStub")
                // launch application and pass stub to receiver
                val stub = AppManager.launch( phase.applicationClass as A ).get()
                val appManager = (stub.stubs[ApplicationManager::class]!! as Stub<SimpleAppManager<D>?>).evolve(null).get()!!
                // remove app-applicationManager from stub !!!
                stub.stubs.remove(ApplicationManager::class)

                (phase as FxApplicationPhase.Launch<D>).receiver.send(AppFlowMessage.AppStubLaunched(stub))

                FxApplicationPhase.Configure.RegisterComponents(
                        appManager,
                        phase.components,
                        phase.receiver
                )
            }
            is FxApplicationPhase.Configure -> when(phase) {
                is FxApplicationPhase.Configure.RegisterComponents<*> -> Parallel {
                    println("registerComponents@fxApplicationStub")
                    require(phase is FxApplicationPhase.Configure.RegisterComponents<D>)
                    val appManager = phase.applicationManager//(phase.stub.stubs[ApplicationManager::class]!! as Stub<SimpleAppManager<D>?>).evolve(null).get()!!

                    appManager.registerComponents(phase.components)
                    //(phase as FxApplicationPhase.Configure.RegisterComponents<D>)
                    phase.receiver.send(AppFlowMessage.ComponentsRegistered())
                    FxApplicationPhase.RunTime<D>(
                            appManager,
                            scope.receiver{},
                            phase.receiver
                    )
                }
            }
            is FxApplicationPhase.RunTime -> Parallel{
                println("runtime@fxApplicationStub")
                // create flow managing runtime-communication
                // 1. create stup observing invcoming messages
                val observingStub = receivingStub<AppFlowMessage.Runtime<D>,AppFlowMessage.Runtime<D>> {
                    evolve{Immediate{it}}
                    gap{
                        from{Immediate{it}}
                        to{_, response -> Immediate{response}}
                    }
                    receiver(phase.applicationManagerPort)
                }
                // create stub of runtime
                val runtimeStub = stub<AppFlowMessage.Runtime<D>>{
                    //id(ApplicationRuntime::class)
                    evolve{message -> when(message){
                        is AppFlowMessage.Runtime.EnteredRuntimePhase -> Parallel{
                            phase.applicationManager.applicationManagerPort = phase.applicationManagerPort
                            phase.applicationManager.port.send(message)
                            AppFlowMessage.Runtime.Wait()
                        }
                        is AppFlowMessage.Runtime.Wait -> Parallel{
                            observingStub.evolve(message).get()
                        }
                        is AppFlowMessage.Runtime.ShowStage<*> -> Parallel{
                            //val stage = phase.applicationManager.showStage(message.id)
                            Parallel<Unit>{
                                val stage = phase.applicationManager.showStage(message.id).get()
                                phase.applicationManager.port.send(AppFlowMessage.FxComponentShown<D>(stage))
                            }
                            AppFlowMessage.Runtime.Wait()

                        }
                        is AppFlowMessage.Runtime.HideStage<*> -> ParallelFx{
                            phase.applicationManager.hideStage(message.id)
                            AppFlowMessage.Runtime.Wait()
                        }
                        is AppFlowMessage.Runtime.Confirm.Cancel,
                        is AppFlowMessage.Runtime.Confirm.Ok -> Immediate{ AppFlowMessage.Runtime.Terminate<D>() }
                        is AppFlowMessage.Runtime.Terminate -> Immediate{message}
                    }}
                }
                // turn runtime-stub into a flow
                val runtimeFlow = runtimeStub.toFlow<AppFlowMessage.Runtime<D>, Boolean>(
                    conditions{
                        testObject(true)
                        check{b->b}
                        updateCondition { message -> message !is AppFlowMessage.Runtime.Terminate }
                    }
                )

                // evolve data under runtime flow
                runtimeFlow.evolve(AppFlowMessage.Runtime.EnteredRuntimePhase(phase.applicationManagerPort)).get()

                // terminate all processes
                // 0. clear config etc

                // 1. stop application
                phase.applicationManager.stop()

                // notify receiver
                phase.receiver.send(AppFlowMessage.Terminated())

                phase.applicationManagerPort.actor.close()
                // return
                println("terminate@fxApplicationStub")
                FxApplicationPhase.Terminate(phase.receiver)

            }
            is FxApplicationPhase.Terminate -> Parallel{phase}
        }
    }
}

fun <D, A: KClass<out SimpleAppManager<D>>> fxApplicationManagerFlow() = fxApplicationManagerStub<D,A>().toFlow<FxApplicationPhase<D>,Boolean>(
        conditions{
            testObject(true)
            check{b->b}
            updateCondition { phase -> phase !is FxApplicationPhase.Terminate<D> }
        }
)

