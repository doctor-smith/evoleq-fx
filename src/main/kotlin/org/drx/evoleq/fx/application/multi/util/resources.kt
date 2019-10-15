package org.drx.evoleq.fx.application.multi.util

import org.drx.evoleq.fx.application.multi.AppManager
import kotlin.reflect.KClass

fun <App : AppManager<*>> KClass<App>.resource(name: String): String =  java.getResource("$name").toExternalForm()

