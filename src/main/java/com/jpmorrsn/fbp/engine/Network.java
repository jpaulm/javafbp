package com.jpmorrsn.fbp.engine;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * The abstract class which all flow networks extend directly or indirectly. A
 * specific flow network must override the <code>define()</code> method, which
 * is written using the <i>mini-language</i> (actually just highly restricted
 * Java invoking the <code>protected</code> methods of this class). The
 * mini-language specifies what threads are to be created using which
 * components, and what connections are established between the ports of those
 * components. 
 */

public abstract class Network extends Component {

  /***************************************************************************
   * Copyright 2007, ..., 2012, J. Paul Morrison. At your option, you may copy,
   * distribute, or make derivative works under the terms of the Clarified
   * Artistic License, based on the Everything Development Company's Artistic
   * License. A document describing this License may be found at
   * http://www.jpaulmorrison.com/fbp/artistic2.htm. THERE IS NO WARRANTY; USE
   * THIS PRODUCT AT YOUR OWN RISK.
   */

  protected static int DEBUGSIZE = 1;

  protected static int PRODUCTIONSIZE = 10;

  //static int defaultCapacity = DEBUGSIZE; // change this when you go to production
  static int defaultCapacity = PRODUCTIONSIZE; // use this one for production  

  /* Following 4 booleans set by properties file */

  private boolean tracing = false;

  private boolean traceLocks = false;

  private boolean forceConsole = false; // for debugging only

  protected boolean deadlockTest = true;

  /*   */

  public boolean runTimeReqd = true;

  final String traceLockFile = "fulltrace.txt";

  BufferedWriter traceWriter = null; // trace buffered writer

  boolean active = false; // used for deadlock detection

  /**
   * Components is a global final synchronizedMap, containing the components in the (sub)network
   * Access through getComponents and setComponents
   */
  private final Map<String, Component> components = Collections.synchronizedMap(new HashMap<String, Component>());

  CountDownLatch cdl = null;

  HashMap<Component, TimeoutHandler> timeouts = new HashMap<Component, TimeoutHandler>();

  /**
   * globals is a global final synchronizedMap intended for real global use.
   * It is not intended for component2component communication.
   * 
   */
  protected Map<String, Object> globals = Collections.synchronizedMap(new HashMap<String, Object>());

  volatile boolean deadlock = false;

  String name;

  private Exception error;

  private boolean abort = false;

  Vector<String> msgs = null; // for use by listCompStatus()

  private boolean useConsole = false;

  private final String tracePath = "";

  static LinkedList<BufferedWriter> traceFileList = new LinkedList<BufferedWriter>();

  //  static Network network;

  private final Map<String, BigInteger> IPCounts = Collections.synchronizedMap(new HashMap<String, BigInteger>());

  File propertiesFile = null;

  HashMap<String, String> properties = new HashMap<String, String>();

  AtomicInteger sends, receives, creates, drops, dropOlds;

  /**
   * Drive define method of network
   * @throws Exception 
   */

  void callDefine() throws Exception {
    // don't turn every exception into a FlowError
    //    try {
    define();
    //    } catch (Throwable t) {
    //      FlowError.complain(t.getMessage());
    //    }
  }

  /**
   * Returns a Component class object, given the component name in the network
   */

  protected final Component component(final String nme) {
    Component comp = getComponent(nme);
    if (comp == null) {
      FlowError.complain("Reference to unknown component " + nme);
    }
    return comp;
  }

  /**
   * Stores the Component class object with its network name in the Hashtable
   * called 'components'
   */

  protected final Component component(final String nme, final Class tpe) {

    if (getComponent(nme) != null) {
      FlowError.complain("Attempt to redefine component " + nme);
    }

    //Pattern p = Pattern.compile("^([_ \\p{N}\\p{L}]+)(\\[(\\d+)\\])?$");
    //Matcher ma = p.matcher(nme);
    //if (!ma.matches()) {
    //  FlowError.complain("Invalid process name (only underscores, blanks, letters and numbers allowed): " + nme);
    //}
    Component comp = null;
    try {
      comp = (Component) tpe.newInstance();
    } catch (IllegalAccessException e) {
      FlowError.complain("Illegal access to component: " + nme);
      return null; // unreachable
    } catch (InstantiationException e) {
      FlowError.complain("Cannot instantiate component: " + nme);
      return null; // unreachable
    }

    comp.setName(nme);
    comp.type = tpe;
    putComponent(nme, comp);
    comp.mother = this;
    Network m = this;
    while (true) {
      if (!(m instanceof SubNet)) {
        comp.network = m;
        break;
      }
      m = m.mother;
    }

    comp.status = StatusValues.NOT_STARTED;

    comp.buildAnnotations();

    return comp;
  }

