package org.drx.evoleq.fx.application.configration.util

import org.drx.evoleq.fx.application.configration.AppManager
import kotlin.reflect.KClass

fun <App : AppManager<*>> KClass<App>.resource(name: String): String =  java.getResource("$name").toExternalForm()

