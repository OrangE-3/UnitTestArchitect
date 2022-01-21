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

package `in`.orange.unittestarchitect.worker.internal.writers

import com.squareup.kotlinpoet.FileSpec
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class TestFileWriterImpl(
        private val sourceDirectoryList: List<String>
): TestFileWriter {
    override fun writeFile(file: FileSpec, sourceCodePath: Path, ) {
        var testPath : Path = Paths.get("")
        for(source in sourceDirectoryList){
            if(sourceCodePath.startsWith(source)){
                var prefix = ""
                if(source.endsWith("main")){
                    prefix = source.substringBeforeLast("main") + "test"
                } else {
                    prefix = source.substringBeforeLast("/")
                    var dir = source.substringAfterLast("/")
                    dir = "test" + dir[0].toUpperCase() + dir.substring(1)
                    prefix = "$prefix/$dir"
                }
                var actualPathString = sourceCodePath.toString().substringAfter(source)
                actualPathString = actualPathString.substringBeforeLast(".")+"Test."+actualPathString.substringAfterLast(".")
                val testPathString = prefix + actualPathString
                testPath = Paths.get(testPathString)
            }
        }
        val fileCheck = File(testPath.toUri()).isFile
        if(!fileCheck){
            val myPath = Paths.get(testPath.toString().substringBefore("/java/") + "/java")
            file.writeTo(myPath)
        }
    }

}