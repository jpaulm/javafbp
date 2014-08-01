JavaFBP
===

Java Implementation of Flow-Based Programming (FBP)


General
---

In computer programming, flow-based programming (FBP) is a programming paradigm that defines applications as networks of "black box" processes, which exchange data across predefined connections by message passing, where the connections are specified externally to the processes. These black box processes can be reconnected endlessly to form different applications without having to be changed internally. FBP is thus naturally component-oriented.

FBP is a particular form of dataflow programming based on bounded buffers, information packets with defined lifetimes, named ports, and separate definition of connections.

Prerequisites
---


The project requires Maven for building (tested with version 3.2.2). You can download the corresponding package from the following URL: 
https://maven.apache.org/download.cgi 

Windows and Linux users should follow the installation instructions on the Maven website (URL provided above).

OSX users (using Brew, http://brew.sh) can install Maven by executing the following command:

    brew install maven


IDE Integration
---

You can generate Eclipse project using the following mvn command:

    mvn eclipse:eclipse


Building
---

For building the project simply run the following command:

    mvn package

As a result a `fbp-1.0-SNAPSHOT.jar` file will be created in the `./target` directory. It will include the JavaFBP core (runtime) and all the examples from the source code (sub-package `com.jpmorrsn.fbp.examples`). 


For running any of the examples use the following command:

    java -cp target/fbp-1.0-SNAPSHOT.jar com.jpmorrsn.fbp.examples.networks.<Class name of the network>

For example:

    java -cp target/fbp-1.0-SNAPSHOT.jar com.jpmorrsn.fbp.examples.networks.TestIPCounting
    

Picking up Java-WebSocket-1.3.1.jar
-----

Currently this jar file is in a local repository called `repo`. It seems that this has to be deployed to the user's repository.  The command I used is 

    mvn deploy:deploy-file 
    -Durl=file://C:\Users\Paul\.m2\repository\repo/ 
    -Dfile=repo\org\Java-WebSocket\1.3.1\Java-WebSocket-1.3.1.jar 
    -DgroupId=org 
    -DartifactId=Java-WebSocket -Dpackaging=jar -Dversion=1.3.1
    
Hopefully a cleaner technique will be found soon.    

Tracing and other options
---

To trace JavaFBP services and/or lock usage, set the appropriate parameter(s) in `JavaFBPProperties.xml` to `true`:

* `tracing` 
* `tracelocks`

Two other options are also supported in the properties file:

* `deadlocktest` (defaults to true, so you might set it to `false` if debugging) 
* `forceconsole` (used if immediate console output is required during debugging)
