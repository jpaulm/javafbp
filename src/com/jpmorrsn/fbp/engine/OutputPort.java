package com.jpmorrsn.fbp.engine;


/**
 * This class is used within components to declare instance variables that hold
 * output ports. Such instance variables should be assigned within the
 * <code>openPorts</code> routine of the component and never changed
 * thereafter. Packets can be sent, and the status of the port manipulated,
 * using the API specified by this interface. Unlike InputPort, this is a real
 * class, the difference being that multiple InputPorts can feed a single
 * Connection, whereas a Connection can not "feed" more than one InputPort.
 */

public class OutputPort {

  /***************************************************************************
   * Copyright 2007, 2012, J. Paul Morrison. At your option, you may copy,
   * distribute, or make derivative works under the terms of the Clarified
   * Artistic License, based on the Everything Development Company's Artistic
   * License. A document describing this License may be found at
   * http://www.jpaulmorrison.com/fbp/artistic2.htm. THERE IS NO WARRANTY; USE
   * THIS PRODUCT AT YOUR OWN RISK.
   */
  Connection cnxt = null; // downstream connection

  boolean isClosed = false;

  String name;

  boolean optional;

  Class type = null;

  Component sender = null; // component sending to this Output Port

  private Network traceNetwork; // the network this port is visible in (needed for tracing)

  private int arrayType = 0; //0 = not array; 1 = array, not fixed size; 2 = fixed size array 

  String fullName; // sender name + "." + port name 

  Port port;

  /**
   * Close this OutputPort. This is a signal that no further packets will be
   * sent via this OutputPort. Since more than one OutputPort may feed a given
   * Connection, this does not necessarily close the Connection.
   */

  public void close() {
    traceFuncs("Closing");
    if (isConnected() && !isClosed) {
      isClosed = true;
      synchronized (cnxt) {
        if (!cnxt.isClosed()) {
          cnxt.indicateOneSenderClosed(); // indicate that one sender has
          // terminated
        }
      }
    }
    traceFuncs("Close finished");
  }

  /**
   * This method returns the downstream Packet count for a given OutputPort It
   * is normally only used by components that do load balancing.
   * 
   * @return int
   */
  public int downstreamCount() {
    return cnxt.count();
  }

  /**
   * This method returns true if the output port is connected
   * 
   * @return boolean
   */
  public boolean isConnected() {
    return cnxt != null;
  }

  /**
   * This method returns true if the output port is closed
   * 
   * @return boolean
   */
  public boolean isClosed() {
    return isClosed;
  }

  /**
   * Send a packet to this Port. The thread is suspended if no capacity is
   * currently available. If the port or connection has been closed,
   * <code>false</code> is returned; otherwise, <code>true</code> is
   * returned.
   * <p>
   * Do not reference the packet after sending - someone else may be modifying
   * it!
   * 
   * @param packet
   *            packet to send
   * @return true if successful
   */

  public// The send function.
  void send(final Packet packet) {

    // Thread t = Thread.currentThread();
    // if (t != packet.owner)
    if (packet == null) {
      FlowError.complain("Null packet reference in 'send' method call: " + getSender().getName());
    }
    boolean res = false;
    if (cnxt == null) {
      res = optional;
    } else {
      if (sender != packet.owner) {
        FlowError.complain("Packet being sent not owned by current component: " + getSender().getName());
      }

      if (isClosed) {
        traceFuncs("Send/closed: " + packet.toString());
        res = false;
      }

      traceFuncs("Sending: " + packet.toString());
      res = cnxt.send(packet, this); // fire up send method on connection
    }
    if (!res) {
      FlowError.complain("Could not deliver packet to: " + getName());
    }
    traceFuncs("Sent OK");
    return;
  }

  String getName() {
    return sender.getName() + "." + name;
  }

  /**
   * Get sender reference
   */
  Component getSender() {
    return sender;
  }

  /**
   * Set sender reference
   */
  void setSender(final Component c) {
    if (sender == null) {
      // first call - c must be the component this port actually belongs to
      traceNetwork = c.mother;
    }
    sender = c;
  }

  Connection getConnection() {
    return cnxt;
  }

  /**
   * Invoked to tell us the type of packet content being sent or expected. The
   * receiver's type must be a supertype of every sender's type, or the
   * network is ill-formed.
     

   void setType(final Class<?> type) {

    //
     * if (type == null) return;
     * 
     * if (cnxt.senderTypes == null) cnxt.senderTypes = new Vector();
     * cnxt.senderTypes.addElement(type);
     * 
     //

  }
  */

  /**
   * Issues tracing messages belonging to this output port. 
   */
  private void traceFuncs(final String msg) {
    traceNetwork.traceFuncs(fullName + ": " + msg);
  }

  protected int getArrayType() { //during initialization: 1 = not array; 2 = array, not fixed size; 3 = fixed size array
    // 0 means normal (execution) function  
    return arrayType;
  }

  protected void setArrayType(final int i) {
    arrayType = i;
  }
}
