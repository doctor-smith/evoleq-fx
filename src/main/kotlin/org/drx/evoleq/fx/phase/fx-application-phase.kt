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
package org.drx.evoleq.fx.phase

import org.drx.evoleq.flow.Phase

const val  FX_APPLICATION_PHASE_TIMEOUT: Long = 1_000

sealed class FxApplicationPhase(open val errors: ArrayList<Exception> = arrayListOf()) : Phase {
    val defaultTimeout: Long = FX_COMPONENT_PHASE_TIMEOUT

    class Launch()

    class Configure()

    class RunTime()

    class Terminate()
}