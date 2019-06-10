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

import kotlinx.coroutines.CoroutineScope
import org.drx.evoleq.dsl.*
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
fun <D, A: KClass<out SimpleAppManager<D>>> CoroutineScope.fxApplicationManagerStub(): Stub<FxApplicationPhase<D>> = stub{
    id(ApplicationManagerStub::class)
    evolve{
        phase -> when(phase) {
            is FxApplicationPhase.Launch<*> -> this@fxApplicationManagerStub.parallel{
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
                is FxApplicationPhase.Configure.RegisterComponents<*> -> this@fxApplicationManagerStub.parallel {
                    println("registerComponents@fxApplicationStub")
                    require(phase is FxApplicationPhase.Configure.RegisterComponents<D>)
                    val appManager = phase.applicationManager//(phase.stub.stubs[ApplicationManager::class]!! as Stub<SimpleAppManager<D>?>).evolve(null).get()!!

                    appManager.registerComponents(phase.components)
                    //(phase as FxApplicationPhase.Configure.RegisterComponents<D>)
                    phase.receiver.send(AppFlowMessage.ComponentsRegistered())
                    FxApplicationPhase.RunTime<D>(
                            appManager,
                            receiver{},
                            phase.receiver
                    )
                }
            }
            is FxApplicationPhase.RunTime -> this@fxApplicationManagerStub.parallel{
                println("runtime@fxApplicationStub")
                // create flow managing runtime-communication
                // 1. create stup observing invcoming messages
                val observingStub = receivingStub<AppFlowMessage.Runtime<D>,AppFlowMessage.Runtime<D>> {
                    evolve{this@fxApplicationManagerStub.parallel{it}}
                    gap{
                        from{this@fxApplicationManagerStub.parallel{it}}
                        to{_, response -> this@fxApplicationManagerStub.parallel{response}}
                    }
                    receiver(phase.applicationManagerPort)
                }
                // create stub of runtime
                val runtimeStub = stub<AppFlowMessage.Runtime<D>>{
                    //id(ApplicationRuntime::class)
                    evolve{message -> when(message){
                        is AppFlowMessage.Runtime.EnteredRuntimePhase -> this@fxApplicationManagerStub.parallel{
                            phase.applicationManager.applicationManagerPort = phase.applicationManagerPort
                            phase.applicationManager.port.send(message)
                            AppFlowMessage.Runtime.Wait<D>()
                        }
                        is AppFlowMessage.Runtime.Wait -> this@fxApplicationManagerStub.parallel{
                            observingStub.evolve(message).get()
                        }
                        is AppFlowMessage.Runtime.ShowStage<*> -> this@fxApplicationManagerStub.parallel{
                            //val stage = phase.applicationManager.showStage(message.id)
                            Parallel<Unit>{
                                val stage = phase.applicationManager.showStage(message.id).get()
                                phase.applicationManager.port.send(AppFlowMessage.FxComponentShown<D>(stage))
                            }
                            AppFlowMessage.Runtime.Wait<D>()

                        }
                        is AppFlowMessage.Runtime.HideStage<*> -> ParallelFx{
                            phase.applicationManager.hideStage(message.id)
                            AppFlowMessage.Runtime.Wait<D>()
                        }
                        is AppFlowMessage.Runtime.Confirm.Cancel,
                        is AppFlowMessage.Runtime.Confirm.Ok -> this@fxApplicationManagerStub.parallel{ AppFlowMessage.Runtime.Terminate<D>() }
                        is AppFlowMessage.Runtime.Terminate -> this@fxApplicationManagerStub.parallel{message}
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
            is FxApplicationPhase.Terminate -> this@fxApplicationManagerStub.parallel{phase}
        }
    }
}

fun <D, A: KClass<out SimpleAppManager<D>>> CoroutineScope.fxApplicationManagerFlow() = fxApplicationManagerStub<D,A>().toFlow<FxApplicationPhase<D>,Boolean>(
        conditions{
            testObject(true)
            check{b->b}
            updateCondition { phase -> phase !is FxApplicationPhase.Terminate<D> }
        }
)

