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

package io.github.orange3.unittestarchitect

import io.github.orange3.unittestarchitect.worker.Worker
import io.github.orange3.unittestarchitect.worker.WorkerImpl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.net.URL

abstract class TestCaseGenerator : DefaultTask() {
    @Input
    lateinit var urls: Array<URL>

    @Input
    lateinit var sourceDirectoryList: List<String>

    @Input
    lateinit var exclude: List<String>

    private lateinit var worker: Worker

    @TaskAction
    fun injectSource() {
        worker = WorkerImpl(urls, sourceDirectoryList, exclude)
        worker.work()
    }
}