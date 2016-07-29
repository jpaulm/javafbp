
Testfile (not the real readme.txt): only for test purposes.

A FRIENDLY PIECE OF ADVICE

First, a note of warning: it is not recommended that you try this software without reading the book first to get a basic understanding of the concepts and methodology.  This would be like being given a pile of girders and being told to build a bridge! This is not because the concepts are complex - it is rather that they involve a paradigm change, and it is much easier to follow where someone else has been before, rather than having to rediscover them for yourself!

If you have reached this point via my book, be sure to go to http://www.jpaulmorrison.com/fbp/jsyntax.htm for details on network and component syntax.

QUICK START GUIDE (Updated 13 June, 2009)

To get JavaFBP up and running, first make sure you have a properly installed Java runtime environment. If you don't have one, you can get one here:

http://www.java.com/en/download/manual.jsp

Then, from the same directory containing this README file, execute the following commands:

cd bin
java com.jpmorrsn.fbp.test.networks.MergeandSort

You should see a Swing text pane come up showing the data sorted in ascending sequence.  However you will see the first 50 IPs are doubled, while the rest are single.

If you've reached this point, your FBP installation is properly installed and functioning.

Let's examine the example in more detail.

The file com.jpmorrsn.fbp.test.networks.MergeandSort.java shows the JavaFBP network that generated the above output. Two generator processes are connected to a single sort process, which in turn is connected to a printer process. 

The two generator processes are connected into the same input port on the Sort process, so their outputs are received on a first-come, first-served basis.  One of these processes generates 100 records (data packets) in descending sequence, and the other 50. The two processes run asynchronously.  They are in fact two instances of Generate.java.  The Sort component is a "mickey-mouse" Sort module which can handle up to 9999 packets.  The reader will easily be able to write a better one!

ADDITIONAL DEMOS

Additional demos can be found in com.jpmorrsn.fbp.test.networks

HOW TO CREATE YOUR OWN NETWORK

1. Create a working directory.

2. Say your new network is for doing XYZ. Create a file called XYZ.java from the template com.jpmorrsn.fbp.test.networks.FBPNetworkTemplate.java 

3. Where the file says /* fill in your network definition here */ put in your XYZ code.

4. Remember to change the class reference in the "main" method to match the network name.

5. Change the copyright information as desired.

RUNNING UNDER ECLIPSE

1. Create a project under Eclipse

2. Make sure Eclipse sets up src and bin directories

3. Add the latest JavaFBP jar file to Project/Properties/JavaBuild Path/Libraries

4. Tracing is enabled as follows:

   Copy file called JavaFBPProperties.xml into your "user" directory, and set the tracing parameter to true or false.  If this file does not exist in that folder, tracing will of course be off.
   The trace will be written out to the directory where your application is running - the "unqualified" folder, and will be one or more files ending in "fulltrace.txt".

HOW TO CREATE YOUR OWN COMPONENT

1. Say your new component is for doing XYZ. Create a file called XYZ.java from the template com.jpmorrsn.fbp.components.FBPComponentTemplate.java 

2. Fill in any port variable definitions

3. Where the file says /* open ports logic */ put in the statements to open your ports and/or port arrays, and set object types for input ports.  Examples can be found in com.jpmorrsn.javaFBP.verbs, including FBPComponentTemplate.

4. Where the file says /* execute logic */ put in your XYZ execute logic

5. Specify metadata for port attributes and component and port descriptions.
 
6. Change the copyright information as desired.


FOR MORE INFORMATION

More details are available at http://www.jpaulmorrison.com/fbp/.  In particular the syntax for creating networks and a sample reusable JFBP component are shown in http://www.jpaulmorrison.com/fbp/jsyntax.htm.

Please send suggestions, bug reports and constructive criticism to:
paul.morrison@rogers.com.

