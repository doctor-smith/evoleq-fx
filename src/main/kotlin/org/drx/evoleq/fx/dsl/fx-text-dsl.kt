package org.drx.evoleq.fx.dsl

import javafx.scene.text.Text
import org.drx.evoleq.fx.component.FxComponent

fun <D> Any?.fxText(configuration: FxComponentConfiguration<Text, D>.()->Unit): FxComponent<Text, D> = fxComponent(configuration)