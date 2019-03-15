package org.drx.evoleq.fx.flow

object Config{
    val base: String by lazy{ this::class.java.getResource(".").toExternalForm().replace("/classes/", "/resources/") }
    val style1 = base + "style1.css"
}