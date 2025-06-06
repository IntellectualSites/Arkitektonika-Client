# Arkitektonika-Client
Java client for the [Arkitektonika API](https://github.com/IntellectualSites/Arkitektonika)

## Description

All of the Arkitektonika API can be interacted with using the Arkitektonika instance. Using it you can:
- check API compatibility,
- upload schematics,
- download schematics,
- check the status of uploaded schematics, and
- delete schematics

## Usage

Here is some sample usage:

```java
Arkitektonika arkitektonika = Arkitektonika.builder().withUrl("https://your.url").build();
arkitektonika.upload(new File("your/file.schem")).whenComplete((keys, throwable) -> {
    if (throwable != null) {
        throwable.printStackTrace();
    } else {
        // ... store the keys, or whatever you feel like
    }
});
```

## Dependencies

The client uses [HTTP4J](https://github.com/Sauilitired/HTTP4J) to interact with the Arkitektonika
REST API. This will need to be available on the classpath for the client to function.

## Maven Deployment
Releases are published to the central repository.

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.intellectualsites.arkitektonika:Arkitektonika-Client:VERSION")
}
```

```xml
<dependencies>
    <dependency>
      <groupId>com.intellectualsites.arkitektonika</groupId>
      <artifactId>Arkitektonika-Client</artifactId>
      <version>VERSION</version>
    </dependency>
</dependencies>
```
