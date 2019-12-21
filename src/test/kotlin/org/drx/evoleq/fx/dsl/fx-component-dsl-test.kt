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
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.*
import org.drx.evoleq.coroutines.onScope
import org.drx.evoleq.coroutines.onScopeSuspended
import org.drx.evoleq.dsl.parallel
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.fx.stub.NoStub
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.drx.evoleq.fx.util.showOnStage
import org.drx.evoleq.fx.util.showStage
import org.drx.evoleq.stub.Key1
import org.drx.evoleq.stub.Key2
import org.drx.evoleq.stub.Stub
import org.junit.Test
import java.lang.Thread.sleep

class FxComponentDslTest {

    @Test
    fun children()=fxRunTest{// {
        val c = onScopeSuspended(){scope: CoroutineScope ->fxComponent<Group,Nothing>(scope) {
            tunnel()
            view { configure {  } }
            children {
                child(fxStackPane<Int> {
                    id<Key1>()
                    view { configure {  } }
                    stub(stub{})
                })
                child(fxStackPane<Int> {
                    id<Key2>()
                    view { configure {  } }
                    stub(stub{})
                })
            }
        }}
        val x = c(GlobalScope,GlobalScope)//GlobalScope.parallel{c(this@parallel)}.get()
        delay(100)
        println(x.stubs.size)
        assert(x.stubs.size == 2)
    }

    //@Test
    fun configureWithConstructorData() = fxRunTest{//runBlocking {
        var done1: Boolean = false
        var done2: Boolean = false
        val c = onScopeSuspended{s: CoroutineScope->fxGroup<Nothing>(s){
            noStub()
            view{configure{}}
            child(fxButton {
                noStub()
                view{configure("Hello1"){
                    done1 = true
                }}
            })
            child(fxButton {
                noStub()
                view{configure(constructor("Hello2")){
                    done2 = true
                }}
            })
        }}
        val x = c(GlobalScope,GlobalScope)//GlobalScope.parallel{c(this@parallel)}.get()
        delay(300)
        assert(done1 && done2)
        //delay(10_000)
    }

    @Test fun cancellation() = fxRunTest{

        var buttonJob : Job? = null
        var buttonJobCancelled = false

        val stage = onScopeSuspended{scope: CoroutineScope -> fxStage<Int>(scope) {
            id<Stage>()
            view{configure{}}
            scene(fxScene{
                id<Scene>()
                view{configure{}}
                root(fxButton{
                    id<Button>()
                    view{configure{
                        text = "Hi There"
                    }}
                    stub(stub{})
                    parallel {
                        buttonJob = scope.launch {
                            println("launched button job")
                            delay(10_000)
                        }
                    }

                }){root->Scene(root)}
                stub(stub{})

            })
            stub(stub{evolve{ parallel{
                println("evolving $it")
                //delay(5_000)
                5
            }}})
                fxRunTime {
                    sleep(1_000)
                    scope.cancel()
                    buttonJobCancelled = true
                    println("cancelled scope of stage")
                    sleep(2_000)

                }
        }}
        //val stub = parallel{showStage(stage).get()}

        val parallelStub = parallel {
             showStage(stage).get()
        }
        parallel {
            val x = parallelStub.get().evolve(0).get()
            println("   > got $x")
        }
        parallel {
            while(!buttonJobCancelled){
                delay(1)
            }
            println("make assertion")
            assert(buttonJob!!.isCancelled)

        }.get()



    }

    @Test
    fun orderOfCreation() = fxRunTest {
         val component = onScopeSuspended{scope: CoroutineScope ->fxStage<Nothing> (scope) {
             id<Stage>()
             view { configure {} }
             scene(fxScene {
                 id<Scene>()
                 view { configure {} }
                 root(fxVBox {
                     id<VBox>()
                     view { configure {} }
                     child(fxButton {
                         id<Button>()
                         view { configure {} }
                         stub(stub {})
                         println(3)
                     })
                     stub(stub {})
                     println(2)
                 })
                 stub(stub {})
                 println(1)
             })
             stub(stub {})
             println(0)
         }}
        component(GlobalScope, GlobalScope)
    }


    @Test(expected = Exception::class)
    fun doNotSetId() = fxRunTest {
        val component = onScopeSuspended{scope: CoroutineScope ->fxComponent<Button, Nothing> (scope) {
            view{configure{}}
            stub(stub{})
        } }//parallel{component(GlobalScope)}.get()
        component(GlobalScope)
    }
    @Test(expected = Exception::class)
    fun doNotSetStub() = fxRunTest {
        val component = onScopeSuspended{scope: CoroutineScope ->fxComponent<Button, Nothing> (scope) {
            id<NoStub<Nothing>>()
            view{configure{}}
        } }//parallel{component(GlobalScope)}.get()
        component(GlobalScope)
    }
    @Test(expected = Exception::class)
    fun doNotSetView() = fxRunTest {
        val component = onScopeSuspended{scope: CoroutineScope ->fxComponent<Button, Nothing> (scope) {
            id<NoStub<Nothing>>()
            stub(stub{})
        } }//parallel{component(GlobalScope)}.get()
        component(GlobalScope)
    }



}
