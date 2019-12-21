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
package org.drx.evoleq.fx.application.stub

import javafx.stage.Stage
import org.drx.evoleq.evolving.Evolving
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.fxStage
import org.drx.evoleq.stub.ID
import org.drx.evoleq.stub.Stub

class AppManagerTest {
    class Input

    val appManager = object : AppManager<Input,Int>() {
        /******************************************************************************************************************
         *
         * Abstract Process API
         *
         ******************************************************************************************************************/
        override fun initData(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        /**
         * Define stages to be registered
         */
        override suspend fun stages(): ArrayList<Pair<ID,suspend () -> FxComponent<Stage, Int>>> =
            arrayListOf(Stage::class to stage() )


        /**
         * When stages are registered perform this action
         */
        override suspend fun onStagesRegistered(data: AppMessage<Int>): AppMessage<Int> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        /**
         * When a stage is shown, perform this action
         */
        override suspend fun onStageShown(id: ID, processId: ID?, data: AppMessage.Response.StageShown<Int>): AppMessage<Int> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        /**
         * When a stage is closed / hidden, perform this action
         */
        override suspend fun onStageHidden(id: ID, processId: ID?, data: AppMessage<Int>): AppMessage<Int> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        /**
         * When a new stub arises
         */
        override suspend fun onDriveStub(stub: Stub<Int>, processId: ID?, initialData: Int): AppMessage<Int> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        /**
         *
         */
        override suspend fun onError(error: AppMessage.Process.Error<Int>): AppMessage<Int> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onInput(input: Input, data: Int): Evolving<AppMessage<Int>> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }


    fun stage(): suspend ()->FxComponent<Stage,Int> = {fxStage{}}
}