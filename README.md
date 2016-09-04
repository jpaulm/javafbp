JavaFBP
===

Java Implementation of "Classical" Flow-Based Programming (FBP)

General
---

General web site for "classical" FBP: 
* http://www.jpaulmorrison.com/fbp/

**Latest release of JavaFBP: `javafbp-4.1.0`** (Package qualifier changed from `jpmorrsn` to `jpaulmorrison`)

In computer programming, flow-based programming (FBP) is a programming paradigm that defines applications as networks of "black box" processes, which exchange data across predefined connections by message passing, where the connections are specified externally to the processes. These black box processes can be reconnected endlessly to form different applications without having to be changed internally. FBP is thus naturally component-oriented.

FBP is a particular form of dataflow programming based on bounded buffers, information packets with defined lifetimes, named ports, and separate definition of connections.
 
JavaFBP Syntax and Component API:
* http://www.jpaulmorrison.com/fbp/jsyntax.htm
  
Promoted to Maven central - do http://search.maven.org/#search%7Cga%7C1%7Cjavafbp .  

Javadoc can also be browsed at http://jpaulm.github.io/javafbp/  (as of v3.0.8)
 
JavaFBP-WebSockets
---

There is also a small GitHub project called `javafbp-websockets`, which contains two generalized components supporting WebSockets ( https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API ), and a simple test component and network - it can be found at https://github.com/jpaulm/javafbp-websockets .

Prerequisites
---


The project requires Gradle for building (tested with version 2.0). You can download the corresponding package from the following URL: 
http://www.gradle.org

Windows and Linux users should follow the installation instructions on the Maven website.

OSX users (using Brew, http://brew.sh) can install Maven by executing the following command:

    brew install gradle


Eclipse IDE Integration
---

You can generate Eclipse project using the following mvn command:

    gradle eclipse

If you already created an Eclipse project you can run:

    gradle cleanEclipse Eclipse

You need to install a Gradle plugin for Eclipse as explained here:
https://github.com/spring-projects/eclipse-integration-gradle/
Then import a generated project in Eclipse, right (ctrl for OSX) click on the project in Eclipse -> Configure -> Convert to Gradle Project. After the conversion you can Right (ctrl for OSX) click on the project -> Gradle -> Task Quick Launcher and type `build`.

You may have to go to the project Properties and select Java Build Path/Source; remove whatever is there and select `JavaFBP/src/main/java`; then close Eclipse, and reopen it.


Building and/or running from command line
---

The latest jar file can simply be downloaded from the latest release, or it can be rebuilt , by running the following command:

    gradle build

As a result a `javafbp-x.x.x.jar` file will be created in the `build/libs` directory. It will include the JavaFBP core (runtime) and all the examples from the source code (sub-package `com.jpmorrsn.fbp.resourcekit.examples`). 

**`resourcekit` is now in the hierarchy, as of version v4.0.1** .

For running any of the examples `cd` to your `javafbp` folder, and use the following command:

    java -cp build/libs/javafbp-x.x.x.jar com.jpaulmorrison.fbp.resourcekit.examples.networks.<Class name of the network>
    
use `jpaulmorrison` if version is 4.1.0 or later.    

For example:

    java -cp build/libs/javafbp-x.x.x.jar com.jpaulmorrison.fbp.resourcekit.examples.networks.TestIPCounting
    
To run one of your own classes, add `.;` in front of `build/`, and make sure your current directory is set to the one containing the highest qualification level in the chosen package.  

In *nix, replace the `;` with `:`.
    
Building/viewing Component Attributes List
--------

A function, `JavaFBPCompAttrs`,  has been added to the JavaFBP GitHub project to build a list of the component attributes for any specified list of JavaFBP component packages.  The bat file, `JavaFBPCompAttrs.bat` can be found in `src/main/resources`.

Mke sure you run `gradle build` to have the necessary `JavaFBPCompAttrs.class` file generated.

As delivered on GitHub, the `bat` file looks like this:

      javadoc -doclet com.jpaulmorrison.fbp.doclets.JavaFBPCompAttrs -docletpath target/classes -classpath "C:/Program Files/Java/jdk1.8.0_101/lib/tools.jar" -sourcepath src/main/java  com.jpaulmorrison.fbp.core.components.audio com.jpaulmorrison.fbp.core.components.io com.jpaulmorrison.fbp.core.components.misc com.jpaulmorrison.fbp.core.components.routing com.jpaulmorrison.fbp.core.components.swing com.jpaulmorrison.fbp.core.components.text

The directories to be scanned can be seen following `-sourcepath` and its operand - change to taste, in your copy!

To run the `bat` file, set your current directory to your `javafbp` folder.   Then run `src\main\resources\javafbpcompattrs.bat` .  The output will be found in `C:\Temp\JavaFBPCompAttrs.html`.  Open with your favorite browser.

Not all JavaFBP component attributes have been filled in as yet, but these will be expanded as time allows.

Running a test
----

Here is a simple command-line test that can be run to test that everything is working.

In the project directory, enter

    java -cp build/libs/javafbp-x.x.x.jar com.jpaulmorrison.fbp.resourcekit.examples.networks.MergeandSort

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

Warning!
-----
Care must be taken if combining `LoadBalance` (with substreams) and `SubstreamSensitiveMerge` in a divergent-convergent pattern - this pattern is one of the warning signals for deadlocks anyway. The problem is described in more detail under https://github.com/jpaulm/javafbp/issues/8.

Tracing and other options
---

To trace JavaFBP services and/or lock usage, set the appropriate parameter(s) in `JavaFBPProperties.xml` in the user directory to `true`:

* `tracing` 
* `tracelocks`
 
e.g.

    <?xml version="1.0"?> 
    <properties> 
    <tracing>true</tracing>
    <tracelocks>false</tracelocks>
    </properties> 

These traces will appear in the project directory (in GitHub if running Eclipse) under the name `xxxx-fulltrace.txt`, where `xxxx` is the name of the network being run.  Subnets have their own trace output files. 

Two other options are also supported in the properties file:

* `deadlocktest` (defaults to true, so you might set it to `false` if debugging) 
* `forceconsole` (used if immediate console output is required during debugging - normally, console output is sent to a file)
