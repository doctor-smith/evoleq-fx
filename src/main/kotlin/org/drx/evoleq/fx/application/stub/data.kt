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
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.stub.ID
import org.drx.evoleq.stub.Stub

sealed class AppMessage<Data> {
    sealed class Request<Data> : AppMessage<Data>() {
        data class ShowStage<Data>(val id: ID) : Request<Data>()
        data class HideStage<Data>(val id: ID): Request<Data>()
        data class RegisterStages<Data>(val stages:  ArrayList<Pair<ID, ()-> FxComponent<Stage, Data>>>) : Request<Data>()
    }
    sealed class Response<Data> : AppMessage<Data>() {
        data class StageShown<Data>(val stub: Stub<Data>): Response<Data>()
        data class StageHidden<Data>(val id: ID): Response<Data>()
        class StagesRegistered<Data> : Response<Data>()
    }
    sealed class Process<Data> : AppMessage<Data>() {
        class Start<Data> : Process<Data>()
        data class DriveStub<Data>(val stub: Stub<Data>, val initialData: Data) : Process<Data>()
        data class Error<Data>(val stageId: ID, val data: Data, val throwable: Throwable): Process<Data>()
        class Terminate<Data> : Process<Data>()
        class Terminated<Data> : Process<Data>()

    }
}