  /**
   * Connect an output port of one component to an input port of another,
   * specifying a connection capacity.
   * The following connect()'s are all various combinations of String notation and Component/Port notation
   *   plus optional capacity, and optional IP counting
   */

  protected final Connection connect(final Component sender, final Port outP, final Component receiver, final Port inP,
      final int size, final boolean IPCount) {

    int cap = size;
    if (size == 0) {
      cap = defaultCapacity;
    }

    if (outP.displayName.equals("*")) {
      outP.displayName = "*OUT";
    }
    if (inP.displayName.equals("*")) {
      inP.displayName = "*IN";
    }

    /* start processing output port */

    OutputPort op = null;
    if (!outP.displayName.substring(0, 1).equals("*")) {
      op = sender.outputPorts.get(outP.name); // try to find output port with port name - no index
      if (op == null) {
        FlowError.complain("Output port not defined in metadata: " + sender.getName() + "." + outP.displayName);
      }

      // at this point, op may contain: 
      //  - an OutArray, if the metadata specified an array port      
      //  - a NullOutputPort, if the metadata did not specify array port
      //  - an OutputPort, if a previous connect specified a source port with the same name, and no index
      //  - null

      if (op instanceof OutArray && outP.index == -1) {
        outP.index = 0;
        outP.displayName += "[0]";
      }

      if (outP.index > -1 && !(op instanceof OutArray)) {
        FlowError
            .complain("Output port not defined as array in metadata: " + sender.getName() + "." + outP.displayName);
      }
    }

    Class tp = null;
    if (op != null) {
      tp = op.type;
    }

    op = sender.outputPorts.get(outP.displayName); // try to find output port with port name - with index

    // at this point, op may contain:  
    //  - an OutputPort, if a previous connect specified a source port with the same name and index
    //  - a NullOutputPort, if the port is an array port, with setDimension specified  OR
    //                      if the metadata did not specify array port
    //  - null

    //if (!(op instanceof NullOutputPort) && !(op instanceof OutArray) && op.cnxt != null) {
    if (op != null && !(op instanceof NullOutputPort)) {
      FlowError.complain("Multiple connections from same output port:" + sender.getName() + ' ' + outP.displayName);
      //}
    }

    op = new OutputPort();
    op.type = tp; //  ???? experimental code copying type info from NullOutputPort or OutArray generated by Component.procOpty

    op.port = outP;
    op.setSender(sender);
    op.name = outP.displayName;
    //op.optional = x;
    op.fullName = sender.getName() + "." + op.name;
    sender.outputPorts.put(op.name, op);

    /* start processing input port */

    InputPort ip = null;
    if (!inP.displayName.substring(0, 1).equals("*")) {
      ip = receiver.inputPorts.get(inP.name);

      // at this point, ip may contain: 
      //  - a ConnArray
      //  - a Connection, if a previous connect specified a destination port with the same name, and no index
      //  - an InitializationConnection, if a previous initialize specified a destination port with the same name, and no index
      //  - a NullConnection
      //  - null

      if (ip == null) {
        FlowError.complain("Input port not defined in metadata: " + receiver.getName() + "." + inP.displayName);
      }

      if (ip instanceof ConnArray) {
        tp = ((ConnArray) ip).type;
      } else if (ip instanceof Connection) {
        tp = ((Connection) ip).type;
      } else if (ip instanceof InitializationConnection) {
        tp = ((InitializationConnection) ip).type;
      } else if (ip instanceof NullConnection) {
        tp = ((NullConnection) ip).type;
      }

      if (ip instanceof ConnArray && inP.index == -1) {
        inP.index = 0;
        inP.displayName += "[0]";
      }

      if (inP.index > -1 && !(ip instanceof ConnArray)) {
        FlowError
            .complain("Input port not defined as array in metadata: " + receiver.getName() + "." + inP.displayName);
      }
    }

    ip = receiver.inputPorts.get(inP.displayName); // try to find output port with port name - with index

    // at this point, ip may contain:  
    //  - a Connection, if a previous connect specified a destination port with the same name and index
    //  - an InitializationConnection, if a previous initialize specified a destination port with the same name and index
    //  - a NullConnection, if the port is an array port, with setDimension specified  OR
    //                      if the metadata did not specify array port
    //  - null

    Connection c;
    if (ip instanceof Connection) {
      if (size != 0 && size != cap) {
        FlowError.complain("Connection capacity does not agree with previous specification\n " + receiver.getName()
            + "." + inP.displayName);
      }
      c = (Connection) ip;
    } else {
      if (ip instanceof InitializationConnection) {
        FlowError.complain("Mixed connection to input port: " + receiver.getName() + "." + inP.displayName);
      }
      c = new Connection(cap);
      c.type = tp;
      c.setPort(inP);
      c.setReceiver(receiver);
      //c.name = in.displayName;
      c.IPCount = IPCount;
      c.setName(receiver.getName() + "." + inP.displayName);
      receiver.inputPorts.put(inP.displayName, c);
    }

    c.bumpSenderCount();
    op.cnxt = c;
    c.receiver = receiver;
    return c;

  }

