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
package org.drx.evoleq.fx.exception

import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.fx.phase.FxComponentPhase

sealed class FxConfigurationException(open val phase: FxComponentPhase? = null, override val  message: String) : Exception(message) {
    class IdNotSet(override val phase: FxComponentPhase? = null) : FxConfigurationException(phase, "Id not set")
    class IdOfChildNotSet(override val phase: FxComponentPhase? = null) : FxConfigurationException(phase, "Id of child not set")
    class IdOfFxSpecialNotSet(override val phase: FxComponentPhase? = null) : FxConfigurationException(phase, "Id of fx-special not set")
    class StubNotSet(override val phase: FxComponentPhase? = null) : FxConfigurationException(phase, "Stub not set")
    class ViewNotSet(override val phase: FxComponentPhase? = null, val componentId: ID) : FxConfigurationException(phase, "View not set")
    class IsNoStub(override val phase: FxComponentPhase? = null, val componentId: ID) : FxConfigurationException(phase, "FxComponent \"$componentId\" is configured to be no stub")

    class RunTimeViewTimeout(override val phase: FxComponentPhase? = null, val componentId: ID, val component: FxComponent<*, *>) : FxConfigurationException(phase, "FxComponent \"$componentId\" \n ${component::class} \nhas no available runtimeView")

    class ConfigurationFailed(override val phase: FxComponentPhase? = null, val errors: ArrayList<Exception>) : FxConfigurationException(phase, "Configuration Failure: \n ${renderErrors(errors)}")
    class ConfigurationCancelled() : FxConfigurationException(message = "Configuration cancelled")
}

fun renderErrors(errors: ArrayList<Exception>): String {
    var res = """"""
    errors.forEach { res += "|- ${it.message} \n" }
    return res.trimMargin()
}