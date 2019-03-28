package org.drx.evoleq.fx.dsl

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.shape.*
import org.drx.evoleq.fx.component.FxComponent

@Suppress("unused")
fun <D> FxComponentConfiguration<out Any, *>.fxPath(configuration: FxComponentConfiguration<Path, D>.()->Unit): FxComponent<Path, D> {
    return fxComponent(configuration)
}

fun <D> fxPath(configuration: FxComponentConfiguration<Path, D>.()->Unit): FxComponent<Path, D> {
    return fxComponent(configuration)
}

fun <D> FxComponentConfiguration<Path, D>.elements(configure: ObservableList<PathElement>.()->Unit) {
    fxRunTime{ elements.configure() }
}

fun ObservableList<PathElement>.moveTo(configuration: MoveTo.()->Unit){
    val moveTo = MoveTo()
    moveTo.configuration()
    add(moveTo)
}

fun ObservableList<PathElement>.lineTo(configuration: LineTo.()->Unit){
    val lineTo = LineTo()
    lineTo.configuration()
    add(lineTo)
}

fun ObservableList<PathElement>.hLineTo(configuration: HLineTo.()->Unit){
    val hLineTo = HLineTo()
    hLineTo.configuration()
    add(hLineTo)
}

fun ObservableList<PathElement>.vLineTo(configuration: VLineTo.()->Unit){
    val vLineTo = VLineTo()
    vLineTo.configuration()
    add(vLineTo)
}

fun ObservableList<PathElement>.arcTo(configuration: ArcTo.()->Unit){
    val arcTo = ArcTo()
    arcTo.configuration()
    add(arcTo)
}

fun ObservableList<PathElement>.quadCurveTo(configuration: QuadCurveTo.()->Unit){
    val quadCurveTo = QuadCurveTo()
    quadCurveTo.configuration()
    add(quadCurveTo)
}

/**
 * Velocity at the end-point
 */
fun QuadCurveTo.endVelocity(setup: Point2DConfiguration.()->Unit){
    val point = point(setup)
    controlX = -point.x / 2 + x
    controlY = -point.y / 2 + y
}

fun QuadCurveTo.startVelocity(startPoint: Point2D,setup: Point2DConfiguration.()->Unit){
    val point = point(setup)
    controlX = startPoint.x + point.x / 2
    controlY = startPoint.y + point.y / 2
}



fun ObservableList<PathElement>.cubicCurveTo(
        startVelocity: Point2D? = null,
        endVelocity: Point2D? = null,
        configuration: CubicCurveTo.()->Unit
){
    val cubicCurveTo = CubicCurveTo()
    cubicCurveTo.configuration()
    if(startVelocity != null) {
        val endPoint = this@cubicCurveTo.endPoint()
        cubicCurveTo.startVelocity(endPoint!!){
            x = startVelocity.x
            y = startVelocity.y
        }
    }
    if(endVelocity != null) {
        cubicCurveTo.endVelocity {
            x = endVelocity.x
            y = endVelocity.y
        }
    }
    add(cubicCurveTo)
}

fun ObservableList<PathElement>.startPoint(): Point2D? = when(this.size) {
    0 -> null
    else -> when (val p = first()) {
        is MoveTo -> Point2D(p.x, p.y)
        else ->null
    }
}

fun ObservableList<PathElement>.endPoint(): Point2D? = when(this.size) {
    0 -> null
    1 -> when(val p = last()) {
        is MoveTo -> Point2D(p.x,p.y)
        else ->null
    }
    else -> when(val p = last()) {
        is MoveTo -> Point2D(p.x,p.y)
        is LineTo -> Point2D(p.x,p.y)
        is QuadCurveTo -> Point2D(p.x,p.y)
        is CubicCurveTo -> Point2D(p.x,p.y)
        is ArcTo -> Point2D(p.x,p.y)
        is HLineTo -> Point2D(p.x, endY()!!)

        is VLineTo -> Point2D(endX()!!,p.y)
        is ClosePath -> startPoint()
        else -> null
    }
}
fun ObservableList<PathElement>.endX() : Double? = when(this.size) {
    0 -> null
    1 -> when(val p = last()) {
        is MoveTo -> p.x
        else ->null
    }
    else -> when(val p = last()) {
        is MoveTo -> p.x
        is LineTo -> p.x
        is QuadCurveTo -> p.x
        is CubicCurveTo -> p.x
        is HLineTo -> p.x
        is VLineTo -> {
            FXCollections.observableArrayList(dropLast(1)).endX()
        }
        is ArcTo -> p.x
        is ClosePath -> startPoint()!!.x
        else ->null
    }
}

