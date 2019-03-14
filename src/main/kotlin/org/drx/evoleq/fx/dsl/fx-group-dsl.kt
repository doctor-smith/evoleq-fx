package org.drx.evoleq.fx.dsl

import javafx.scene.Group
import javafx.scene.control.Button
import org.drx.evoleq.fx.component.FxComponent

/**
 * Button
 */
fun <D> Any?.fxGroup(configuration: FxComponentConfiguration<Group, D>.()->Unit): FxComponent<Group, D> {
    return fxComponent(configuration)
}