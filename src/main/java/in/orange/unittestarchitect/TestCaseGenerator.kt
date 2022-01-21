package `in`.orange.unittestarchitect

import com.squareup.kotlinpoet.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.File
import java.lang.reflect.Modifier
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

abstract class TestCaseGenerator: DefaultTask() {
    @Input
    lateinit var urls: Array<URL>

    @Input
    lateinit var sourceDirectoryList: List<String>

    @Input
    lateinit var exclude: List<String>

    @TaskAction
    fun injectSource() {
        val ANDROID = URL("file:/Users/rahulchoudhary/Library/Android/sdk/platforms/android-31/android.jar")
        urls += ANDROID
        for(url in urls){
            println(url.toString())
        }
        val classloader = URLClassLoader(urls)
        for(sourceDirectory in sourceDirectoryList) {
            val projectDirAbsolutePath = Paths.get(sourceDirectory)
            val paths = Files.walk(projectDirAbsolutePath)
                    .filter { item -> Files.isRegularFile(item) }
                    .filter { item -> item.toString().endsWith(".kt") || item.toString().endsWith(".java") }
                    .forEach { item ->
                        var shouldExclude = false
                        for(exc in exclude) {
                            if(item.toString().startsWith(exc)) {
                                shouldExclude = true
                            }
                        }
                        if(!shouldExclude) {
                            val className = item.toString().substringAfter("$sourceDirectory/java/").replace("/", ".").removeSuffix(".kt")
                            val x = classloader.loadClass(className)
                            if (!x.isInterface) {
                                createTestCaseFile(x, item)
                            }
                        }
                    }
        }
    }

    private fun createTestCaseFile(clazz: Class<*>, filePath: Path) {
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

        if(methodCount!=0)
        writeToTestDirectories(file, filePath)

    }

    private fun writeToTestDirectories(file: FileSpec, actualPath: Path){
        var testPath : Path = Paths.get("")
        for(source in sourceDirectoryList){
            if(actualPath.startsWith(source)){
                var prefix = ""
                if(source.endsWith("main")){
                    prefix = source.substringBeforeLast("main") + "test"
                } else {
                    prefix = source.substringBeforeLast("/")
                    var dir = source.substringAfterLast("/")
                    dir = "test" + dir[0].toUpperCase() + dir.substring(1)
                    prefix = "$prefix/$dir"
                }
                var actualPathString = actualPath.toString().substringAfter(source)
                actualPathString = actualPathString.substringBeforeLast(".")+"Test."+actualPathString.substringAfterLast(".")
                val testPathString = prefix + actualPathString
                testPath = Paths.get(testPathString)
            }
        }
        val fileCheck = File(testPath.toUri()).isFile
        println(fileCheck)
        if(!fileCheck){
            val myPath = Paths.get(testPath.toString().substringBefore("/java/") + "/java")
            file.writeTo(myPath)
        }
    }

}