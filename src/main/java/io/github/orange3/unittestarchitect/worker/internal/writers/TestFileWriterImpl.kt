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

package io.github.orange3.unittestarchitect.worker.internal.writers

import io.github.orange3.unittestarchitect.utils.Constants.Companion.DIRECTORY_SEPARATOR
import io.github.orange3.unittestarchitect.utils.Constants.Companion.JAVA_DIRECTORY
import io.github.orange3.unittestarchitect.utils.Constants.Companion.MAIN_DIRECTORY
import io.github.orange3.unittestarchitect.utils.Constants.Companion.PACKAGE_SEPARATOR
import io.github.orange3.unittestarchitect.utils.Constants.Companion.TEST_DIRECTORY
import io.github.orange3.unittestarchitect.utils.Constants.Companion.TEST_FILE_SUFFIX
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

internal class TestFileWriterImpl(
        private val sourceDirectoryList: List<String>
) : TestFileWriter {
    override fun writeFile(file: FileSpec, sourceCodePath: Path) {
        var testPath: Path = Paths.get("")
        for (source in sourceDirectoryList) {
            if (sourceCodePath.startsWith(source)) {
                var prefix: String
                if (source.endsWith(MAIN_DIRECTORY)) {
                    prefix = source.substringBeforeLast(MAIN_DIRECTORY) + TEST_DIRECTORY
                } else {
                    prefix = source.substringBeforeLast(DIRECTORY_SEPARATOR)
                    var dir = source.substringAfterLast(DIRECTORY_SEPARATOR)
                    dir = TEST_DIRECTORY + dir[0].toUpperCase() + dir.substring(1)
                    prefix = prefix + DIRECTORY_SEPARATOR + dir
                }
                var actualPathString = sourceCodePath.toString().substringAfter(source)
                actualPathString = actualPathString.substringBeforeLast(PACKAGE_SEPARATOR) + TEST_FILE_SUFFIX + PACKAGE_SEPARATOR + actualPathString.substringAfterLast(PACKAGE_SEPARATOR)
                val testPathString = prefix + actualPathString
                testPath = Paths.get(testPathString)
            }
        }
        val fileCheck = File(testPath.toUri()).isFile
        if (!fileCheck) {
            val myPath = Paths.get(testPath.toString().substringBefore(DIRECTORY_SEPARATOR + JAVA_DIRECTORY + DIRECTORY_SEPARATOR) + DIRECTORY_SEPARATOR + JAVA_DIRECTORY)
            file.writeTo(myPath)
        }
    }

}