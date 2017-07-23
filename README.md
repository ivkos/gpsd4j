# gpsd4j

[![Release](https://jitpack.io/v/com.ivkos/gpsd4j.svg)](https://jitpack.io/#com.ivkos/gpsd4j)

**gpsd4j** is a Java library that allows you to communicate with a [gpsd server](http://www.catb.org/gpsd/).

## Table of Contents
* [Requirements](#requirements)
* [Installation](#installation)
	* [Maven](#maven)
	* [Gradle](#gradle)
* [Documentation](#documentation)
* [Quick Start](#quick-start)
	* [Creating a client](#creating-a-client)
	* [Message handlers](#message-handlers)
	* [Client lifecycle](#client-lifecycle)
		* [Persisting device settings and watch mode](#persisting-device-settings-and-watch-mode)
	* [Sending commands](#sending-commands)
		* [Sending a command and expecting a response](#sending-a-command-and-expecting-a-response)
		* [Sending a command and not awaiting a response](#sending-a-command-and-not-awaiting-a-response)
	* [Putting it all together](#putting-it-all-together)



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
    <groupId>com.ivkos</groupId>
    <artifactId>gpsd4j</artifactId>
    <version>1.2.1</version>
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
    compile 'com.ivkos:gpsd4j:1.2.1'
}
```

## Documentation
Javadocs can be found [here](https://jitpack.io/com/ivkos/gpsd4j/1.2.1/javadoc/).

## Quick Start
> An example is worth a thousand pages of Javadocs.


### Creating a client
```java
// Create a client for connecting to the gpsd server at localhost, port 2947
GpsdClient client = new GpsdClient("localhost", 2947);
```

```java
// You can pass an options object if you wish to configure connection handling
GpsdClientOptions options = new GpsdClientOptions()
    .setReconnectOnDisconnect(true)
    .setConnectTimeout(3000) // ms
    .setIdleTimeout(30) // seconds
    .setReconnectAttempts(5)
    .setReconnectInterval(3000); // ms

GpsdClient client = new GpsdClient("localhost", 2947, options);
```


### Message handlers
Because of the asynchronous nature of the client, you can
dynamically add or remove handlers with no unexpected side effects,
no matter if the client is running or not.

```java
// Adds a handler that prints received gpsd errors to stderr
client.addErrorHandler(System.err::println);

// Adds a message handler that handles incoming TPV messages
client.addHandler(TPVReport.class, tpv -> {
    Double lat = tpv.getLatitude();
    Double lon = tpv.getLongitude();
    
    System.out.printf("Lat: %f, Lon: %f\n", lat, lon);
});

// Adding handlers can be chained
client.addHandler(TPVReport.class, tpv -> { ... })
      .addHandler(SKYReport.class, sky -> { ... })
      .addHandler(GSTReport.class, gst -> { ... });
```

```java
// Suppose you use a generic handler for multiple types of messages
Consumer<? extends GpsdMessage> genericHandler = msg -> {
    System.out.println("Got a message: " + msg);
};

client.addHandler(TPVReport.class, (Consumer<TPVReport>) genericHandler)
      .addHandler(GSTReport.class, (Consumer<GSTReport>) genericHandler);

// You can remove it from a specific type of message
client.removeHandler(TPVReport.class, (Consumer<TPVReport>) genericHandler);

// Or you can remove it altogether from all types of messages
client.removeHandler(genericHandler);
```


### Client lifecycle
```java
// After you have created a client and (optionally) added handlers, you can start it
client.start();

// As long as the client is running, you can send commands to the gpsd server
// More on that later...
client.sendCommand(...)
      .sendCommand(..., result -> { ... });

// Send a command to the server to enable dumping of messages
client.watch();

// To stop the client:
client.stop();
```

#### Persisting device settings and watch mode
Device settings and watch mode settings may be lost if the connection drops
or the gpsd server restarts. In order to persist them, you can set a connection
handler that gets executed upon each successful connection the gpsd server, including
reconnections.
```java
new GpsdClient(...)
    .setSuccessfulConnectionHandler(client -> {
       DeviceMessage device = new DeviceMessage();
       device.setPath("/dev/ttyAMA0");
       device.setNative(true);

       client.sendCommand(device);
       client.watch();
    })
    .addHandler(TPVReport.class, tpv -> { ... })
    .start();
```


### Sending commands
There are multiple ways of sending commands to the server. In order to send commands,
the client must be started and running. Otherwise, an `IllegalStateException` may be thrown.

#### Sending a command and expecting a response
```java
// The response is the same type as the command message (subtypes of GpsdCommandMessage)
client.sendCommand(new PollMessage(), pollMessageResponse -> {
    Integer activeDevices = pollMessageResponse.getActiveCount();
});
```

#### Sending a command and not awaiting a response
```java
// Setup the GPS device to run in its native mode
DeviceMessage device = new DeviceMessage();
device.setPath("/dev/ttyAMA0");
device.setNative(true);

client.sendCommand(device); 
```


### Putting it all together
```java
new GpsdClient("localhost", 2947)
      .addErrorHandler(System.err::println)
      .addHandler(TPVReport.class, tpv -> {
          Double lat = tpv.getLatitude();
          Double lon = tpv.getLongitude();

          System.out.printf("Lat: %f, Lon: %f\n", lat, lon);
      })
      .addHandler(SKYReport.class, sky -> {
          System.out.printf("We can see %d satellites\n", sky.getSatellites().size())
      })
      .setSuccessfulConnectionHandler(client -> {
          DeviceMessage device = new DeviceMessage();
          device.setPath("/dev/ttyAMA0");
          device.setNative(true);

          client.sendCommand(device);
          client.watch();
      })
      .start();
```
