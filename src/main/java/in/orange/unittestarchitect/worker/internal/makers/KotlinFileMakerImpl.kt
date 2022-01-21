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

import `in`.orange.unittestarchitect.worker.internal.makers.interfaces.KotlinFileMaker
import com.squareup.kotlinpoet.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.lang.reflect.Modifier

class KotlinFileMakerImpl: KotlinFileMaker {
    override fun makeKotlinFile(clazz: Class<*>): FileSpec? {
        val classBuilder = TypeSpec.classBuilder(clazz.simpleName + "Test")
        val beforeFunctionSpecBuilder = FunSpec.builder("setUp")
                .addAnnotation(Before::class)
                .addStatement("%T.initMocks(this)", MockitoAnnotations::class)
        var i=0
        val parameterMap: MutableMap<Class<*>,Int> = HashMap()
        for(constructor in clazz.declaredConstructors) {
            if(constructor.isSynthetic || Modifier.isPrivate(constructor.modifiers)) continue
            i+=1
            //Number of constructors = number of test objects.
            val testObject = if(i==1){
                "testObject"
            } else {
                "testObject$i"
            }
            classBuilder.addProperty(PropertySpec.builder(testObject, clazz)
                    .addModifiers(arrayListOf(KModifier.LATEINIT, KModifier.PRIVATE))
                    .mutable(true)
                    .build())

            beforeFunctionSpecBuilder
                    .addStatement("$testObject = %T(", clazz)

            val parameterLength = constructor.parameterTypes.size
            var j = 0
            for(parameter in constructor.parameterTypes) {
                if(!parameterMap.containsKey(parameter)){
                    parameterMap[parameter] = 1
                } else {
                    parameterMap[parameter] = parameterMap[parameter]!! + 1
                }
                val count = parameterMap[parameter]
                val name = if(count == 1) {
                    parameter.simpleName[0].toLowerCase() + parameter.simpleName.substring(1)
                } else {
                    parameter.simpleName[0].toLowerCase() + parameter.simpleName.substring(1) + count
                }
                if(parameter.isPrimitive){
                    classBuilder.addProperty(PropertySpec.builder(name, parameter.asTypeName().copy(nullable = true))
                            .addModifiers(arrayListOf(KModifier.PRIVATE))
                            .initializer("null")
                            .mutable(true)
                            .build())
                } else if(parameter == java.lang.String::class.java || parameter == java.lang.Throwable::class.java) {
                    classBuilder.addProperty(PropertySpec.builder(name, parameter.kotlin)
                            .addModifiers(arrayListOf(KModifier.LATEINIT, KModifier.PRIVATE))
                            .mutable(true)
                            .build())
                } else {
                    classBuilder.addProperty(PropertySpec.builder(name, parameter.kotlin)
                            .addModifiers(arrayListOf(KModifier.LATEINIT, KModifier.PRIVATE))
                            .addAnnotation(Mock::class)
                            .mutable(true)
                            .build())
                }
                j+=1
                if(j<parameterLength) {
                    beforeFunctionSpecBuilder
                            .addStatement("\t%N,", name)
                } else {
                    beforeFunctionSpecBuilder
                            .addStatement("\t%N", name)
                }
            }
            beforeFunctionSpecBuilder
                    .addStatement(")")
        }
        val methodNameMap: MutableMap<String, Int> = HashMap()
        var methodCount = 0
        for(method in clazz.declaredMethods){
            //Exhaustive list of non-private methods
            if(!method.isSynthetic && !Modifier.isPrivate(method.modifiers)) {
                methodCount +=1
                if(!methodNameMap.containsKey(method.name)){
                    methodNameMap[method.name] = 1
                    classBuilder.addFunction(FunSpec.builder(method.name)
                            .addAnnotation(Test::class)
                            .build())
                } else {
                    methodNameMap[method.name] = methodNameMap[method.name]!! + 1
                    classBuilder.addFunction(FunSpec.builder(method.name + methodNameMap[method.name])
                            .addAnnotation(Test::class)
                            .build())
                }

            }
        }
        classBuilder.addFunction(beforeFunctionSpecBuilder.build())

        val file = FileSpec.builder(clazz.canonicalName.substringBeforeLast("."), clazz.simpleName + "Test")
                .addType(classBuilder.build())
                .build()

        return if(methodCount!=0)
            file
        else null
    }
}