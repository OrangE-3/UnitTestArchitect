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

import `in`.orange.unittestarchitect.worker.internal.makers.interfaces.KotlinFunctionMaker
import com.squareup.kotlinpoet.FunSpec
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class KotlinFuctionMakerImpl : KotlinFunctionMaker {
    companion object {
        private const val BEFORE_FUNCTION_NAME = "setUp"
    }

    override fun createBeforeFunction(clazz: Class<*>, testObjectMap: Map<String, Map<String, Class<*>>>): FunSpec {
        val beforeFunctionSpecBuilder = FunSpec.builder(BEFORE_FUNCTION_NAME)
                .addAnnotation(Before::class)
                .addStatement("%T.initMocks(this)", MockitoAnnotations::class)

        for (testObject in testObjectMap) {
            beforeFunctionSpecBuilder.addStatement("${testObject.key} = %T(", clazz)
            var j = 0
            val parameterLength = testObject.value.size
            for (parameter in testObject.value) {
                j += 1
                if (j < parameterLength) {
                    beforeFunctionSpecBuilder.addStatement("\t%N,", parameter.key)
                } else {
                    beforeFunctionSpecBuilder.addStatement("\t%N", parameter.key)
                }
            }
            beforeFunctionSpecBuilder.addStatement(")")
        }

        return beforeFunctionSpecBuilder.build()
    }

    override fun createTestFunction(
            functionName: String
    ): FunSpec {
        return FunSpec.builder(functionName)
                .addAnnotation(Test::class)
                .build()
    }
}