/*
 * Copyright (C) 2022 Rahul Choudhary
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package `in`.orange.unittestarchitect.worker.internal.makers

import `in`.orange.unittestarchitect.worker.internal.makers.interfaces.KotlinPropertyMaker
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asTypeName
import org.mockito.Mock

class KotlinPropertyMakerImpl : KotlinPropertyMaker {

    override fun createProperty(
            parameter: Class<*>,
            name: String,
            mock: Boolean,
            initializer: Boolean,
            nullable: Boolean
    ): PropertySpec {
        val answer = if (nullable) {
            PropertySpec.builder(name, parameter.asTypeName().copy(nullable = true))
        } else {
            PropertySpec.builder(name, parameter.kotlin)
        }

        answer.mutable(true)
        if (initializer) {
            answer.addModifiers(arrayListOf(KModifier.PRIVATE))
                    .initializer("null")
        } else {
            answer.addModifiers(arrayListOf(KModifier.LATEINIT, KModifier.PRIVATE))
        }
        if (mock) {
            answer.addAnnotation(Mock::class)
        }
        return answer.build()
    }
}