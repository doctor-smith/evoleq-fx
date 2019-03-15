package org.drx.evoleq.fx.dsl

import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import org.drx.evoleq.fx.component.FxComponent

fun <D> Any?.fxTooltip(configuration: FxComponentConfiguration<Tooltip, D>.()->Unit): FxComponent<Tooltip, D> = fxComponent(configuration)


fun <C : Control, D> FxComponentConfiguration<C, D>.tooltip(component: FxComponent<Tooltip, D>) {
    fxSpecial(component)
    fxRunTime{
        tooltip = component.show()
    }
}