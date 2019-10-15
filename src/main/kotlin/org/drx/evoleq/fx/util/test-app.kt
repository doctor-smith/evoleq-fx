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
package org.drx.evoleq.fx.util

import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import org.drx.evoleq.dsl.parallel
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.application.configration.AppManager
import org.drx.evoleq.fx.application.deprecated.SimpleAppManager
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.stub.Stub
import java.lang.Thread.sleep


/**
 * Show the stageComponent and return it as a stub
 */
fun <D> CoroutineScope.showStage(stageComponent: FxComponent<Stage, D>): Parallel<Stub<D>> = parallel {

    class TestApp<D> : SimpleAppManager<D>() {
        init{
            scope.parallelFx<Unit>{showStage(stageComponent.show())}
        }
        override fun configure(): Stub<D> = stageComponent as Stub<D>
    }
    val stub = AppManager.launch(TestApp<D>()).get()
    stub
}

fun <D> CoroutineScope.showStage(stageComponent: CoroutineScope.(CoroutineScope)-> FxComponent<Stage, D>): Parallel<Stub<D>> = parallel {

    class TestApp : SimpleAppManager<D>() {
        var component: FxComponent<Stage,D>? = null
        init{
            scope.parallel{
                while(component== null){
                    delay(1)
                }
                parallelFx{
                    this@TestApp.showStage(component!!.show())
                }.get()
            }
        }
        override fun configure(): Stub<D> {
            var done = false
            scope.parallel parent@{
                component = stageComponent(this@parent)
                done = true
            }
            while(!done){
                sleep(1)
            }
            return component!!
        }
    }
    val stub = AppManager.launch(TestApp()).get()
    stub
}

fun <D> CoroutineScope.launchStage(stageComponent: FxComponent<Stage, D>): Parallel<Stub<D>> = parallel{

    class TestApp<D> : SimpleAppManager<D>() {
        init{
            scope.parallelFx{showStage(stageComponent.show())}
        }
        override fun configure(): Stub<D> = stageComponent as Stub<D>
    }


    val stubLauncher = launchApplicationStub<D, TestApp<D>> {
        application(TestApp())
    }
    val stub = stubLauncher.evolve(null).get()!!
    stub
}

fun <D> CoroutineScope.launchStage(stageComponent:CoroutineScope.(CoroutineScope)-> FxComponent<Stage, D>): Parallel<Stub<D>> = parallel{

    class TestApp : SimpleAppManager<D>() {
        var component: FxComponent<Stage,D>? = null
        init{
            scope.parallel{
                while(component== null){
                    delay(1)
                }
                parallelFx{
                    this@TestApp.showStage(component!!.show())
                }.get()
            }
        }
        override fun configure(): Stub<D> {
            var done = false
            scope.parallel parent@{
                component = stageComponent(this@parent)
                done = true
            }
            while(!done){
                sleep(1)
            }
            return component!!
        }
    }

    val stubLauncher = launchApplicationStub<D, TestApp> {
        application(TestApp())
    }
    val stub = stubLauncher.evolve(null).get()!!
    stub
}

fun <P : Parent,D> CoroutineScope.showOnStage(parentComponent: FxComponent<P, D>): Parallel<Stub<D>> =

        launchStage(fxStage<D> {
            id<StageId>()
            view{configure {  }}
            scene(fxScene{
                tunnel()
                root(parentComponent){root -> Scene(root) }
            })
            stub(org.drx.evoleq.dsl.stub{
                evolve{data -> parentComponent.evolve(data)}
            })
        })

fun <P : Parent,D> CoroutineScope.showOnStage(parentComponent: CoroutineScope.(CoroutineScope)->FxComponent<P, D>): Parallel<Stub<D>> = parallel{

        var component: FxComponent<P,D>? = null

        launchStage(fxStage<D> {
            id<StageId>()
            view{configure {  }}
            scene(fxScene{
                tunnel()

                component = scope.parentComponent(scope)

                root(component!!){root ->
                    Scene(root)
                }
            })
            stub(org.drx.evoleq.dsl.stub{
                evolve{data -> component!!.evolve(data)}
            })
        }).get()}


/*parallel{
    val stageComponent = fxStage<D>(this) {
        id<StageId>()
        view{configure {  }}
        scene(fxScene{
            tunnel()
            root(parentComponent){root -> Scene(root) }
        })
        stub(org.drx.evoleq.dsl.stub{
            evolve{data -> parentComponent.evolve(data)}
        })
    }
    launchTestStage(stageComponent).get()
}*/

fun <N : Node, D> CoroutineScope.showNodeOnStage(nodeComponent: FxComponent<N, D>): Parallel<Stub<D>> = parallel{
    val group = fxGroup<D>{
        tunnel()
        view{configure{}}
        child(nodeComponent)
    }
    showOnStage(group).get()
}

fun <N : Node, D> CoroutineScope.showNodeOnStage(nodeComponent: CoroutineScope.(CoroutineScope)-> FxComponent<N, D>): Parallel<Stub<D>> = parallel{
    val group = fxGroup<D>{
        tunnel()
        view{configure{}}
        child(nodeComponent)
    }
    showOnStage(group).get()
}