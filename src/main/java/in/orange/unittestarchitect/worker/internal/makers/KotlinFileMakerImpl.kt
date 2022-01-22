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

import `in`.orange.unittestarchitect.utils.Constants.Companion.PACKAGE_SEPARATOR
import `in`.orange.unittestarchitect.utils.Constants.Companion.TEST_FILE_SUFFIX
import `in`.orange.unittestarchitect.worker.internal.makers.interfaces.KotlinClassMaker
import `in`.orange.unittestarchitect.worker.internal.makers.interfaces.KotlinFileMaker
import com.squareup.kotlinpoet.FileSpec

internal class KotlinFileMakerImpl(
        private val kotlinClassMaker: KotlinClassMaker = KotlinClassMakerImpl()
) : KotlinFileMaker {

    override fun makeKotlinFile(clazz: Class<*>): FileSpec? {
        val testClass = kotlinClassMaker.createTestClass(clazz, clazz.simpleName + TEST_FILE_SUFFIX) ?: return null
        return FileSpec.builder(clazz.canonicalName.substringBeforeLast(PACKAGE_SEPARATOR), clazz.simpleName + TEST_FILE_SUFFIX)
                .addType(testClass)
                .build()
    }
}