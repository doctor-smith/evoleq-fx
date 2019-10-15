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

import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.*
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.parallel
import org.drx.evoleq.dsl.stub
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.stub.Stub
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class ApplicationManager

val DEFAULT_FX_APPLICATION_SCOPE: ()->CoroutineScope =  {
    //println("DEFAULT_FX_APPLICATION_SCOPE")
    CoroutineScope(Job())

}//{GlobalScope}//{CoroutineScope(SupervisorJob())}//

/**
 * You will have to override the configure method.
 * This method will be called during the launching-process
 */
abstract class AppManager<D>() : Application(), Configuration<Stub<D>> {

    val scope: CoroutineScope
    init{
        scope = DEFAULT_FX_APPLICATION_SCOPE()
        SCOPE +scope.coroutineContext
    }

    /**
     * Do not override unless you set TOOLKIT_INITIALIZED = true
     */
    override fun start(primaryStage: Stage?) {

        TOOLKIT_INITIALIZED = true

        /* use this method to enter the fx-thread only */
    }




    companion object Manager{
        val SCOPE: CoroutineScope = DEFAULT_FX_APPLICATION_SCOPE()
        private lateinit var STUB: Stub<*>
        var TOOLKIT_INITIALIZED: Boolean = false
        //private var STUB_INITIALIZED = false
        protected val REGISTRY: HashMap<ID, Stub<*>> by lazy { HashMap<ID, Stub<*>>() }
        var CALLED = -1

        fun waitingForToolkit(): Boolean = !TOOLKIT_INITIALIZED
        /**
         * Launch an application ( and register it ).
         * Calling this function a second time with a second app will not launch the entire
         * application (which is impossibly) - it will only change the implementation of the app-applicationManager
         */
        @Suppress("UNCHECKED_CAST")
        fun <D, A : AppManager<D>> launch(app: A): Parallel<Stub<D>> {
            //SCOPE
            CALLED++
            return SCOPE.parallel {
            // reset state
            var STUB_INITIALIZED = false

            // eventually launch app
            if(waitingForToolkit()) {
                launch {
                    coroutineScope {
                        launch(app::class.java)
                    }
                }
            }
            while(waitingForToolkit()){
                kotlinx.coroutines.delay(1)
            }

            // configure app and
            // set stubConfiguration to return and
            launch { coroutineScope{
                STUB = app.configure()
                STUB.stubs[ApplicationManager::class] = stub<AppManager<D>?>{
                    id(ApplicationManager::class)
                    evolve{ Parallel{app} }
                }
                STUB_INITIALIZED = true
            } }
            while(!STUB_INITIALIZED){
                kotlinx.coroutines.delay(1)
            }
            val stub = STUB as Stub<D>
            REGISTRY[stub.id] = stub
            stub
        }}

        fun <D, C: KClass<out AppManager<D>>> launch(appClass: C): Parallel<Stub<D>> {
            val app = appClass.createInstance()
            return launch(app)
        }

        @Suppress("UNCHECKED_CAST")
        fun <E> appStub(id: ID): Stub<E>? = REGISTRY[id] as Stub<E>?

        fun appStubs() = REGISTRY.values
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