  protected final Connection connect(final Component sender, final Port outP, final String receiver, final int size,
      final boolean IPCount) {
    String[] parts;
    parts = cPSplit(receiver);
    return connect(sender, outP, component(parts[0]), port(parts[1]), size, IPCount);
  }

  protected final Connection connect(final String sender, final Component receiver, final Port inP, final int size,
      final boolean IPCount) {
    String[] parts;
    parts = cPSplit(sender);
    return connect(component(parts[0]), port(parts[1]), receiver, inP, size, IPCount);
  }

  protected final Connection connect(final String sender, final String receiver, final int size, final boolean IPCount) {
    String[] sParts;
    sParts = cPSplit(sender);
    String[] rParts;
    rParts = cPSplit(receiver);
    return connect(component(sParts[0]), port(sParts[1]), component(rParts[0]), port(rParts[1]), size, IPCount);
  }

  /**
   * Same but with capacity and count parameter reversed
   */

  protected final Connection connect(final Component sender, final Port outP, final Component receiver, final Port inP,
      final boolean IPCount, final int size) {
    return connect(sender, outP, receiver, inP, size, IPCount);
  }

  protected final Connection connect(final Component sender, final Port outP, final String receiver,
      final boolean IPCount, final int size) {
    return connect(sender, outP, receiver, size, IPCount);
  }

  protected final Connection connect(final String sender, final Component receiver, final Port inP,
      final boolean IPCount, final int size) {
    return connect(sender, receiver, inP, size, IPCount);
  }

  protected final Connection connect(final String sender, final String receiver, final boolean IPCount, final int size) {
    return connect(sender, receiver, size, IPCount);
  }

  /**
   * Connect an output port of one component to an input port of another,
   * using default capacity, but specifying IPCount
   */
  protected final Connection connect(final Component sender, final Port outP, final Component receiver, final Port inP,
      final boolean IPCount) {
    return connect(sender, outP, receiver, inP, 0, IPCount);
  }

  protected final Connection connect(final Component sender, final Port outP, final String receiver,
      final boolean IPCount) {
    return connect(sender, outP, receiver, 0, IPCount);
  }

  protected final Connection connect(final String sender, final Component receiver, final Port inP,
      final boolean IPCount) {
    return connect(sender, receiver, inP, 0, IPCount);
  }

  protected final Connection connect(final String sender, final String receiver, final boolean IPCount) {
    return connect(sender, receiver, 0, IPCount);
  }

  /**
  * Connect an output port of one component to an input port of another,
  * using default IPCount, but specifying capacity
  */

