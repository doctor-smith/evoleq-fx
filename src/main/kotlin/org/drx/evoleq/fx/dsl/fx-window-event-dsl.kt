package org.drx.evoleq.fx.dsl

import javafx.stage.Stage
import javafx.stage.Window
import javafx.stage.WindowEvent

fun <W:  Window> W.onCloseRequest(action: WindowEvent.()->Unit): W {
    this.setOnCloseRequest(action)
    return this
}

fun <W:  Window> W.onShown(action: WindowEvent.()->Unit): W {
    this.setOnShown(action)
    return this
}

fun <W:  Window> W.onShowing(action: WindowEvent.()->Unit): W {
    this.setOnShowing(action)
    return this
}

fun <W:  Window> W.onHiding(action: WindowEvent.()->Unit): W {
    this.setOnHiding(action)
    return this
}

fun <W:  Window> W.onHidden(action: WindowEvent.()->Unit): W {
    this.setOnHidden(action)
    return this
}