package org.drx.evoleq.fx.view

import javafx.scene.Node
import javafx.scene.Scene
import javafx.stage.Stage

sealed class FxView<V>(val view: V)
data class FxScene(val scene: Scene) : FxView<Scene>(scene)
data class Fxstage(val stage: Stage) : FxView<Stage>(stage)
data class FxNode(val node: Node) : FxView<Node>(node)