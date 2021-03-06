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

import kotlinx.coroutines.CoroutineScope
import org.drx.evoleq.dsl.EvoleqDsl
import org.drx.evoleq.fx.evolving.AsynqFx
import org.drx.evoleq.fx.evolving.ParallelFx

@EvoleqDsl
fun <D> CoroutineScope.parallelFx(
        delay: Long = 1,
        block: CoroutineScope.() -> D
) : ParallelFx<D> = ParallelFx(delay, this){ block() }

@EvoleqDsl
fun <D> CoroutineScope.asyncFx(
        delay: Long = 1,
        block: CoroutineScope.() -> D
) : AsynqFx<D> = AsynqFx(delay, this){ block() }