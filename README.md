JavaFBP 
===

### Java Implementation of "Classical" Flow-Based Programming (FBP)  

General web site on Flow-Based Programming: https://jpaulm.github.io/fbp/ .

Latest release is `v4.1.8`.  The jar file - `javafbp-4.1.8.jar` - can be obtained from the Releases folder, from `build/libs`, or from Maven.  <!-- Note: The Maven 'shield' below will show the new release for a little while before it actually becomes available. --> After clicking on the Maven shield below, select `Download`, then `jar`.

[![Maven Central](https://img.shields.io/maven-central/v/com.jpaulmorrison/javafbp.svg?label=JavaFBP)](https://search.maven.org/search?q=g:%22com.jpaulmorrison%22%20AND%20a:%22javafbp%22)

This implementation is a kit for building JavaFBP projects.  For a number of sample networks, go to 
https://github.com/jpaulm/javafbp/tree/master/src/main/java/com/jpaulmorrison/fbp/resourcekit/examples .

For your own projects, include the JavaFBP jar file in the Build Path property for the project.

## General
 
General web site for "classical" FBP: 
* https://jpaulm.github.io/fbp

In computer programming, flow-based programming (FBP) is a programming paradigm that defines applications as networks of "black box" processes, which exchange data across predefined connections by message passing, where the connections are specified externally to the processes. These black box processes can be reconnected endlessly to form different applications without having to be changed internally. FBP is thus naturally component-oriented.

FBP is a particular form of dataflow programming based on bounded buffers, information packets with defined lifetimes, named ports, and separate definition of connections.
 
JavaFBP Syntax and Component API:
* https://jpaulm.github.io/fbp/jsyntax.htm

An automatically generated Javadoc can also be browsed at http://jpaulm.github.io/javafbp/ .  Unfortunately this isn't very useful for someone planning to use JavaFBP components, so we have built an FBP-specific Component Attributes List, which can be displayed by clicking on http://htmlpreview.github.io/?https://github.com/jpaulm/javafbp/blob/master/compList.html  - see below.

<!--
## Running your JavaFBP project under Eclipse
 

**Add the current JavaFBP jar file to the `External Jar Files` tab in your project's `Java build path` properties.**

In your component source, you will need the following import statement:

    import com.jpaulmorrison.fbp.core.engine.*;
    
Note: the **core** level was added in the last repackaging of JavaFBP.

-->

## MySQL Support

Two components are available for reading and writing MySQL tables, respectively: `ReadJDBC` and `WriteJDBC`, in `core.components.jdbc`.  These dynamically load the most recent MySQL jar file, and use reflection to execute SQL services.

If you get a message saying "No suitable driver found", try restarting the MySQL service in `services.msc`.
 
## JavaFBP-WebSockets

There is also a small GitHub project called `javafbp-websockets`, which contains two generalized components supporting WebSockets ( https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API ), and a simple test component and network - it can be found at https://github.com/jpaulm/javafbp-websockets .

<!--## Prerequisites for rebuilding JavaFBP or derivative

The project requires Gradle for (re)building. You can download the corresponding package from the following URL: 
http://www.gradle.org

Windows and Linux users should follow the installation instructions on the Maven website.

OSX users (using Brew, http://brew.sh) can install Maven by executing the following command:

    brew install gradle


## Eclipse IDE Integration with Gradle

You can generate an Eclipse project using the following command:

    gradle eclipse

If you already created an Eclipse project you can run:

    gradle cleanEclipse Eclipse

You need to install a Gradle plugin for Eclipse as explained here:
https://github.com/spring-projects/eclipse-integration-gradle/
Then import a generated project in Eclipse, right (ctrl for OSX) click on the project in Eclipse -> Configure -> Convert to Gradle Project. After the conversion you can Right (ctrl for OSX) click on the project -> Gradle -> Task Quick Launcher and type `build`.

You may have to go to the project Properties and select Java Build Path/Source; remove whatever is there and select `JavaFBP/src/main/java`; then close Eclipse, and reopen it.

<!--
## Building and/or running from command line

The latest jar file can simply be downloaded from the latest release in JavaFBP GitHub Releases, or it can be rebuilt , by running the following command:

    gradle build

As a result a `javafbp-x.x.x.jar` file will be created in the `build/libs` directory. It will include the JavaFBP core (runtime) and all the examples from the source code (sub-package `com.jpmorrsn.fbp.resourcekit.examples`). 

**`resourcekit` is now in the hierarchy, as of version v4.0.1** .

The generated code shown above is a standard JavaFBP network, and can be executed as described below.

-->
## Running a network

### Default connection size

JavaFBP recommends using a small connection size for debugging, and a larger one for production.  In the latest version of JavaFBP, this is specified in the `<user>.JavaFBPProperties.xml` file - <defaultcapacity> tag.
 
### Running on DOS

You will have downloaded the JavaFBP jar file earlier, so do a `gradle build` for your project, to make sure the compiled classes (`.class` files) are in the 'bin' directory.

Now position the current directory to your own project, using `cd`, and enter the following into the DOS window:

      java -cp "<JavaFBP directory>/javafbp-x.y.z.jar;." <program class name> 
      
where `x.y.z` is the version of the JavaFBP jar file.  Note the final **;.**... or you can place **.;** in front of the jar file name.

`Program class name` must be the fully qualified network name.  

You must also make sure that `java` can find any class files that yor main line needs, by specifying the nwecssary jar files, and class directories using the `-cp/-classpath` parameter.
 
Here is a test command you can run, using networks and components provided by the JavaFBP project:

Do a `cd` command to your downloaded JavaFBP project, then enter
 
     java -cp .;build/libs/javafbp-x.y.z.jar com.jpaulmorrison.fbp.resourcekit.examples.networks.Copy1    
     
For your own project, you will probably have to add more directories to the `classpath` parameter - remember to provide the whole directory name.

### Running on *nix
      
Replace the ';'  in the `-cp` parameter with ":" for *nix.   

### Running in Eclipse

Go to `Properties/Java Build Path` for your project; click on `Add External Jars`, add your JavaFBP jar file to the list, and then hit `Apply` and `OK`.   

Select `Debug` for your project.

<!--
### Set up some data   

Nearly forgot - we need to give it some data: ReadFile handles any sequential file.  In this case the file reader's IIP names at a CSV file (https://en.wikipedia.org/wiki/Comma-separated_values ), and the selected records will appear in a separate window. 

-->   

## Checking your setup
 

Here is a simple command-line test that can be run to test that everything is working.

Position to the `javafbp` directory; then enter

    java -cp "build/libs/javafbp-x.y.z.jar;." com.jpaulmorrison.fbp.resourcekit.examples.networks.MergeandSort

where `x.y.z` is the current version number of JavaFBP.

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

## Warning!
 
Care must be taken if combining `LoadBalance` (with substreams) and `SubstreamSensitiveMerge` in a divergent-convergent pattern - this pattern is one of the warning signals for deadlocks anyway. The problem is described in more detail under https://github.com/jpaulm/javafbp/issues/8.

## Tracing and other options
 

To trace JavaFBP services and/or lock usage, set the appropriate parameter(s) in `JavaFBPProperties.xml` in the user directory to `true`:

* `tracing` 
* `tracelocks`
 
e.g.

    <?xml version="1.0"?> 
    <properties> 
    <tracing>true</tracing>
    <tracelocks>false</tracelocks>
    <defaultcapacity>...</defaultcapacity>    (as of v4.1.3)
    </properties> 
    
These traces will appear in the project directory (in GitHub if running Eclipse) under the name `xxxx-fulltrace.txt`, where `xxxx` is the name of the network being run.  Subnets have their own trace output files. 

Values for `defaultcapacity` are as follows:

- PRODUCTION (currently 10)
- DEBUG (currently 1)
- any number
- `defaultcapacity` not specified - defaults to DEBUG value

Two other options are also supported in the properties file:

* `deadlocktest` (defaults to true, so you might set it to `false` if debugging) 
* `forceconsole` (used if immediate console output is required during debugging - normally, console output is sent to a file)
