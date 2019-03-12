package org.drx.evoleq.fx.exception

import org.drx.evoleq.fx.dsl.ID
import org.drx.evoleq.fx.phase.FxComponentPhase

sealed class FxConfigurationException(open val phase: FxComponentPhase? = null, override val  message: String) : Exception(message) {
    class IdNotSet(override val phase: FxComponentPhase? = null) : FxConfigurationException(phase, "Id not set")
    class IdOfChildNotSet(override val phase: FxComponentPhase? = null) : FxConfigurationException(phase, "Id of child not set")
    class IdOfFxSpecialNotSet(override val phase: FxComponentPhase? = null) : FxConfigurationException(phase, "Id of fx-special not set")
    class StubNotSet(override val phase: FxComponentPhase? = null) : FxConfigurationException(phase, "Stub not set")
    class ViewNotSet(override val phase: FxComponentPhase? = null, val componentId: ID) : FxConfigurationException(phase, "View not set")
    class IsNoStub(override val phase: FxComponentPhase? = null, val componentId: ID) : FxConfigurationException(phase, "FxComponent \"$componentId\" is configured to be no stub")

}