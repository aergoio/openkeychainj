# openkeychainj

Java implementation of Openkeychain SDK


## Latest

v1.0.0


## Compatibility

- Aergo: v1.2.x
- Java : JDK 7 or higher


## Download

- Group ID: io.aergo
- Artifact ID: openkeychainj-sdk


### Maven


```
<repositories>
  <repository>
    <id>jcenter</id>
    <url>https://jcenter.bintray.com</url>
  </repository>
</repositories>

...

<dependencies>
  <dependency>
    <groupId>io.aergo</groupId>
    <artifactId>openkeychainj-sdk</artifactId>
    <version>${openkeychainjVersion}</version>
  </dependency>
</dependencies>
```

### Gradle

```
repositories {
  jcenter()
}

...

dependencies {
  implementation "io.aergo:openkeychainj-sdk:${openkeychainjVersion}"
}
```


## Modules

The repository contains next:

- sdk
- examples


## Integration

TBD


## Build


### Prerequisites

#### JDK

- [JDK8](https://openjdk.java.net/projects/jdk8/)

### Clone

```console
$ git clone https://github.com/aergoio/openkeychainj.git
```

### Build and Package

- Clean

```console
$ gradle clean
```

- Build

```console
$ gradle build
```

- Install to maven local

```console
$ gradle clean
```


## Test

### Unit Test

They are classes with 'Test' suffix.


### Integration test

They are classes with 'IT' suffix meaning integration test.
