package org.drx.evoleq.fx.dsl

import javafx.scene.control.Label
import org.drx.evoleq.fx.component.FxComponent

fun <D> Any?.fxLabel(configuration: FxComponentConfiguration<Label, D>.()->Unit): FxComponent<Label, D> = fxComponent(configuration)