  protected final Connection connect(final Component sender, final Port outP, final Component receiver, final Port inP,
      final int size) {
    return connect(sender, outP, receiver, inP, size, false);
  }

  protected final Connection connect(final Component sender, final Port outP, final String receiver, final int size) {
    return connect(sender, outP, receiver, size, false);
  }

  protected final Connection connect(final String sender, final Component receiver, final Port inP, final int size) {
    return connect(sender, receiver, inP, size, false);
  }

  protected final Connection connect(final String sender, final String receiver, final int size) {
    return connect(sender, receiver, size, false);
  }

  /**
   * Connect an output port of one component to an input port of another,
   * using default IPCount and capacity
   */

  protected final Connection connect(final Component sender, final Port outP, final Component receiver, final Port inP) {
    return connect(sender, outP, receiver, inP, 0, false);
  }

  protected final Connection connect(final Component sender, final Port outP, final String receiver) {
    return connect(sender, outP, receiver, 0, false);
  }

  protected final Connection connect(final String sender, final Component receiver, final Port inP) {
    return connect(sender, receiver, inP, 0, false);
  }

  protected final Connection connect(final String sender, final String receiver) {
    return connect(sender, receiver, 0, false);
  }

  // splits a string into component part and port part

  String[] cPSplit(final String s) {
    int i = s.indexOf(".");
    if (i < 0) {
      FlowError.complain("Invalid receiver string: " + s);
    }
    String[] p = { s.substring(0, i), s.substring(i + 1) };
    return p;
  }

  protected abstract void define() throws Exception;

  Iterator<Component> enumerateComponents() {
    /*
     * Make a copy of the concurrent component list to prevent 
     * ConcurrentModificationExceptions during building the network. This can
     * occur when enumerateComponents() is called from another thread during 
     * this phase.
     * 
     * components is a Map returned by Collections.synchronizedMap() - see 
     * declaration above. All operations are implicitly synchronized except
     * iterating over the items of the map. Therefore we have to synchronize
     * the following access. 
     */
    ArrayList<Component> currentComponents = new ArrayList<Component>();
    synchronized (getComponents()) {
      for (Component component : getComponents().values()) {
        currentComponents.add(component);
      }
    }
    return currentComponents.iterator();
  }

  /**
   * Execute method used by network being used as if it were a component
   */

  @Override
  protected void execute() throws Exception {
    // overridden by specific networks
  }

  /**
  * Execute network as a whole
   * @throws Exception
  */
  public final void go() throws Exception {

    receives = new AtomicInteger(0);
    sends = new AtomicInteger(0);
    creates = new AtomicInteger(0);
    drops = new AtomicInteger(0);
    dropOlds = new AtomicInteger(0);

    long now = System.currentTimeMillis();

    network = this;
    //  setTracePath("/");  //used for testing

    name = this.getClass().getName();
    int i = name.lastIndexOf(".");
    if (i > -1) {
      name = name.substring(i + 1);
    }
    setName(name); // set Thread name

    readPropertiesFile();

    String p = properties.get("tracing");
    if (p != null && p.equals("true")) {
      tracing = true;
    }
    p = properties.get("tracelocks");
    if (p != null && p.equals("true")) {
      traceLocks = true;
    }
    p = properties.get("deadlocktest"); //defaults to true
    if (p != null && p.equals("false")) {
      deadlockTest = false;
    }
    p = properties.get("forceconsole");
    if (p != null && p.equals("true")) {
      forceConsole = true;
    }

    /**
     * During go() no ConcurrentModification error can occur, because the network is
     * finished constructing so the components will not change. If it could, then
     * synchronized would be necessary.
     * 
     */
    try {
      callDefine();
      boolean res = true;
      for (Component comp : getComponents().values()) {
        res &= comp.checkPorts();
      }
      if (!res)
    	  FlowError.complain("One or more mandatory connections have been left unconnected: " + getName());
      active = true;
      initiate();

      waitForAll();

    } catch (FlowError e) {
      String s = "Flow Error :" + e;
      System.out.println("Network: " + s);
      System.out.flush();
      // rethrow the exception for external error handling
      // in case of a deadlock: deadlock is the cause
      throw e;
    }

    if (error != null) {
      // throw the exception which caused the network to stop
      throw error;
    }

    if (runTimeReqd) {
      long duration = System.currentTimeMillis() - now;
      long s = duration / 1000;
      long ms = duration % 1000;
      String mss = "000";
      String ms2 = mss.concat(String.valueOf(ms));
      i = ms2.length();
      String ms3 = ms2.substring(i - 3, i);
      traceFuncs("Run complete.  Time: " + s + '.' + ms3 + " seconds");
      closeTraceFiles();
      System.out.println("Run complete.  Time: " + s + '.' + ms3 + " seconds");
      System.out.println("Counts: C: " + creates + ", D: " + drops + ", S: " + sends + ", R (non-null): " + receives
          + ", DO: " + dropOlds);
      System.out.flush();
    }

    // ps.close();

    // }

  }

