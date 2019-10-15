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
package org.drx.evoleq.fx.application.configration

import javafx.stage.Stage
import kotlinx.coroutines.GlobalScope
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.fx.dsl.launchApplicationStub
import org.drx.evoleq.fx.dsl.parallelFx
import org.drx.evoleq.fx.test.dsl.fxRunTest
import org.drx.evoleq.stub.Stub
import org.junit.*

class AppManagerTest {



    @Test fun launchApp() = fxRunTest{//runBlocking {
        class App: AppManager<Unit>() {
            override fun configure(): Stub<Unit> = scope.stub {
                id(App::class)
                evolve { parallelFx {
                    //println("fx")
                    val stage = Stage()
                    stage.title = "PIPI"
                    showStage(stage)
                } }
            }
        }

        val stub = AppManager.launch(app=App()).get()
        assert(stub.id == App::class)
        val u = stub.evolve(Unit).get()
        assert(true)
        //delay(1_000)
    }
    @Test fun launchFromClass() = fxRunTest{//runBlocking{
        class App: AppManager<Unit>() {
            override fun configure(): Stub<Unit> = GlobalScope.stub {
                id(App::class)
                evolve { GlobalScope.parallelFx {
                    //println("fx")
                    val stage = Stage()
                    stage.title = "PIPI"
                    showStage(stage)
                } }
            }
        }

        val stub = AppManager.launch(App::class).get()
        assert(stub.id == App::class)
        val u = stub.evolve(Unit).get()
        assert(true)
    }

    @Test fun launchViaDsl() = fxRunTest{//runBlocking{

        class App: AppManager<Boolean>() {
            override fun configure(): Stub<Boolean> = GlobalScope.stub{
                id(App::class)
                evolve{ GlobalScope.parallelFx{
                    val stage = Stage()
                    stage.title = "PIPI"
                    showStage(stage)
                    true
                } }
            }
        }

        val stubLauncher = launchApplicationStub<Boolean,App> {
            application(App())
        }

        val applicationStub = stubLauncher.evolve(null).get()!!

        val u = applicationStub.evolve(false).get()

        assert(applicationStub.id == App::class)
        assert(u)

    }

    @Test fun launchTwoAppsAndCheckRegistry() = fxRunTest{//runBlocking {
        class App1: AppManager<Boolean>() {
            override fun configure(): Stub<Boolean> = scope.stub{
                id(App1::class)
                evolve{ parallelFx{
                    val stage = Stage()
                    stage.title = "PIPI_1"
                    showStage(stage)
                    true
                } }
            }
        }

        class App2: AppManager<Boolean>() {
            override fun configure(): Stub<Boolean> = scope.stub{
                id(App2::class)
                evolve{ parallelFx{
                    val stage = Stage()
                    stage.title = "PIPI_2"
                    showStage(stage)
                    true
                } }
            }
        }

        val stub1 = AppManager.launch(app=App1()).get()
        val v1 = stub1.evolve(false).get()
        assert(v1)
        //delay(500)
        assert(stub1.id == App1::class )
        assert(AppManager.appStub<Boolean>(stub1.id) != null)

        val stub2 = AppManager.launch(app=App2()).get()
        val v2 = stub2.evolve(false).get()
        assert(v2)
        assert(stub2.id == App2::class )
        //delay(500)

        assert(AppManager.appStub<Boolean>(stub2.id) != null)
        assert(AppManager.appStub<Boolean>(stub1.id) != null)

        //delay(500)

    }
}