fun ObservableList<PathElement>.endY() : Double? = when(this.size) {
    0 -> null
    1 -> when(val p = last()) {
        is MoveTo -> p.y
        else ->null
    }
    else -> when(val p = last()) {
        is MoveTo -> p.y
        is LineTo -> p.y
        is QuadCurveTo -> p.y
        is CubicCurveTo -> p.y
        is VLineTo -> p.y
        is HLineTo -> {
            FXCollections.observableArrayList(dropLast(1)).endY()
        }
        is ArcTo -> p.y
        is ClosePath -> startPoint()!!.y
        else ->null
    }
}

fun CubicCurveTo.startVelocity(startPoint: Point2D,setup: Point2DConfiguration.()->Unit){
    val point = point(setup)
    controlX1 = startPoint.x + point.x / 3
    controlY1 = startPoint.y + point.y / 3

}

fun CubicCurveTo.endVelocity(setup: Point2DConfiguration.()->Unit){
    val point = point(setup)

    controlX2 = - point.x / 3 + x
    controlY2 = - point.y / 3 + y
}

fun ObservableList<PathElement>.closePath(configuration: ClosePath.()->Unit){
    val closePath = ClosePath()
    closePath.configuration()
    add(closePath)
}
fun ObservableList<PathElement>.closePath(){
    val closePath = ClosePath()
    add(closePath)
}

fun Path.add(element: PathElement) {
    elements.add(element)
}

fun Path.remove(element: PathElement) {
    elements.remove(element)
}



/*
fun <D> FxComponentConfiguration<Path, D>.moveTo(moveTo: MoveTo) {
    this.fxRunTime{ elements.add(moveTo) }
}
fun <D> FxComponentConfiguration<Path, D>.moveTo(configuration: MoveTo.()->Unit) {
    val moveTo = MoveTo()
    moveTo.configuration()
    moveTo(moveTo)
}

fun <D> FxComponentConfiguration<Path, D>.lineTo(lineTo: LineTo) =  {

    fxRunTime{ elements.add(lineTo) }
}
fun <D> FxComponentConfiguration<Path, D>.lineTo(configuration: LineTo.()->Unit) {
    val lineTo = LineTo()
    lineTo.configuration()
    lineTo(lineTo)
}

fun <D> FxComponentConfiguration<Path, D>.hLineTo(lineTo: HLineTo) {

    this.fxRunTime{ this.elements.add(lineTo) }
}
fun <D> FxComponentConfiguration<Path, D>.hLineTo(configuration: HLineTo.()->Unit) {
    val lineTo = HLineTo()
    lineTo.configuration()
    hLineTo(lineTo)
}

fun <D> FxComponentConfiguration<Path, D>.vLineTo(vLineTo: VLineTo) {
    this.fxRunTime{ this.elements.add(vLineTo) }
}
fun <D> FxComponentConfiguration<Path, D>.vLineTo(configuration: VLineTo.()->Unit) {
    val vLineTo = VLineTo()
    vLineTo.configuration()
    vLineTo(vLineTo)
}

fun <D> FxComponentConfiguration<Path, D>.cubicCurveTo(cubicCurveTo: CubicCurveTo) {
    this.fxRunTime{ this.elements.add(cubicCurveTo) }
}
fun <D> FxComponentConfiguration<Path, D>.cubicCurveTo(configuration: CubicCurveTo.()->Unit) {
    val cubicCurveTo = CubicCurveTo()
    cubicCurveTo.configuration()
    cubicCurveTo(cubicCurveTo)
}

fun <D> FxComponentConfiguration<Path, D>.quadCurveTo(quadCurveTo: QuadCurveTo) {
    this.fxRunTime{ this.elements.add(quadCurveTo) }
}
fun <D> FxComponentConfiguration<Path, D>.quadCurveTo(configuration: QuadCurveTo.()->Unit) {
    val quadCurveTo = QuadCurveTo()
    quadCurveTo.configuration()
    quadCurveTo(quadCurveTo)
}

fun <D> FxComponentConfiguration<Path, D>.arcTo(arcTo: ArcTo) {
    this.fxRunTime{ this.elements.add(arcTo) }
}
fun <D> FxComponentConfiguration<Path, D>.arcTo(configuration: ArcTo.()->Unit) {
    val arcTo = ArcTo()
    arcTo.configuration()
    arcTo(arcTo)
}

fun <D> FxComponentConfiguration<Path, D>.closePath(closePath: ClosePath) {
    this.fxRunTime{ this.elements.add(closePath) }
}
fun <D> FxComponentConfiguration<Path, D>.closePath(configuration: ClosePath.()->Unit) {
    val closePath = ClosePath()
    closePath.configuration()
    closePath(closePath)
}
*/

