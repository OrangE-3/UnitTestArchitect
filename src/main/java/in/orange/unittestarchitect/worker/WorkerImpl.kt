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

package `in`.orange.unittestarchitect.worker

import `in`.orange.unittestarchitect.worker.internal.helpers.FilePathHelper
import `in`.orange.unittestarchitect.worker.internal.helpers.FilePathHelperImpl
import `in`.orange.unittestarchitect.worker.internal.makers.ClassMakerImpl
import `in`.orange.unittestarchitect.worker.internal.makers.KotlinFileMakerImpl
import `in`.orange.unittestarchitect.worker.internal.makers.interfaces.ClassMaker
import `in`.orange.unittestarchitect.worker.internal.makers.interfaces.KotlinFileMaker
import `in`.orange.unittestarchitect.worker.internal.writers.TestFileWriter
import `in`.orange.unittestarchitect.worker.internal.writers.TestFileWriterImpl
import java.net.URL

internal class WorkerImpl(
        private val urls: Array<URL>,
        private val sourceDirectoryList: List<String>,
        private val exclude: List<String>,
        private val filePathHelper: FilePathHelper = FilePathHelperImpl(sourceDirectoryList, exclude),
        private val classMaker: ClassMaker = ClassMakerImpl(urls),
        private val kotlinFileMaker: KotlinFileMaker = KotlinFileMakerImpl(),
        private val testFileWriter: TestFileWriter = TestFileWriterImpl(sourceDirectoryList)
) : Worker {
    override fun work() {
        val paths = filePathHelper.getFilePaths()
        for (path in paths) {
            val clazz = classMaker.makeClass(path)
            val file = clazz?.let { kotlinFileMaker.makeKotlinFile(it) }
            if (file != null) {
                testFileWriter.writeFile(file, path)
            }
        }
    }
}