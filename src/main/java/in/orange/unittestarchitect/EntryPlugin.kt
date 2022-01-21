package `in`.orange.unittestarchitect

import org.gradle.api.Plugin
import org.gradle.api.Project

class EntryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("testCaseGenerator", TestCaseGenerator::class.java)
    }
}
