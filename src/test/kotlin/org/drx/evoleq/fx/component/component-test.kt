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
package org.drx.evoleq.fx.component

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.dsl.conditions
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.application.AppManager
import org.drx.evoleq.fx.dsl.fxComponent
import org.drx.evoleq.fx.dsl.launchApplicationStub
import org.drx.evoleq.fx.evolving.ParallelFx
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import org.junit.Test
import java.lang.Thread.sleep

class ComponentTest {
    @Test fun component() = runBlocking  {
        class Data(val message: String = "", val passedStates: ArrayList<String> = arrayListOf<String>())

        //val passedStates = arrayListOf<String>()

        class App : AppManager<Data>() {
            override fun configure(): Stub<Data> = stub{
                id(App::class)

                val component = fxComponent<VBox, Data>{
                    view {
                        node(VBox()){
                            val defaultHeight = 30;
                            children.addAll(
                                fxComponent<Label,Unit> {
                                    stub{}
                                    view{
                                        node(Label()){
                                            text = "Hello World"
                                        }
                                        style("""
                                            -fx-text-align: left;
                                            -fx-pref-height: $defaultHeight;
                                        """.trimIndent())
                                    }
                                }.show(),

                                fxComponent<Button,Data>{
                                    stub{}
                                    view{
                                        node(Button()){
                                            text = "Text"
                                        }
                                        style("""
                                            -fx-pref-height: $defaultHeight;
                                            -fx-pref-width: 500;
                                            -fx-color: gray;
                                        """.trimIndent())
                                    }
                                }.show()

                            )

                        }
                        style("""
                            -fx-color: green;
                            -fx-padding: 10 10 10 10;
                        """.trimIndent())
                    }
                    stub{}
                }
                lateinit var stage: Stage
                evolve{ data -> when(data.message) {
                    "show" ->ParallelFx {
                        data.passedStates.add(data.message)
                        println(data.message)
                        stage = Stage()
                        val scene = Scene(component.show())
                        stage.scene = scene
                        showStage(stage)

                        Data("shown",data.passedStates)
                    }
                    "shown" -> Parallel {
                        data.passedStates.add(data.message)
                        println(data.message)
                        delay(2_000)
                        /*
                        var waiting = true
                        GlobalScope.launch {
                            delay(2_000)
                            waiting = false
                        }
                        while(waiting){
                            sleep(10)
                        }
                        */
                        //Thread.currentThread().sleep(2_000)
                        Data("close",data.passedStates)
                    }
                    "close" -> ParallelFx {
                        data.passedStates.add(data.message)
                        println(data.message)
                        hideStage(stage)
                        Data("closed",data.passedStates)
                    }
                    "closed" -> Immediate{
                        data.passedStates.add(data.message)
                        println(data.message)
                        data
                    }
                    else -> Parallel{
                        data.passedStates.add(data.message)
                        data
                    }
                } }
            }
        }

        val appLauncherStub = launchApplicationStub<Data, App> {
            application(App())
        }

        val appStub = appLauncherStub.evolve(null).get()!!

        val data = appStub.toFlow<Data, Boolean>(conditions{
            testObject(true)
            check{b->b}
            updateCondition { data: Data -> data.message != "closed" }
        }).evolve(Data("show")).get()

        println(data.passedStates)
        assert(data.passedStates == arrayListOf("show", "shown", "close"))

        delay(1_000)

    }
}