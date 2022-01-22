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

import `in`.orange.unittestarchitect.utils.Constants.Companion.DIRECTORY_SEPARATOR
import `in`.orange.unittestarchitect.utils.Constants.Companion.JAVA_DIRECTORY
import `in`.orange.unittestarchitect.utils.Constants.Companion.KOTLIN_FILE_EXTENSION
import `in`.orange.unittestarchitect.utils.Constants.Companion.PACKAGE_SEPARATOR
import `in`.orange.unittestarchitect.worker.internal.makers.interfaces.ClassMaker
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Path

internal class ClassMakerImpl(
        private var urls: Array<URL>
) : ClassMaker {
    companion object {
        private const val ANDROID_DIRECTORY = "ANDROID_SDK_DIRECTORY"
        private const val ANDROID_JAR = "android.jar"
        private const val FILE_URL_PREFIX = "file:"
    }

    private var classloader: ClassLoader

    init {
        val SDK_LOCATION = System.getenv(ANDROID_DIRECTORY)
        if (SDK_LOCATION != null) {
            val ANDROID = URL(FILE_URL_PREFIX + SDK_LOCATION + DIRECTORY_SEPARATOR + ANDROID_JAR)
            urls += ANDROID
        }
        classloader = URLClassLoader(urls)
    }

    override fun makeClass(path: Path): Class<*>? {
        val className = path.toString().substringAfter(DIRECTORY_SEPARATOR + JAVA_DIRECTORY + DIRECTORY_SEPARATOR).replace(DIRECTORY_SEPARATOR, PACKAGE_SEPARATOR).removeSuffix(KOTLIN_FILE_EXTENSION)
        val x = classloader.loadClass(className)
        return if (!x.isInterface) {
            x
        } else null
    }
}