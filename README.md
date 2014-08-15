JavaFBP
===

Java Implementation of Flow-Based Programming (FBP)


General
---

In computer programming, flow-based programming (FBP) is a programming paradigm that defines applications as networks of "black box" processes, which exchange data across predefined connections by message passing, where the connections are specified externally to the processes. These black box processes can be reconnected endlessly to form different applications without having to be changed internally. FBP is thus naturally component-oriented.

FBP is a particular form of dataflow programming based on bounded buffers, information packets with defined lifetimes, named ports, and separate definition of connections.

Web sites for FBP: 
* http://www.jpaulmorrison.com/fbp/
* https://github.com/flowbased/flowbased.org/wiki

Prerequisites
---


The project requires Gradle for building (tested with version 2.0). You can download the corresponding package from the following URL: 
http://www.gradle.org

Windows and Linux users should follow the installation instructions on the Maven website (URL provided above).

OSX users (using Brew, http://brew.sh) can install Maven by executing the following command:

    brew install gradle


Eclipse IDE Integration
---

You can generate Eclipse project using the following mvn command:

    gradle eclipse

If you already created an Eclipse project you can run:

    gradle cleanEclipse Eclipse

You need to install a Gradle plugin for Eclipse as explain here:
https://github.com/spring-projects/eclipse-integration-gradle/
Then import a generated project in Eclipse, right (ctrl for OSX) click on the project in Eclipse -> Configure -> Convert to Gradle Project. After the conversion you can Right (ctrl for OSX) click on the project -> Gradle -> Task Quick Launcher and type `build`.


Building from command line
---

For building the project simply run the following command:

    gradle build

As a result a `javafbp-1.1.jar` file will be created in the `build/libs` directory. It will include the JavaFBP core (runtime) and all the examples from the source code (sub-package `com.jpmorrsn.fbp.examples`). 

For running any of the examples use the following command:

    java -cp build/libs/javafbp-1.1.jar com.jpmorrsn.fbp.examples.networks.<Class name of the network>

For example:

    java -cp build/libs/javafbp-1.1.jar com.jpmorrsn.fbp.examples.networks.TestIPCounting


Running a test
----

Here is a simple test that can be run to test that everything is working.

In the project directory, enter

    java -cp build/libs/javafbp-1.1.jar com.jpmorrsn.fbp.examples.networks.MergeandSort

Here is a picture of MergeandSort, drawn using DrawFBP:

![MergeandSort](https://github.com/jpaulm/javafbp/blob/master/docs/MergeandSort.png "Diagram of MergeandSort Network")
    
This network contains 4 processes: 

* 2 occurrences of GenerateTestData, 
* a Sort process - a very simple-minded Sort, which can only handle up to 9,999 information packets 
* a text display component, which invokes Java Swing to display the sorted data in a scroll pane. 
 
The outputs of the two GenerateTestData processes are merged on a "first come, first served" basis.  During the run you should see a scroll pane with the sorted data scrolling down.

At the end of the run, you should see:

    Run complete.  Time: x.xxx seconds
    Counts: C: 150, D: 153, S: 300, R (non-null): 304, DO: 0
    
where the counts are respectively: creates, normal drops, sends, non-null receives, and drops done by "drop oldest".   

Running JavaFBP apps using Websockets
-----

This uses an additional jar file: `Java-WebSocket-1.3.1.jar`, see https://github.com/TooTallNate/Java-WebSocket/issues/118.  As this has not yet been released to the central Maven repository, we have to include and distribute the required jar in a local `lib` directory, which of course impacts publication of our own jar into the central Maven repository.

You can however run the test server code in com.jpmorrsn.fbp.websockets.networks.TestWebSockets by entering in the project directory

    java -cp "build\libs\javafbp-1.1.jar;lib\Java-WebSocket-1.3.1.jar" com.jpmorrsn.fbp.websockets.networks.TestWebSockets
    
(note the double quotes)    

Tracing and other options
---

To trace JavaFBP services and/or lock usage, set the appropriate parameter(s) in `JavaFBPProperties.xml` in the user directory to `true`:

* `tracing` 
* `tracelocks`

These traces will appear in the project directory under the name `xxxx-fulltrace.txt`, where `xxxx` is the name of the network being run.  Subnets have their own trace output files. 

Two other options are also supported in the properties file:

* `deadlocktest` (defaults to true, so you might set it to `false` if debugging) 
* `forceconsole` (used if immediate console output is required during debugging - normally, console output is sent to a file)
