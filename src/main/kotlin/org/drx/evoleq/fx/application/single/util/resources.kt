package org.drx.evoleq.fx.application.single.util

import org.drx.evoleq.fx.application.single.AppManager
import kotlin.reflect.KClass

fun <App : AppManager<*>> KClass<App>.resource(name: String): String =  java.getResource("$name").toExternalForm()

