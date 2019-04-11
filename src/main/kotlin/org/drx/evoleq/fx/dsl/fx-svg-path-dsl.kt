/**
 * Copyright (c) 2019 Dr. Florian Schmidt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drx.evoleq.fx.dsl

import javafx.scene.shape.SVGPath
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.fx.component.FxComponent


@Suppress("unused")
fun <D> FxComponentConfiguration<out Any, *>.fxSvgPath(configuration: FxComponentConfiguration<SVGPath, D>.()->Unit): FxComponent<SVGPath, D> {
    return fxComponent(configuration)
}

@Suppress("unused")
fun <D> fxSvgPath(configuration: FxComponentConfiguration<SVGPath, D>.()->Unit): FxComponent<SVGPath, D> {
    return fxComponent(configuration)
}

sealed class SVGPathElement {
    data class M(val x: Int, val  y: Int) : SVGPathElement()
    data class m(val x: Int, val  y: Int) : SVGPathElement()
    // lines
    data class L(val x: Int, val y: Int) : SVGPathElement()
    data class l(val x: Int, val y: Int) : SVGPathElement()
    data class H(val x: Int) : SVGPathElement()
    data class h(val x: Int) : SVGPathElement()
    data class V(val y: Int) : SVGPathElement()
    data class v(val y: Int) : SVGPathElement()
    // cubic bezier curves
    data class C(val controlX1: Int, val controlY1: Int, val controlX2: Int, val controlY2: Int, val x: Int, val y: Int) : SVGPathElement()
    data class c(val controlX1: Int, val controlY1: Int, val controlX2: Int, val controlY2: Int, val x: Int, val y: Int) : SVGPathElement()
    data class S(val controlX2: Int, val controlY2: Int, val x: Int, val y: Int) : SVGPathElement()
    data class s(val controlX2: Int, val controlY2: Int, val x: Int, val y: Int) : SVGPathElement()

    // quadratic bezier curve
    data class Q(val controlX1: Int, val controlY1: Int, val x: Int, val y: Int) : SVGPathElement()
    data class q(val controlX1: Int, val controlY1: Int, val x: Int, val y: Int) : SVGPathElement()
    data class T(val x: Int, val y: Int) : SVGPathElement()
    data class t(val x: Int, val y: Int) : SVGPathElement()

    // elliptic arc
    data class A(val rx: Int, val ry: Int, val xAxisRotation: Int, val largeArcFlag: Int, val sweepFlagX: Int, val sweepFlagY: Int): SVGPathElement()
    data class a(val rx: Int, val ry: Int, val xAxisRotation: Int, val largeArcFlag: Int, val sweepFlagX: Int, val sweepFlagY: Int): SVGPathElement()

    object Z : SVGPathElement()

    fun svg(): String = when(this) {
        is M -> "M$x,$y"
        is m -> "m$x,$y"
        is L -> "L$x,$y"
        is l -> "l$x,$y"
        is H -> "H$x"
        is h -> "h$x"
        is V -> "V$y"
        is v -> "v$y"
        is C -> "C$controlX1,$controlY1 $controlX2,$controlY2 $x,$y"
        is c -> "c$controlX1,$controlY1 $controlX2,$controlY2 $x,$y"
        is S -> "S$controlX2,$controlY2 $x,$y"
        is s -> "s$controlX2,$controlY2 $x,$y"
        is Q -> "Q$controlX1,$controlY1 $x,$y"
        is q -> "q$controlX1,$controlY1 $x,$y"
        is T -> "T$x,$y"
        is t -> "t$x,$y"
        is A -> "A$rx,$ry $xAxisRotation $largeArcFlag, $sweepFlagX, $sweepFlagY"
        is a -> "A$rx,$ry $xAxisRotation $largeArcFlag, $sweepFlagX, $sweepFlagY"
        is Z -> "Z"
            // a25,25 -30 0,1 50,-25
    }
}
fun ArrayList<SVGPathElement>.svg(): String = map{ it.svg() }.reduce{s, t -> "$s $t" }

abstract class SVGPathElementConfiguration : Configuration<SVGPathElement> {
    var absolute = true
    var x: Int? = null
    var y: Int? = null
}
class MoveToConfiguration : SVGPathElementConfiguration() {
    override fun configure(): SVGPathElement = when(absolute) {
        true -> SVGPathElement.M(x!!,y!!)
        false -> SVGPathElement.m(x!!,y!!)
    }
}
class LineToConfiguration : SVGPathElementConfiguration() {
    override fun configure(): SVGPathElement = when(absolute) {
        true -> SVGPathElement.L(x!!,y!!)
        false -> SVGPathElement.l(x!!,y!!)
    }
}
class HLineToConfiguration : SVGPathElementConfiguration() {
    override fun configure(): SVGPathElement = when(absolute) {
        true -> SVGPathElement.H(x!!)
        false -> SVGPathElement.h(x!!)
    }
}
class VLineToConfiguration : SVGPathElementConfiguration() {
    override fun configure(): SVGPathElement = when(absolute) {
        true -> SVGPathElement.V(y!!)
        false -> SVGPathElement.v(y!!)
    }
}

class CubicCurveToConfiguration : SVGPathElementConfiguration() {
    var controlX1: Int? = null
    var controlY1: Int? = null
    var controlX2: Int? = null
    var controlY2: Int? = null
    override fun configure(): SVGPathElement = when(absolute) {
        true -> when(controlX1 == null || controlY1 == null){
            true -> when((controlX1 == null && controlY1 == null)){
                true -> SVGPathElement.S(controlX2!!, controlY2!!, x!!,y!!)
                false -> when(controlX1 == null) {
                    true -> SVGPathElement.C(0,controlY1!!,controlX2!!,controlY2!!,x!!,y!!)
                    false -> SVGPathElement.C(controlX1!!,0,controlX2!!,controlY2!!,x!!,y!!)
                }
            }
            false -> SVGPathElement.C(controlX1!!,controlY1!!,controlX2!!, controlY2!!, x!!,y!!)
        }
        false-> when(controlX1 == null || controlY1 == null){
            true -> when((controlX1 == null && controlY1 == null)){
                true -> SVGPathElement.s(controlX2!!, controlY2!!, x!!,y!!)
                false -> when(controlX1 == null) {
                    true -> SVGPathElement.c(0,controlY1!!,controlX2!!,controlY2!!,x!!,y!!)
                    false -> SVGPathElement.c(controlX1!!,0,controlX2!!,controlY2!!,x!!,y!!)
                }
            }
            false -> SVGPathElement.c(controlX1!!,controlY1!!,controlX2!!, controlY2!!, x!!,y!!)
        }
    }
}

class QuadraticCurveToConfiguration : SVGPathElementConfiguration() {
    var controlX1: Int? = null
    var controlY1: Int? = null

    override fun configure(): SVGPathElement = when(absolute) {
        true -> when(controlX1 == null || controlY1 == null){
            true -> when((controlX1 == null && controlY1 == null)){
                true -> SVGPathElement.T(x!!,y!!)
                false -> when(controlX1 == null) {
                    true -> SVGPathElement.Q(0,controlY1!!,x!!,y!!)
                    false -> SVGPathElement.Q(controlX1!!,0,x!!,y!!)
                }
            }
            false -> SVGPathElement.Q(controlX1!!,controlY1!!, x!!,y!!)
        }
        false-> when(controlX1 == null || controlY1 == null){
            true -> when((controlX1 == null && controlY1 == null)){
                true -> SVGPathElement.t(x!!,y!!)
                false -> when(controlX1 == null) {
                    true -> SVGPathElement.q(0,controlY1!!,x!!,y!!)
                    false -> SVGPathElement.q(controlX1!!,0,x!!,y!!)
                }
            }
            false -> SVGPathElement.q(controlX1!!,controlY1!!, x!!,y!!)
        }
    }
}

class ArcToConfiguration: SVGPathElementConfiguration() {
    var rx: Int? = null
    var ry: Int? = null
    var xAxisRotation: Int? = null
    var largeArcFlag: Int? = null
    var sweepFlagX: Int? = null
    var sweepFlagY: Int? = null

    override fun configure(): SVGPathElement = when(absolute) {
        true -> SVGPathElement.A(rx!!,ry!!,xAxisRotation!!,largeArcFlag!!,sweepFlagX!!,sweepFlagY!!)
        false -> SVGPathElement.a(rx!!,ry!!,xAxisRotation!!,largeArcFlag!!,sweepFlagX!!,sweepFlagY!!)
    }
}

class ClosePathConfiguration: SVGPathElementConfiguration() {
    override fun configure(): SVGPathElement = SVGPathElement.Z
}



fun ArrayList<SVGPathElement>.moveTo(configuration: MoveToConfiguration.()->Unit) {
    val conf = MoveToConfiguration()
    conf.configuration()
    add(conf.configure())
}

fun ArrayList<SVGPathElement>.lineTo(configuration: LineToConfiguration.()->Unit) {
    val conf = LineToConfiguration()
    conf.configuration()
    add(conf.configure())
}

fun ArrayList<SVGPathElement>.hLineTo(configuration: HLineToConfiguration.()->Unit) {
    val conf = HLineToConfiguration()
    conf.configuration()
    add(conf.configure())
}

fun ArrayList<SVGPathElement>.vLineTo(configuration: VLineToConfiguration.()->Unit) {
    val conf = VLineToConfiguration()
    conf.configuration()
    add(conf.configure())
}

fun ArrayList<SVGPathElement>.cubicCurveTo(configuration: CubicCurveToConfiguration.()->Unit) {
    val conf = CubicCurveToConfiguration()
    conf.configuration()
    add(conf.configure())
}

fun ArrayList<SVGPathElement>.quadraticCurveTo(configuration: QuadraticCurveToConfiguration.()->Unit) {
    val conf = QuadraticCurveToConfiguration()
    conf.configuration()
    add(conf.configure())
}

fun ArrayList<SVGPathElement>.arcTo(configuration: ArcToConfiguration.()->Unit) {
    val conf = ArcToConfiguration()
    conf.configuration()
    add(conf.configure())
}

fun ArrayList<SVGPathElement>.closePath() {
    val conf = ClosePathConfiguration()
    add(conf.configure())
}

fun SVGPath.elements(configuration: ArrayList<SVGPathElement>.()->Unit) {
    val list = arrayListOf<SVGPathElement>()
    list.configuration()
    content = list.svg()
}

fun SVGPath.M( x: Int, y: Int): SVGPath = when(content) {
    "" -> {
        content = "M$x,$y"
        this
    }
    else -> {
        content += ", M$x,$y"
        this
    }
}

fun SVGPath.m( dx: Int, dy: Int): SVGPath = when(content) {
    "" -> {
        content = "m$dx,$dy"
        this
    }
    else -> {
        content += ", m$dx,$dy"
        this
    }
}

fun SVGPath.L( x: Int, y: Int): SVGPath  {
    content += ", L$x,$y"
    return this
}

fun SVGPath.l( dx: Int, dy: Int): SVGPath  {
    content += ", l$dx,$dy"
    return this
}

