# gpsd4j

[![Release](https://jitpack.io/v/com.ivkos.gpsd4j/gpsd4j.svg)](https://jitpack.io/#com.ivkos.gpsd4j/gpsd4j)

**gpsd4j** is a Java library that allows you to communicate with a [gpsd server](http://www.catb.org/gpsd/).

## Requirements
* JRE 8 or higher at runtime
* JDK 8 or higher to compile the library from source

## Installation
### Maven
**Step 1.** Add the JitPack repository to your `pom.xml` file:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

**Step 2.** Add the dependency:
```xml
<dependency>
    <groupId>com.ivkos.gpsd4j</groupId>
    <artifactId>gpsd4j</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
**Step 1.** Add the JitPack repository to your root `build.gradle` at the end of repositories:
```
allprojects {
    repositories {
      ...
      maven { url "https://jitpack.io" }
    }
}
```

**Step 2.** Add the dependency:
```
dependencies {
    compile 'com.ivkos.gpsd4j:gpsd4j:1.0.0'
}
```

## Documentation
Javadocs can be found [here](https://jitpack.io/com/ivkos/gpsd4j/gpsd4j/1.0.0/javadoc/).

## Quick Start
