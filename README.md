# UnitTestArchitect
This is a gradle plugin that helps you to generate boilerplate code for writing jUnit + Mockito Test Cases. 


To us this plugin, add it to the buildscripts classpath:

```
buildscript {
    repositories {
        ...
        mavenCentral()
        mavenLocal()
        ...
    }
    dependencies {
        ...
        classpath "in.orange:unit-test-architect:1.0.0-SNAPSHOT"
        ...
    }
}
```

And then add it as a plugin in your library gradle

```
plugins {
  ...
  id 'in.orange.unit-test-architect'
  ...
}
```

Finally, register this task:
You can give source folders and exclude directories of which you don't want test cases to be generated.

```
tasks.register('generateTests', in.orange.unittestarchitect.TestCaseGenerator) {
    android.libraryVariants.each { v ->
        if (v.name == "SOME FLAVOR VARIANT NAME") {
            urls = v.getCompileClasspath(null).getFiles().collect { it.toURI().toURL() } as URL[]
            sourceDirectoryList = ["library/src/main", "library/src/flavorFolder2", "library/src/flavorFolder3"]
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


Please also set a ANDROID_SDK_DIRECTORY in your environment variables. This is not mandatory if you are not dealing with android. Non android consumers can skip this.
Example: 
```
ANDROID_SDK_DIRECTORY = "/Users/rahulchoudhary/Library/Android/sdk/platforms/android-31"
```



To use the task, your project should be compiled. This plugin is compatible with both java and android projects.

```
Usage of task : ./gradlew :library:generateTests
```

## Example File: module/src/someFolder/java/
```
package foo.bar.tom.files


import foo.bar.tom.models.SomeRequestObject
import foo.bar.tom.dog.DummyClass3
import foo.bar.tom.dog.converter.DummyClass2
import foo.bar.tom.dog.network.service.DummyClass1
import foo.bar.tom.dog.usecases.interfaces.MyClassInterface
import android.content.Context

class MyClass(
    private val context: Context,
    private val dummyClass1: DummyClass1,
    private val dummyClass3: DummyClass3,
    private val dummyClass2: DummyClass2
) : MyClassInterface {

    override suspend fun execute(request: SomeRequestObject) {
        doSomeThing()
    }

    private fun doSomething(
    ): Unit {
        //does something
    }
}
```

This will generate boilerplate test classes with cases for classes without any test cases already present.
## Example Output: module/src/testSomeFolder/java/
### Classes Like this one, compilable, will be generated in your project's test folders.

```
package foo.bar.tom.files

import foo.bar.tom.dog.DummyClass3
import foo.bar.tom.dog.converter.DummyClass2
import foo.bar.tom.dog.network.service.DummyClass1
import android.content.Context
import kotlin.Unit
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

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

  @Test
  public fun execute(): Unit {
  }

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
}

```


