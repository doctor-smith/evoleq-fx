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

sealed class AppMessage<Data>(
        open val data: Data
) {

    sealed class Request<Data>(
            override val data: Data
    ) : AppMessage<Data>(
            data
    ) {
        data class ShowStage<Data>(
                val id: ID,
                val processId: ID? = null,
                override val data: Data
        ) : Request<Data>(
                data
        )

        data class HideStage<Data>(
                val id: ID,
                val processId: ID? = null,
                override val data: Data
        ): Request<Data>(
                data
        )

        data class RegisterStages<Data>(
                val stages:  ArrayList<Pair<ID, ()-> FxComponent<Stage, Data>>>,
                override val data: Data
        ) : Request<Data>(
                data
        )
    }

    sealed class Response<Data>(
            override val data: Data
    ) : AppMessage<Data>(
            data
    ) {
        data class StageShown<Data>(
                val stub: Stub<Data>,
                val processId: ID? = null,
                override val data: Data
        ): Response<Data>(
                data
        )

        data class StageHidden<Data>(
                val id: ID,
                val processId: ID? = null,
                override val data: Data
        ): Response<Data>(
                data
        )
        class StagesRegistered<Data>(
                override val data: Data
        ) : Response<Data>(
                data
        )
    }

    sealed class Process<Data>(
            override val data: Data
    ) : AppMessage<Data>(
            data
    ) {
        class Start<Data>(
                override val data: Data
        ) : Process<Data>(
                data
        )

        data class DriveStub<Data>(
                val stub: Stub<Data>,
                val processId: ID? = null,
                override val data: Data
        ) : Process<Data>(
                data
        )

        data class Wait<Data>(
                override val data: Data
        ) : Process<Data>(
                data
        )

        data class Error<Data>(
                val stageId: ID,
                val processId: ID? = null,
                override val data: Data,
                val throwable: Throwable
        ): Process<Data>(
                data
        )

        class Terminate<Data>(
                override val data: Data
        ) : Process<Data>(
                data
        )

        class Terminated<Data>(
                override val data: Data
        ) : Process<Data>(
                data
        )
    }
}
