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


Please also set a ANDROID_SDK_DIRECTORY in your environment variables. 
Example: 
```
ANDROID_SDK_DIRECTORY = "Users/rahulchoudhary/Library/Android/sdk/platforms/android-31/"
```



To use the task, your project should be compiled. This plugin is compatible with both java and android projects.

```
Usage of task : ./gradlew :library:generateTests
```
