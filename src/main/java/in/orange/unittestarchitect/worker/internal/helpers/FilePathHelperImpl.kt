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

package `in`.orange.unittestarchitect.worker.internal.helpers

import `in`.orange.unittestarchitect.utils.Constants.Companion.JAVA_FILE_EXTENSION
import `in`.orange.unittestarchitect.utils.Constants.Companion.KOTLIN_FILE_EXTENSION
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FilePathHelperImpl(
        private val sourceDirectoryList: List<String>,
        private val exclude: List<String>
) : FilePathHelper {
    override fun getFilePaths(): List<Path> {
        val answer = ArrayList<Path>()
        for (sourceDirectory in sourceDirectoryList) {
            val projectDirAbsolutePath = Paths.get(sourceDirectory)
            val paths = Files.walk(projectDirAbsolutePath)
                    .filter { item -> Files.isRegularFile(item) }
                    .filter { item -> item.toString().endsWith(KOTLIN_FILE_EXTENSION) || item.toString().endsWith(JAVA_FILE_EXTENSION) }
                    .forEach { item ->
                        var shouldExclude = false
                        for (exc in exclude) {
                            if (item.toString().startsWith(exc)) {
                                shouldExclude = true
                            }
                        }
                        if (!shouldExclude) {
                            answer.add(item)
                        }
                    }
        }
        return answer
    }
}