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

package io.github.orange3.unittestarchitect.worker.internal.helpers

import io.github.orange3.unittestarchitect.utils.Constants.Companion.TEST_OBJECT_NAME
import io.github.orange3.unittestarchitect.worker.internal.helpers.interfaces.ClassHelper
import java.lang.reflect.Method
import java.lang.reflect.Modifier

internal class ClassHelperImpl : ClassHelper {
    override fun getTestObjectMap(clazz: Class<*>): Map<String, Map<String, Class<*>>> {
        val answer = LinkedHashMap<String, Map<String, Class<*>>>()
        var i = 0
        val parameterMap: MutableMap<Class<*>, Int> = HashMap()
        for (constructor in clazz.declaredConstructors) {
            if (constructor.isSynthetic || Modifier.isPrivate(constructor.modifiers)) continue
            i += 1
            //Number of constructors = number of test objects.
            val testObject = if (i == 1) {
                TEST_OBJECT_NAME
            } else {
                TEST_OBJECT_NAME + i
            }
            val myParameters = LinkedHashMap<String, Class<*>>()
            var j = 0
            for (parameter in constructor.parameterTypes) {
                if (!parameterMap.containsKey(parameter)) {
                    parameterMap[parameter] = 1
                } else {
                    parameterMap[parameter] = parameterMap[parameter]!! + 1
                }
                val count = parameterMap[parameter]
                val name = if (count == 1) {
                    parameter.simpleName[0].toLowerCase() + parameter.simpleName.substring(1)
                } else {
                    parameter.simpleName[0].toLowerCase() + parameter.simpleName.substring(1) + count
                }
                myParameters[name] = parameter
                j += 1
            }
            answer[testObject] = myParameters
        }
        return answer
    }

    override fun getMethodMap(clazz: Class<*>): Pair<Int, Map<String, Method>> {
        val map = LinkedHashMap<String, Method>()
        val methodNameMap: MutableMap<String, Int> = HashMap()
        var methodCount = 0
        for (method in clazz.declaredMethods) {
            //Exhaustive list of non-private methods
            if (!method.isSynthetic && !Modifier.isPrivate(method.modifiers)) {
                methodCount += 1
                val name = if (!methodNameMap.containsKey(method.name)) {
                    methodNameMap[method.name] = 1
                    method.name
                } else {
                    methodNameMap[method.name] = methodNameMap[method.name]!! + 1
                    method.name + methodNameMap[method.name]
                }
                map[name] = method
            }
        }
        return Pair(methodCount, map)
    }
}