  void indicateTerminated(final Component comp) {
    synchronized (comp) {
      comp.status = StatusValues.TERMINATED;
    }
    traceFuncs(comp.getName() + ": Terminated");

    cdl.countDown();
    // net.interrupt();
  }

  /**
   * Build InitializationConnection object
   */

  protected final void initialize(final Object content, final Component receiver, final Port inP) {
    // if (inName.equals("*")) inName = "*IN";
    //String inName = inPort.displayName;

    InputPort ip = null;
    if (!inP.displayName.substring(0, 1).equals("*")) {
      ip = receiver.inputPorts.get(inP.name); // try to get entry with no index 
      // at this point, ip may contain: 
      //  - a ConnArray
      //  - a Connection, if a previous connect specified a destination port with the same name, and no index
      //  - an InitializationConnection, if a previous initialize specified a destination port with the same name, and no index
      //  - null

      if (ip == null) {
        FlowError.complain("Input port not defined in metadata: " + receiver.getName() + "." + inP.displayName);
      }

      if (ip instanceof ConnArray && inP.index == -1) {
        inP.index = 0;
        inP.displayName += "[0]";
      }

      if (inP.index > -1 && !(ip instanceof ConnArray)) {
        FlowError
            .complain("Input port not defined as array in metadata: " + receiver.getName() + "." + inP.displayName);
      }
    }
    ip = receiver.inputPorts.get(inP.displayName); // try to get entry for indexed name 

    // at this point, ip may contain:  
    //  - a Connection, if a previous connect specified a destination port with the same name and index
    //  - an InitializationConnection, if a previous initialize specified a destination port with the same name and index
    //  - a NullConnection, if the port is an array port, with setDimension specified  OR
    //                      if the metadata did not specify array port
    //  - null

    if (ip != null) {
      if (ip instanceof Connection || ip instanceof ConnArray) {
        FlowError.complain("IIP port cannot be shared: " + receiver.getName() + "." + inP.displayName);
      }
      if (ip instanceof InitializationConnection) {
        FlowError.complain("IIP port already used: " + receiver.getName() + "." + inP.displayName);
      }
    }

    InitializationConnection ic = new InitializationConnection(content, receiver);
    ic.setName(receiver.getName() + "." + inP.displayName);
    //ic.network = this;

    ic.setPort(inP);

    receiver.inputPorts.put(inP.displayName, ic);
  }

  protected final void initialize(final Object content, final String receiver) {
    String parts[] = cPSplit(receiver);
    initialize(content, component(parts[0]), port(parts[1]));
  }

  /**
   * Go through components opening ports, and activating those which are
   * self-starting (have no input connections)
   */
  void initiate() {

    cdl = new CountDownLatch(getComponents().size());

    for (Component comp : getComponents().values()) {
      comp.openPorts();
    }

    ArrayList<Component> selfStarters = new ArrayList<Component>();
    for (Component comp : getComponents().values()) {
      comp.autoStarting = true;

      if (!comp.selfStarting) {
        for (InputPort port : comp.inputPorts.values()) {
          if (port instanceof Connection) {
            comp.autoStarting = false;
            break;
          }
        }
      }

      if (comp.autoStarting) {
        selfStarters.add(comp);
      }
    }

    for (Component comp : selfStarters) {
      comp.activate();
    }
  }

