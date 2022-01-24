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

import com.squareup.kotlinpoet.TypeSpec
import io.github.orange3.unittestarchitect.worker.internal.helpers.ClassHelperImpl
import io.github.orange3.unittestarchitect.worker.internal.helpers.interfaces.ClassHelper
import io.github.orange3.unittestarchitect.worker.internal.makers.interfaces.KotlinClassMaker
import io.github.orange3.unittestarchitect.worker.internal.makers.interfaces.KotlinFunctionMaker
import io.github.orange3.unittestarchitect.worker.internal.makers.interfaces.KotlinPropertyMaker

internal class KotlinClassMakerImpl(
        private val classHelper: ClassHelper = ClassHelperImpl(),
        private val kotlinFunctionMaker: KotlinFunctionMaker = KotlinFuctionMakerImpl(),
        private val kotlinPropertyMaker: KotlinPropertyMaker = KotlinPropertyMakerImpl()
) : KotlinClassMaker {

    override fun createTestClass(clazz: Class<*>, className: String): TypeSpec? {
        val methodMap = classHelper.getMethodMap(clazz)
        if (methodMap.first <= 0) {
            return null
        }

        val classBuilder = TypeSpec.classBuilder(className)
        val testObjectMap = classHelper.getTestObjectMap(clazz)

        classBuilder.addFunction(kotlinFunctionMaker.createBeforeFunction(clazz, testObjectMap))
        for (testObject in testObjectMap) {
            classBuilder.addProperty(kotlinPropertyMaker.createProperty(clazz, testObject.key, mock = false))
            for (parameter in testObject.value) {
                if (parameter.value == java.lang.String::class.java || parameter.value == java.lang.Throwable::class.java || parameter.value.isPrimitive) {
                    classBuilder.addProperty(kotlinPropertyMaker.createProperty(parameter.value, parameter.key, mock = false))
                } else {
                    classBuilder.addProperty(kotlinPropertyMaker.createProperty(parameter.value, parameter.key, mock = true))
                }
            }
        }

        for (method in methodMap.second) {
            classBuilder.addFunction(kotlinFunctionMaker.createTestFunction(method.key, method.value))
        }

        return classBuilder.build()
    }
}