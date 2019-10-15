package org.drx.evoleq.fx.application.stub.util

import org.drx.evoleq.fx.application.stub.AppManager
import kotlin.reflect.KClass

fun <App : AppManager<*>> KClass<App>.resource(name: String): String =  java.getResource("$name").toExternalForm()