  /**
   * Interrupt all components
   */

  void interruptAll() {

    System.out.println("*** Crashing whole application!");
    System.out.flush();

    System.exit(0); // trying this - see if more friendly!

  }

  /**
   * method to open ports for subnet
   */

  @Override
  protected void openPorts() { // dropped final - should never get executed

  }

  /**
   * method to register a port name for subnet
   */

  protected final Port port(final String nme) {

    return new Port(nme, -1);

  }

  /**
   * method to register an array port with index
   */

  protected final Port port(final String nme, final int index) {
    int i = nme.indexOf("*");
    if (i > 0) { //  if asterisk in name, must be in first position    
      FlowError.complain("Stray * in port name " + nme);
    }
    return new Port(nme, index);
  }

  /**
   * Test if network as a whole has terminated
   */

  void waitForAll() {

    boolean possibleDeadlock = false;

    long freq = 500L; // check every .5 second
    //long freq = 600000L; // check every 10 mins.

    while (true) {
      boolean res = true;
      // GenTraceLine("Starting await");
      try {
        if (deadlockTest) {
          res = cdl.await(freq, TimeUnit.MILLISECONDS);
        } else {
          cdl.await();
          res = true;
        }
      } catch (InterruptedException e) {
        FlowError.complain("Network " + getName() + " interrupted");
        break; // unreachable
      }
      if (res) {
        break;
      }

      // if an error occurred, skip deadlock testing
      if (error != null) {
        break;
      }

      // if the network was aborted, skip deadlock testing 
      if (abort) {
        break;
      }

      // time elapsed
      if (!deadlockTest) {
        continue;
      }

      // enabled
      testTimeouts(freq);
      if (active) {
        active = false; // reset flag every 1/2 sec
      } else if (!possibleDeadlock) {
        possibleDeadlock = true;
      } else {
        deadlock = true; // well, maybe
        // so test state of components
        msgs = new Vector<String>();
        msgs.add("Network has deadlocked"); // add in case msgs are printed
        if (listCompStatus(msgs)) { // if true, it is a deadlock
          //          interruptAll();
          for (String m : msgs) {
            System.out.println(m);
          }
          // FlowError.Complain("Deadlock detected");
          System.out.println("*** Deadlock detected in Network ");
          System.out.flush();
          // terminate the net instead of crashing the application
          terminate();
          // tell the caller a deadlock occurred
          FlowError.complain("Deadlock detected in Network");
          break;
        }
        // one or more components haven't started or
        // are in a long wait
        deadlock = false;
        possibleDeadlock = false;

      }
    } // while

    for (Component c : getComponents().values()) {
      try {
        c.join();
      } catch (InterruptedException e) {
        FlowError.complain("Component " + c.getName() + " interrupted");
        break; // unreachable
      }
    }
  }

  /**
   * Queries the status of the subnet's components.
   * 
   * returns true if it is a deadlock, else false
   * 
   * @param msgs the message vector for status lines
   */

  synchronized boolean listCompStatus(final Vector<String> mss) {

    // Messages are added to list, rather than written directly,
    // in case it is not a deadlock

	boolean terminated = true;  
    for (Component comp : getComponents().values()) {
      if (comp instanceof SubNet) {
        // consider components of subnets
        SubNet subnet = (SubNet) comp;
        if (!subnet.listCompStatus(mss)) {
          return false;
        }
      } else {
        if (comp.getStatus() == StatusValues.ACTIVE || comp.getStatus() == StatusValues.LONG_WAIT) {
          return false;
        }
        String st = comp.getStatus().toString();
        st = (st + "            ").substring(0, 13);
        String cn = comp.getName();
        if (!(st.trim().equals("TERMINATED")))
        	terminated = false;
        if (st.trim().equals("SUSP_RECV")) {
          cn = comp.curConn.getName();
        }
        if (st.trim().equals("SUSP_SEND")) {
          cn = comp.curOutPort.getName();
        }

        mss.add(String.format("--- %2$s     %1$s", cn, st));
      }
    }

    return !terminated;

  }

