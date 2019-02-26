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
package org.drx.evoleq.fx.data

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.SceneAntialiasing
import javafx.scene.paint.Paint


sealed class FxSceneConfigData {
    object Empty: FxSceneConfigData()
    data class Data1(val width: Double, val height: Double) : FxSceneConfigData()
    data class Data2(val width: Double, val height: Double, val depthBuffer: Boolean) : FxSceneConfigData()
    data class Data3(val width: Double, val height: Double, val depthBuffer: Boolean, val antialiasing: SceneAntialiasing) : FxSceneConfigData()
    data class Data4(val width: Double, val height: Double, val fill: Paint) : FxSceneConfigData()
    data class Data5(val fill: Paint) : FxSceneConfigData()
}
fun fxSceneData(
        width: Double? = null,
        height: Double? = null,
        depthBuffer: Boolean? = null,
        antialiasing: SceneAntialiasing? = null,
        fill: Paint? = null
): FxSceneConfigData {
    if(antialiasing != null){
        return FxSceneConfigData.Data3(width!!, height!!, depthBuffer!!, antialiasing)
    }
    if(depthBuffer != null){
        return FxSceneConfigData.Data2(width!!, height!!, depthBuffer)
    }
    if(fill != null) {
        if(width != null){
            return FxSceneConfigData.Data4(width, height!!, fill)
        }
        else{
            return FxSceneConfigData.Data5(fill)
        }
    }
    if(width != null) {
        return FxSceneConfigData.Data1(width, height!!)
    }
    return FxSceneConfigData.Empty
}

fun <R : Parent> R.toScene(data: FxSceneConfigData) : Scene =
        when(this.scene != null){
            true ->this.scene
            false->  when(data) {
                is FxSceneConfigData.Empty -> Scene(this)
                is FxSceneConfigData.Data1 -> Scene(this, data.width, data.height)
                is FxSceneConfigData.Data2 -> Scene(this,data.width, data.height, data.depthBuffer)
                is FxSceneConfigData.Data3 -> Scene(this,data.width, data.height, data.depthBuffer, data.antialiasing)
                is FxSceneConfigData.Data4 -> Scene(this,data.width, data.height, data.fill)
                is FxSceneConfigData.Data5 -> Scene(this,data.fill)
} }
