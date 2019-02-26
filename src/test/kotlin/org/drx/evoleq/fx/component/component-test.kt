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

import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.drx.evoleq.dsl.conditions
import org.drx.evoleq.dsl.configureSuspended
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Immediate
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.application.AppManager
import org.drx.evoleq.fx.application.BgAppManager
import org.drx.evoleq.fx.dsl.*
import org.drx.evoleq.fx.evolving.AsyncFx
import org.drx.evoleq.fx.evolving.ParallelFx
import org.drx.evoleq.stub.Stub
import org.drx.evoleq.stub.toFlow
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit
import java.lang.Thread.sleep

class ComponentTest {

    @Before
    fun launchBgAppManager() = runBlocking {
        FxToolkit.registerPrimaryStage()
        val m = FxToolkit.setupApplication { BgAppManager() }
    }


    @Test
    fun component() = runBlocking  {
        class Data(val message: String = "", val passedStates: ArrayList<String> = arrayListOf<String>())

        //val passedStates = arrayListOf<String>()

        class App : AppManager<Data>() {
            override fun configure(): Stub<Data> = stub{
                id(App::class)

                val component = fxPane<VBox, Data>{
                    view { node<VBox>{}
                        style("""
                            -fx-color: green;
                            -fx-padding: 10 10 10 10;
                        """.trimIndent())
                    }

                    val defaultHeight = 30
                    child( fxNode<Label,Unit> {
                            stub{}
                            view{
                                node<Label>{
                                    text = "Hello World"
                                }
                                style("""
                                    -fx-text-align: left;
                                    -fx-pref-height: $defaultHeight;
                                """.trimIndent()
                                )
                            }
                        }
                    )
                    child( fxNode<Button,Data>{
                            stub{}
                            view{ node<Button>{
                                    text = "Text"
                                }
                                style("""
                                    -fx-pref-height: $defaultHeight;
                                    -fx-pref-width: 500;
                                    -fx-color: gray;
                                """.trimIndent()
                                )
                            }
                        }
                    )
                    stub{}
                } as FxParentComponent<VBox,Data>
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

        //delay(1_000)

    }


    @Test
    fun parentComponent() = runBlocking {
        val x = fxPane<VBox, Unit>{

            view {
                node<VBox>{
                    spacing = 10.0
                }
            }
            child(fxNode<Button, Unit>{
                view{node(Button("1"))
                }
            })
            child(fxNode<Button, Unit>{
                view{node(Button("2"))}
            })
            child(fxNode<Button, Unit>{
                view{node(Button("3")){

                }}
            })
            child(fxPane<HBox,Unit> {
                view{node(HBox()){

                }}
                child(fxNode<Label,Unit> { view{node(Label("1"))} })
                child(fxNode<Label,Unit> { view{node(Label("2"))} })
                child(fxNode<Label,Unit> { view{node(Label("3"))} })
            })
        }

        class App : AppManager<Unit>() {
            override fun configure(): Stub<Unit> = stub {
                id(App::class)
                evolve{ ParallelFx<Unit>{
                    val stage = Stage()
                    val scene = Scene(x.show())
                    stage.scene = scene
                    showStage(stage)
                    Unit
                } }
            }
        }

        val appLauncherStub = launchApplicationStub<Unit, App> {
            application(App())
        }

        val appStub = appLauncherStub.evolve(null).get()!!

        val res = appStub.evolve(Unit).get()

        delay(2_000)
    }


    @Test
    fun sceneComponent() = runBlocking {
        val sceneComponent = fxScene<VBox, Unit> {
            configure{ }
            sceneConstructorData(width = 300.0, height = 200.0)
            root( fxPane<VBox,Unit>{
                view{
                    node<VBox>{
                        spacing = 2.0
                        padding = Insets(5.0,5.0,5.0,5.0)
                    }
                }
                child(fxNode<Label,Unit>{view{node(Label("label 1"))}})
                child(fxNode<Label,Unit>{view{node<Label>{ text = "label 2"}}})
                child(fxNode<Label,Unit>{view{node(Label("label 3"))}})
                child(fxNode<Button,Nothing>{view{node<Button>{text = "Button"}}})
            } as FxParentComponent<VBox, Unit> )
        }

        class App : AppManager<Unit>() {
            override fun configure(): Stub<Unit> = stub {
                id(App::class)
                evolve{ ParallelFx<Unit>{
                    val stage = Stage()
                    val scene = sceneComponent.show()
                    stage.scene = scene
                    showStage(stage)
                    Unit
                } }
            }
        }

        val appLauncherStub = launchApplicationStub<Unit, App> {
            application(App())
        }

        val appStub = appLauncherStub.evolve(null).get()!!

        val res = appStub.evolve(Unit).get()

        delay(2_000)

    }

    @Test
    fun stageComponent() = runBlocking {

        val stageComponent = fxStage<Unit>{
            configure{
                title = "Title"
            }
            scene<VBox>( fxScene {
                root(fxPane<VBox,Unit> {
                    view{
                        node<VBox>()
                    }
                    child(fxNode<Label,Unit> { view{node<Label>{
                        text = "HUHU"
                        prefWidth = 200.0
                    }} })
                } as FxParentComponent<VBox, Unit>)
            } )

        }

        class App : AppManager<Unit>() {
            override fun configure(): Stub<Unit> = stub {
                id(App::class)
                evolve{ ParallelFx<Unit>{
                    showStage(stageComponent.show())
                    Unit
                } }
            }
        }

        val appLauncherStub = launchApplicationStub<Unit, App> {
            application(App())
        }

        val appStub = appLauncherStub.evolve(null).get()!!

        val res = appStub.evolve(Unit).get()

        delay(2_000)
    }

    @Test fun showStageTwice() = runBlocking {
        val stage = fxStage<Nothing>{
            configure { initStyle(StageStyle.UNDECORATED) }
            scene(fxScene<Pane,Nothing>{
                root(fxPane<Pane, Nothing>{
                    view{node<Pane>{

                    }}
                    child(fxNode<Button, Nothing>{
                        view{node<Button>{text = "Hello"}}
                    })
                } as FxParentComponent<Pane, Nothing>)
            })
        }

        class App : AppManager<Int>() {
            override fun configure(): Stub<Int> = stub {
                id(App::class)
                evolve{ x: Int-> AsyncFx<Int>{
                        val s = stage.show()
                        showStage(s)
                        //sleep(500)
                        hideStage(s)
                        //sleep(500)
                        x+1
                    }
                }
            }
        }

        val appLauncherStub = launchApplicationStub<Int, App> {
            application(App())
        }

        val appStub = appLauncherStub.evolve(null).get()!!

        val res = appStub.toFlow<Int,Boolean>(
                conditions{
                    testObject(true)
                    check { b -> b }
                    updateCondition{x -> x <= 1}
                }
        ).evolve(0).get()
        assert(res == 2 )
        delay(1_000)

    }
}