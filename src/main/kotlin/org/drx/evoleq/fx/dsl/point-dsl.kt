package org.drx.evoleq.fx.dsl

import javafx.geometry.Point2D
import javafx.geometry.Point3D
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.dsl.configure

class Point2DConfiguration : Configuration<Point2D> {

    lateinit var x: Number
    lateinit var y: Number

    override fun configure(): Point2D  = Point2D(x.toDouble(), y.toDouble())

}
fun point(configuration: Point2DConfiguration.()->Unit): Point2D = configure(configuration)




class Point3DConfiguration : Configuration<Point3D> {

    lateinit var x: Number
    lateinit var y: Number
    lateinit var z: Number

    override fun configure(): Point3D  = Point3D(x.toDouble(), y.toDouble(), z.toDouble())

}
fun point(configuration: Point3DConfiguration.()->Unit): Point3D = configure(configuration)