  // called by WaitForAll method
  synchronized void testTimeouts(final long freq) {

    for (TimeoutHandler t : timeouts.values()) {
      t.decrement(freq); // if negative, complain
    }

  }

  /**
   * Handles errors in the network.
   * @param e the exception which specifies the error
   */
  void signalError(final Exception e) {
    // only react to the first error, the others presumably are inherited errors
    if (error == null) {
      // set the error field to let go() throw the exception
      error = e;
      // terminate the network's components
      for (Component comp : getComponents().values()) {
        comp.terminate(StatusValues.ERROR);
      }
    }
  }

  /**
   * Shuts down the network.
   */
  void terminate() {
    terminate(StatusValues.TERMINATED);
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.Component#terminate(com.jpmorrsn.fbp.engine.Component.StatusValues)
   */
  @Override
  protected void terminate(final StatusValues newStatus) {
    // prevent deadlock testing, components will be shut down anyway
    abort = true;
    for (Component comp : getComponents().values()) {
      comp.terminate(newStatus);
    }
  }

  /**
   * Sets a new path for trace files. By default the current directory will be
   * used.
   * @param path the trace path to set
   * Not used! 
   void setTracePath(final String path) {
    if (path.endsWith(File.separator) || path.equals("")) {
      tracePath = path;
    } else {
      // append the file name separator if it is missing and the path is not
      // empty
      tracePath = path + File.separator;
    }
  }
  */
  /**
   * Generate a function trace line on trace file for network or subnet
   */
  void traceFuncs(final String s) {
    if (tracing) {
      trace(s);
    }
  }

  /**
   * Generate a function trace line on trace file for network or subnet
   */
  void traceLocks(final String s) {
    if (traceLocks) {
      trace(s);
    }
  }

  /**
   * Generate either kind of trace line on trace file for network or subnet
   */

  synchronized void trace(final String s) {

    Date date = new Date(); // create date for "now"
    String str = "yyyy-MM-dd'T'HH:mm:ss:SSS";
    SimpleDateFormat fmt = new SimpleDateFormat(str);
    TimeZone timeZone = TimeZone.getTimeZone("UTC");
    fmt.setTimeZone(timeZone);
    String dt = fmt.format(date);

    String n = getTracingName();

    // forceConsole is used for debugging purposes to force writing to the console
    // useConsole will be set to true if the trace file could not be opened
    if (forceConsole || useConsole) {
      synchronized (network) {
        System.err.println(dt + " " + n + ": " + s);
        System.err.flush();
      }
      return;
    }
    if (traceWriter == null) {
      String fileName = tracePath + n + '-' + traceLockFile;
      try {
        traceWriter = new BufferedWriter(new FileWriter(fileName));
      } catch (IOException e) {
        // file cannot be created or opened - disable tracing
        synchronized (network) {
          System.err.println("Trace file " + fileName + " could not be opened - writing to console...");
          //tracing = false;
          System.err.println(dt + " " + n + ": " + s);
          System.err.flush();
        }
        // don't try to create or open the file on every invocation
        useConsole = true;
        return;
      }
      traceFileList.add(traceWriter);
      try {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        String d = dateFormatGmt.format(new Date());
        traceWriter.write("Run date and time: " + d + " GMT\nJavaFBP Version: " + VersionAndTimestamp.getVersion()
            + "; Date: " + VersionAndTimestamp.getDate() + "\n");
      } catch (IOException e) {
        //do nothing
      }
    }
    try {
      traceWriter.write(dt + " " + s + "\n");
      traceWriter.flush();
    } catch (IOException e) {
      //do nothing
    }

  }

  /**
   * Retrieves the hierarchical network name (for tracing). Such a name looks like "rootNetwork.subnet1.subnet2...thisSubnet".
   * @return the network name
   */
  protected String getTracingName() {
    String s = "";
    Network m = mother;
    if (m == null) {
      return getName();
    }
    s = getName();
    while (true) {
      if (m == null) {
        break;
      }
      s = m.getName() + "." + s;
      m = m.mother;
    }
    return s;
  }

  /**
   * Closes all trace files.
   */
  private void closeTraceFiles() {
    for (BufferedWriter x : traceFileList) {
      try {
        x.close();
      } catch (IOException e) {
        //do nothing
      }
    }
  }

  /**
   * @param iPCounts the iPCounts to set
   **
   void setIPCounts(final Map<String, BigInteger> iPCounts) {
    IPCounts = iPCounts;
  }
  */

  boolean readPropertiesFile() {

    if (propertiesFile == null) {
      String uh = System.getProperty("user.home");
      propertiesFile = new File(uh + File.separator + "JavaFBPProperties.xml");
      if (!propertiesFile.exists()) {
        return false;
      }
    }
    BufferedReader in = null;
    String s = null;
    try {
      in = new BufferedReader(new FileReader(propertiesFile));
    } catch (FileNotFoundException e) {
      return false;
    }
    // if (in == null)
    // return false;

    // boolean assoc = false;
    // String cd = null;
    while (true) {
      try {
        s = in.readLine();
      } catch (IOException e) {
        // do nothing
      }
      if (s == null) {
        break;
      }
      s = s.trim();
      if (s.equals("<properties>") || s.equals("</properties>")) {
        continue;
      }
      if (s.startsWith("<?xml")) {
        continue;
      }

      if (s.startsWith("<!--")) {
        continue;
      }

      int i = s.indexOf("<");
      int j = s.indexOf(">");
      if (i > -1 && j > -1 && j > i + 1) {
        String key = s.substring(i + 1, j);
        s = s.substring(j + 1);
        int k = s.indexOf("<");
        if (k > 0) {
          s = s.substring(0, k).trim();
          properties.put(key, s);
        }
      }
    }
    try {
      in.close();
    } catch (IOException e) {
      // nothing to do
    }
    return true;

  }

  /*

  void writePropertiesFile() {
    BufferedWriter out = null;

    try {
      out = new BufferedWriter(new FileWriter(propertiesFile));
      out.write("<?xml version=\"1.0\"?> \n");
      out.write("<properties> \n");
      for (String k : properties.keySet()) {
        String s = "<" + k + "> " + properties.get(k) + "</" + k + "> \n";
        out.write(s);
      }
     
      out.write("</properties> \n");
      // Close the BufferedWriter
      out.flush();
      out.close();

    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }
  */

  /**
   * @return the iPCounts
   */
  public Map<String, BigInteger> getIPCounts() {
    return IPCounts;
  }

  /**
   * putGlobal provides access to the private final field globals, a synchronizedMap.
   
   * Use this carefully as global data creates a fat coupling.
   * 
   * @param s The String key to the global field 
   * @param o The Object value to be stored at the String key.
   * @return Object containing the old entry if one is replaced, or null, if none is replaced.
   */
  @Override
  @Deprecated
  // This method has been moved to Component
  protected Object putGlobal(final String s, final Object o) {
    return globals.put(s, o);
  }

  /**
   * getGlobal provides access to the private final field globals, a synchronizedMap.
   * The specific global data required is accessed by means of a String key.
   * Use putGlobal to update.
   * 
   * @param s The String key to the global field 
   * @param o The Object value to be stored at the String key.
   * @return Object containing the entry if one is found, or null, if none is found.
   * 
   */
  @Override
  @Deprecated
  // This method has been moved to Component
  protected Object getGlobal(final String s) {
    return globals.get(s);
  }

  /**
   * Returns a final synchronizedMap containing the components in this (sub)network
   * Removed setComponents, as this is only set on construction and never changed.
   * 
   * @return the components
   */
  Map<String, Component> getComponents() {
    return components;
  }

  // replaced putComponents().get(name) / .put(name, comp) by getComponent(name), putComponent(name, comp).
  // getComponent still desirable for Network internal access and Subnet access to components.
  //

  /**
   * Returns the requested component in this network if present, null if not present.
   * 
   * @return the components
   */
  Component getComponent(final String nme) {
    return components.get(nme);
  }

  /**
   * Creates a component, if necessary replacing the old component. Returns null or the old component. 
   * 
   * @param name
   * @param component
   * @return the old component or null
   */
  Component putComponent(final String nme, final Component comp) {
    Component oldComponent = components.get(nme);
    components.put(nme, comp);
    return oldComponent;
  }

}