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
package org.drx.evoleq.fx.test

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.application.AppManager
import org.drx.evoleq.fx.application.deprecated.SimpleAppManager
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.fx.evolving.ParallelFx
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.time.Change
import org.drx.evoleq.time.happen


/**
 * Show the stageComponent and return it as a stub
 */
fun <D> showTestStage(stageComponent: FxComponent<Stage, D>): Parallel<Stub<D>> = Parallel {

    class TestApp<D> : SimpleAppManager<D>() {
        init{
            ParallelFx<Unit>{showStage(stageComponent.show())}
        }
        override fun configure(): Stub<D> = stageComponent as Stub<D>
    }
    val stub = AppManager.launch(TestApp<D>()).get()
    stub
}

fun <D> launchTestStage(stageComponent: FxComponent<Stage,D>): Parallel<Stub<D>> = Parallel{

    class TestApp<D> : SimpleAppManager<D>() {
        init{
            ParallelFx<Unit>{showStage(stageComponent.show())}
        }
        override fun configure(): Stub<D> = stageComponent as Stub<D>
    }


    val stubLauncher = launchApplicationStub<D, TestApp<D>> {
        application(TestApp())
    }
    val stub = stubLauncher.evolve(null).get()!!
    stub
}

fun <P : Parent,D> showInTestStage(parentComponent: FxComponent<P, D>): Parallel<Stub<D>> = Parallel{
    val stageComponent = fxStage<D> {
        id<StageId>()
        view{configure {  }}
        scene(fxScene{
            tunnel()
            root(parentComponent){root -> Scene(root) }
        })
        stub(stub{
            evolve{data -> parentComponent.evolve(data)}
        })
    }
    launchTestStage(stageComponent).get()
}

fun <N : Node, D> showNodeInTestStage(nodeComponent: FxComponent<N, D>): Parallel<Stub<D>> = Parallel{
    val group = fxGroup<D>{
        tunnel()
        view{configure{}}
        child(nodeComponent)
    }
    showInTestStage(group).get()
}


val testRunner: SendChannel<Change<suspend CoroutineScope.() -> Unit>> =
        GlobalScope.actor {
            var blocked = false
            for (f in channel) {
                while(blocked){
                    delay(1)
                }
                var error : Exception? = null
                Parallel<Unit> {
                    blocked = true
                    var job: Job? = null
                    try {
                        withTimeout(10_000) {
                            job = launch { f.value(scope) }
                            job!!.join()

                        }
                    }
                    catch(exception: Exception) {
                        job!!.cancel()
                        error = exception
                    }

                    blocked = false
                }.get()
                if(error == null) {
                    f.value = {}
                }
                else{
                    f.value = {throw error!!}
                }
            }

        }

suspend fun runTest(test: suspend CoroutineScope.()->Unit): Evolving<suspend CoroutineScope.()->Unit> {
    val change = Change(test)
    val changing = change.happen()
    testRunner.send(change)
    return changing
}

fun fxRunTest(test: suspend CoroutineScope.()->Unit) = runBlocking {
    runTest(test).get()()
}