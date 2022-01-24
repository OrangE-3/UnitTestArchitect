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

package io.github.orange3.unittestarchitect.worker.internal.makers

import io.github.orange3.unittestarchitect.worker.internal.makers.interfaces.KotlinPropertyMaker
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asTypeName
import org.mockito.Mock

internal class KotlinPropertyMakerImpl : KotlinPropertyMaker {

    override fun createProperty(
            parameter: Class<*>,
            name: String,
            mock: Boolean
    ): PropertySpec {
        val answer = PropertySpec.builder(name, parameter.kotlin)

        answer.mutable(true)
        if (parameter.isPrimitive) {
            when (parameter.kotlin) {
                Boolean::class -> {
                    answer.addModifiers(arrayListOf(KModifier.PRIVATE))
                            .initializer("false")
                }
                Char::class -> {
                    answer.addModifiers(arrayListOf(KModifier.PRIVATE))
                            .initializer("a")
                }
                Byte::class, Short::class, Int::class, Long::class -> {
                    answer.addModifiers(arrayListOf(KModifier.PRIVATE))
                            .initializer("1")
                }
                Float::class, Double::class -> {
                    answer.addModifiers(arrayListOf(KModifier.PRIVATE))
                            .initializer("1.0")
                }
                Void::class -> {
                    answer.addModifiers(arrayListOf(KModifier.PRIVATE))
                            .initializer("null")
                }
            }
        } else {
            answer.addModifiers(arrayListOf(KModifier.LATEINIT, KModifier.PRIVATE))
        }
        if (mock) {
            answer.addAnnotation(Mock::class)
        }
        return answer.build()
    }
}