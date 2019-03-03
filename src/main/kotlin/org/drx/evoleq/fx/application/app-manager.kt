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
package org.drx.evoleq.fx.application

import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.StubConfiguration
import org.drx.evoleq.dsl.configure
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * You will have to override the configure method.
 * This method will be called during the launching-process
 */
abstract class AppManager<D> : Application(), Configuration<Stub<D>> {
    /**
     * Do not override unless you set TOOLKIT_INITIALIZED = true
     */
    override fun start(primaryStage: Stage?) {
        TOOLKIT_INITIALIZED = true
        /* use this method to enter the fx-thread only */
    }


    companion object Manager{
        private lateinit var STUB: Stub<*>
        private var TOOLKIT_INITIALIZED: Boolean = false
        //private var STUB_INITIALIZED = false
        protected val REGISTRY: HashMap<KClass<*>, Stub<*>> by lazy { HashMap<KClass<*>, Stub<*>>() }
        var CALLED = -1

        fun waitingForToolkit(): Boolean = !TOOLKIT_INITIALIZED
        //fun waitingForStub(): Boolean = !STUB_INITIALIZED
        /**
         * Launch an application ( and register it ).
         * Calling this function a second time with a second app will not launch the entire
         * application (which is impossibly) - it will only change the implementation of the app-manager
         */
        @Suppress("UNCHECKED_CAST")
        fun <D, A : AppManager<D>> launch(app: A): Parallel<Stub<D>> {
            CALLED++
            return Parallel {
            // reset state
            var STUB_INITIALIZED = false

            // eventually launch app
            if(/*CALLED == 0 &&*/ waitingForToolkit()) {
                //CALLED++
                scope.launch {
                    coroutineScope {
                        launch(app::class.java)
                    }
                }
            }
            while(waitingForToolkit()){
                //println("waiting for toolkit ...")
                kotlinx.coroutines.delay(1)
            }

            // configure app and
            // set stubConfiguration to return and
            scope.launch { coroutineScope{
                AppManager.STUB = app.configure()
                STUB_INITIALIZED = true
            } }
            while(!STUB_INITIALIZED){
                kotlinx.coroutines.delay(1)
            }
            val stub = AppManager.STUB as Stub<D>
            REGISTRY[stub.id] = stub
            stub
        }}

        fun <D, C: KClass<out AppManager<D>>> launch(appClass: C): Parallel<Stub<D>> {
            val app = appClass.createInstance()
            return Manager.launch(app)
        }

        @Suppress("UNCHECKED_CAST")
        fun <E> appStub(id: KClass<*>): Stub<E>? = REGISTRY[id] as Stub<E>?

        fun appStubs() = REGISTRY.values
/*
        fun shutDown(idConfiguration: KClass<*>) : Boolean {
            val stubConfiguration = REGISTRY[idConfiguration]
            if(stubConfiguration != null){
                REGISTRY.remove(idConfiguration)
            }
            return true
        }
*/
        /*
        fun <D, A: AppManager<D>> restart(app: A): Parallel<Stub<D>> = when(AppManager.TOOLKIT_INITIALIZED){
            true -> Parallel {
                AppManager.STUB = app.configure()

                AppManager.STUB as Stub<D>
            }
            false ->  AppManager.launch(app)
        }
        */

    }


    /******************************************************
     *
     * AppManager API
     *
     ******************************************************/

    open fun showStage(stage: Stage) {
        stage.show()
    }

    open fun showStageAndWait(stage: Stage) {
        stage.showAndWait()
    }

    open fun hideStage(stage: Stage) {
        stage.hide()
    }


}

class BgAppManager : AppManager<Unit>(){
    override fun configure(): Stub<Unit> = stub{
        id(BgAppManager::class)
    }
}

