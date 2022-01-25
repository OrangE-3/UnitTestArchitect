# UnitTestArchitect 

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.orange-3/unit-test-architect/badge.svg?)](https://maven-badges.herokuapp.com/maven-central/io.github.orange-3/unit-test-architect?)

This is a gradle plugin that helps you to generate boilerplate code for writing jUnit + Mockito Test Cases.

Any existing IDEs such as IntelliJ or Android Studio, will generate test cases but only per method per file. 
They also generate only empty methods and leave the actual code to the developer. 
This can get tedious if you have a lot of untested code in your codebase, as you'll have to write mock fields for
each dependency in your class.

This is where you can use this plugin to reduce developer time. This Plugin generates actual test case code along 
with mocks and input fields + output fields per test case. It even adds an assertion to your method invoke!


#### Major advantage of using this plugin is that it is language agnostic. It supports:
#### * Android Projects
#### * Pure Java Projects
#### * Pure Kotlin Projects
#### * Java + Kotlin Projects

## Usage

You'll need Mockito and junit dependencies for the test files to work. Add this in your module/app/project build.gradle
```
dependencies {
    ...
    testImplementation 'org.mockito.kotlin:mockito-kotlin:$MOCKITO_VERSION'
    testImplementation 'junit:junit:$UNIT_VERSION'
    ...
}
```

### 1. Android Projects

To use this plugin, add it the top of your root project's build.gradle:

```
buildscript {
    repositories {
        mavenCentral()
        ...
    }
    dependencies {
        ...
        classpath "io.github.orange-3:unit-test-architect:$LATEST_LIBRARY_VERSION"
        ...
    }
}
```

And then add it as a plugin in your library/ application build.gradle

```
plugins {
  ...
  id 'io.github.orange-3.unit-test-architect'
  ...
}
```

Finally, register this task:
You can give source folders and exclude directories of which you don't want test cases to be generated.

```
tasks.register('generateTests', io.github.orange3.unittestarchitect.TestCaseGenerator) {
    // Use android.applicationVariants.each for application modules
    android.libraryVariants.each { variant ->
        if (variant.name == "SOME FLAVOR VARIANT NAME") {
            // This line searches for javac compiled code
            // You can use this for older gradle versions: def javaCompiledClasses = variant.javaCompileProvider.get().destinationDir.getAbsoluteFile().toURI().toURL()
            def javaCompiledClasses = variant.getJavaCompileProvider().get().destinationDirectory.getAsFile().get().toURI().toURL()
            // This line searches for kotlin compiled code + dependencies
            def restDependencies = variant.getCompileClasspath(null).getFiles().collect { it.toURI().toURL() } as URL[]
            // You need to set this field with all locations to your compiled code (.class files)
            urls = restDependencies + javaCompiledClasses
            // You also need to provide a list of source directories
            sourceDirectoryList = ["library/src/main", "library/src/flavorFolder2", "library/src/flavorFolder3"]
            // If needed, you can exclude directories or files using this
            exclude = ["library/src/main/java/foo/bar/tom/di",
                       "library/src/main/java/foo/bar/tom/models",
                       "library/src/main/java/foo/bar/tom/cat/di",
                       "library/src/main/java/foo/bar/tom/cat/models",
                       "library/src/main/java/foo/bar/tom/dog/di",
                       "library/src/main/java/foo/bar/tom/dog/models",
                       "library/src/flavorFolder2/java/foo/bar/tom/di",
                       "library/src/flavorFolder3/java/foo/bar/tom/di",]
        }
    }
}
```

Please also set a ANDROID_SDK_DIRECTORY in your environment variables.
Example: 
```
ANDROID_SDK_DIRECTORY = "/Users/rahulchoudhary/Library/Android/sdk/platforms/android-31"
```

To use the task, your project should be compiled.

```
Usage of task : ./gradlew :library:generateTests
```

### 2. Non-Android Projects

To use this plugin, add it the top of your project's settings.gradle:

```
pluginManagement {
    repositories {
        mavenCentral()
        maven { url "https://plugins.gradle.org/m2/" }
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id 'io.github.orange-3.unit-test-architect' version("$LATEST_PLUGIN_VERSION")
    }
}
```

And then add it as a plugin in your build.gradle

```
plugins {
  ...
  id 'io.github.orange-3.unit-test-architect'
  ...
}
```

Finally, register this task:

```
tasks.register('generateTests', io.github.orange3.unittestarchitect.TestCaseGenerator) {
    //directory to the compiled runtime classes
    urls = sourceSets.main.runtimeClasspath.files.collect { it.toURI().toURL() } as URL[]
    //directory list of your source
    sourceDirectoryList = ["src/main"]
    // If needed, you can exclude directories or files using this
    exclude = []
}
```

To use the task, your project should be compiled.

```
Usage of task : ./gradlew :generateTests
```


## Example File: module/src/someFolder/java/
```
package foo.bar.tom.files


import foo.bar.tom.models.SomeRequest
import foo.bar.tom.models.SomeResponse
import foo.bar.tom.interfaces.SomeInterface
import foo.bar.tom.dog.DummyClass3
import foo.bar.tom.dog.converter.DummyClass2
import foo.bar.tom.dog.network.service.DummyClass1
import foo.bar.tom.dog.usecases.interfaces.MyClassInterface
import android.content.Context
import kotlin.Int
import kotlin.String

class MyClass(
    private val context: Context,
    private val dummyClass1: DummyClass1,
    private val dummyClass3: DummyClass3,
    private val dummyClass2: DummyClass2
) : MyClassInterface {

    override fun execute(request: SomeRequest): SomeResponse {
        doSomeThing()
    }
    
    override suspend fun check(
        input: String, 
        dummyInt: Int, 
        someInterface: SomeInterface
    ): AnotherResponse {
        //does Something
    }
    
    private fun doSomething(
    ): Unit {
        //does something
    }
   
}
```

This will generate and paste boilerplate test classes of any new code without affecting existing test classes.
Hence, it's more useful in projects where unit testing was not in scope earlier, but now you want to write testes for older code. 
The more the number of untested files in your code, the more useful this plugin gets.

```
package foo.bar.tom.models
import ...
data class SomeRequest(
    val integer: Int,
    val someClass: SomeClass
)
```
```
package foo.bar.tom.models
import ...
data class SomeResponse(
    val double: Double
)
```
```
package foo.bar.tom.classes
import ...
public class SomeClass(
    val data: String,
    ...
) {
//Some Class Logic
}
```
```
package foo.bar.tom.classes
import ...
public class AnotherResponse(
    //Empty Constructor
) {
}
```
What the logic will do, is it will perform Graph Search operations on parameters and
generate all required intermediate test objects!

## Example Output: module/src/testSomeFolder/java/
### Classes Like this one, compilable, will be generated in your project's test folders.

```
package foo.bar.tom.files

import foo.bar.tom.models.SomeRequest
import foo.bar.tom.models.SomeResponse
import foo.bar.tom.classes.SomeClass
import foo.bar.tom.classes.AnotherResponse
import foo.bar.tom.interfaces.SomeInterface
import foo.bar.tom.dog.DummyClass3
import foo.bar.tom.dog.converter.DummyClass2
import foo.bar.tom.dog.network.service.DummyClass1
import android.content.Context
import kotlin.Unit
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.Int
import kotlin.String
import kotlin.Double

public class MyClassTest {
  private lateinit var testObject: MyClass

  @Mock
  private lateinit var context: Context

  @Mock
  private lateinit var dummyClass1: DummyClass1

  @Mock
  private lateinit var dummyClass3: DummyClass3

  @Mock
  private lateinit var dummyClass2: DummyClass2

  @Before
  public fun setUp(): Unit {
    MockitoAnnotations.initMocks(this)
    testObject = MyClass(
    	context,
    	dummyClass1,
    	dummyClass3,
    	dummyClass2
    )
  }
  
  @Test
  public fun execute(): Unit {
    //generates random primitive type and string values 
    val testIntObject: Int = 1
    val testStringObject: String = "dasfewfe3"
    val testSomeClassObject: SomeClass = SomeClass(data)
    val testSomeRequestObject : SomeRequest = SomeRequest(testIntegerObject, data)
    val testDoubleObject: Double = 1.0
    val testExpectedResult : SomeResponse = SomeResponse(testDoubleObject)
    Assert.assertEquals(
        testExpectedResult,
        testObject.execute(testSomeRequestObject)
    )
  }
  
  @Test
  public fun check(): Unit {
    val testStringObject: String = "fwf4scxaasd"
    val testIntObject: Int = 1
    // Leaves interface implementations upto the developer
    lateinit var someInterface: SomeInterface
    val testExpectedResult : AnotherResponse = AnotherResponse()
    
    runBlocking {
        Assert.assertEquals(
            testExpectedResult,
            testObject.check(
                testStringObject, 
                testIntObject, 
                someInterface
            )
        )
    }
  }
  
}

```

### It must be evident by now that this plugin writes so much code for you.
### Please note that the test cases will be generated only in kotlin for both java and kotlin files.

## Open Issues
* Classes with generics are not supported. But the task will run and cases will be generated.
* Some coroutine tests are generated for no reason.
* Above cases will create compilation error in test files, but these can be addressed by the 
developer himself.

