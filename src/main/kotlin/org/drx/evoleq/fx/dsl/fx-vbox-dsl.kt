package org.drx.evoleq.fx.dsl

import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.drx.evoleq.fx.component.FxComponent

fun <D> Any?.fxVBox(configuration: FxComponentConfiguration<VBox, D>.()->Unit): FxComponent<VBox, D> = fxComponent(configuration)