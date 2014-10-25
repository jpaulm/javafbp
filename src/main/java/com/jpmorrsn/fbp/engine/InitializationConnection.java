package com.jpmorrsn.fbp.engine;


/**
 * This class provides connections that hold just a single object at network
 * setup time. The <code>initialize</code> statement in the network
 * configuration mini-language creates instances of this class. It is a
 * degenerate form of Connection.
 * <p>
 * This class implements a type of parametrization of components - the
 * "parameter", which can be any object type, is associated with a port, and is
 * turned into a Packet when the first receive to that port is issued. This
 * occurs once per activation of that component. From the component's point of
 * view, it looks like a normal data stream containing one Packet.
 */

public class InitializationConnection implements InputPort {

  /***************************************************************************
   * Copyright 2007, 2012, J. Paul Morrison. At your option, you may copy,
   * distribute, or make derivative works under the terms of the Clarified
   * Artistic License, based on the Everything Development Company's Artistic
   * License. A document describing this License may be found at
   * http://www.jpaulmorrison.com/fbp/artistic2.htm. THERE IS NO WARRANTY; USE
   * THIS PRODUCT AT YOUR OWN RISK.
   */
  private Component receiver; // The receiver to deliver to.

  // Packet packet;

  final Object content; // object passed to it by initialize statement

  private boolean closed = false;

  String name;

  private Port port;

  Class type;
  
  private Network traceNetwork; // the network this port is visible in (needed for tracing)  

  // Network network;

  /**
   * Create an InitializationConnection: requires a content and a receiver.
   */

  InitializationConnection(final Object cont, final Component newReceiver) {

    content = cont; // store object
    receiver = newReceiver;
  }

  /**
   * The maximum number of packets available in an InitializationConnection
   * must be 1.
   */
  //private int capacity() {
  //  return 1;
  //}

  /**
   * Close Initialization Connection
   */
  public void close() {
    closed = true;
  }

  /**
   * (Re)open Initialization Connection
   */
  void reopen() {
    closed = false;
  }

  public boolean isClosed() {
    return closed;
  }

  /**
   * Invoked to get receiver.
   */

  Component getReceiver() {
    return receiver;
  }

  /**
   * The receive function of an InitializationConection. Returns null after
   * the packet has been delivered (because the Packet is set to null). You
   * get one copy per activation
   * 
   * Warning: the object contained in this packet must not be modified.
   * 
   * See InputPort.receive.
   */
  public Packet receive() {
    Packet p;

    if (!isClosed()) {
      p = new Packet(content, getReceiver());
      getReceiver().network.receives.getAndIncrement();
      getReceiver().mother.traceFuncs(getName() + ": Received: " + p.toString());
      close(); 
    } else {
      p = null;
      // p.setOwner(receiver);
      // content = null;
    }

    return p;
  }

  public String getName() {
    return name;
  }

  /**
   * Invoked to tell us the type of packet content being sent or expected. The
   * receiver's type must be a supertype of content, or the network is
   * ill-formed.
   
   void setType(Class type) {

  	if (type == null)
  		return;

  	if (type == Object.class)
  		return;

  	//
  	 * if (!(type.isAssignableFrom(content.getClass())))
  	 * FlowError.complain("Connection type mismatch");
  	 //
  }
  */

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#setType(java.lang.Class)
   */
  @SuppressWarnings("unused")
  public void setType(final Class tp) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#setName(java.lang.String)
   */
  void setName(final String n) {
    name = n;

  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#getPort()
   */
  public Port getPort() {
    return port;
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#setPort(com.jpmorrsn.fbp.engine.Port)
   */
  public void setPort(final Port p) {
    port = p;
  }
  
  /**
   * Invoked to tell us we have a receiver.
   */

  public void setReceiver(final Component newReceiver) {
    // added for subnet support
    if (receiver == null) {
      // called by Component.network.connect()
      receiver = newReceiver;
      traceNetwork = newReceiver.mother;
    } else {
      // always use the same lock for subnet ports
      newReceiver.goLock = receiver.goLock;
      receiver = newReceiver;
    }
  }

}
