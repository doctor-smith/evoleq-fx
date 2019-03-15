package org.drx.evoleq.fx.dsl

import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import org.drx.evoleq.fx.component.FxComponent


fun <D> Any?.fxTabPane(configuration: FxComponentConfiguration<TabPane, D>.()->Unit): FxComponent<TabPane, D> = fxComponent(configuration)

fun <D> FxComponentConfiguration<TabPane, D>.tab(component: FxComponent<Tab, D>) {
    fxSpecial(component)
    fxRunTime { tabs.add(component.show()) }
}
fun <D> FxComponentConfiguration<TabPane, D>.side(side: Side) {
    fxRunTime { this.side = side  }
}
fun <D> Any?.fxTab(configuration: FxComponentConfiguration<Tab, D>.()->Unit): FxComponent<Tab, D> = fxComponent(configuration)

fun<D> FxComponentConfiguration<Tab, D>.content(component: FxComponent<out Node,*>) {
    fxSpecial(component)
    fxRunTime { content = component.show() }
}
