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

import `in`.orange.unittestarchitect.worker.internal.helpers.ClassHelperImpl
import `in`.orange.unittestarchitect.worker.internal.helpers.interfaces.ClassHelper
import `in`.orange.unittestarchitect.worker.internal.makers.interfaces.KotlinClassMaker
import `in`.orange.unittestarchitect.worker.internal.makers.interfaces.KotlinFunctionMaker
import `in`.orange.unittestarchitect.worker.internal.makers.interfaces.KotlinPropertyMaker
import com.squareup.kotlinpoet.TypeSpec

class KotlinClassMakerImpl(
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
            classBuilder.addProperty(kotlinPropertyMaker.createProperty(clazz, testObject.key, mock = false, initializer = false, nullable = false))
            for (parameter in testObject.value) {
                if (parameter.value.isPrimitive) {
                    classBuilder.addProperty(kotlinPropertyMaker.createProperty(parameter.value, parameter.key, mock = false, initializer = true, nullable = true))
                } else if (parameter == java.lang.String::class.java || parameter == java.lang.Throwable::class.java) {
                    classBuilder.addProperty(kotlinPropertyMaker.createProperty(parameter.value, parameter.key, mock = false, initializer = false, nullable = false))
                } else {
                    classBuilder.addProperty(kotlinPropertyMaker.createProperty(parameter.value, parameter.key, mock = true, initializer = false, nullable = false))
                }
            }
        }

        for (method in methodMap.second) {
            classBuilder.addFunction(kotlinFunctionMaker.createTestFunction(method.key))
        }

        return classBuilder.build()
    }
}