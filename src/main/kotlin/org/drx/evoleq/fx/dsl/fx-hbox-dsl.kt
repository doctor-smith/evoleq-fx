package org.drx.evoleq.fx.dsl

import javafx.scene.layout.HBox
import org.drx.evoleq.fx.component.FxComponent

fun <D> Any?.fxHBox(configuration: FxComponentConfiguration<HBox, D>.()->Unit): FxComponent<HBox, D> = fxComponent(configuration)