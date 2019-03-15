package org.drx.evoleq.fx.dsl

import javafx.scene.image.ImageView
import org.drx.evoleq.fx.component.FxComponent


fun <D> Any?.fxImageView(configuration: FxComponentConfiguration<ImageView, D>.()->Unit): FxComponent<ImageView, D> = fxComponent(configuration)