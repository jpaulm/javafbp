package com.jpmorrsn.fbp.engine;


// import java.util.*;

import java.math.BigInteger;


/**
 * This class implements buffering between Component threads. One is created
 * behind the scenes whenever two ports are connected. This class was founded on
 * Doug Lea's BoundedBufferVST class from his book _Concurrent Programming in
 * Java_, page 100.
 */

public class Connection implements InputPort {

  /***************************************************************************
   * Copyright 2007, 2014, J. Paul Morrison. At your option, you may copy,
   * distribute, or make derivative works under the terms of the Clarified
   * Artistic License, based on the Everything Development Company's Artistic
   * License. A document describing this License may be found at
   * http://www.jpaulmorrison.com/fbp/artistic2.htm. THERE IS NO WARRANTY; USE
   * THIS PRODUCT AT YOUR OWN RISK.
   */
  final static long serialVersionUID = 817911632652898426L;

  private final int capacity;

  //Packet packet = null;

  // The packets currently in transit.
  private final Packet[] array;

  // Index into array where the next packet sent should go.
  private int sendPtr = 0;

  // Index into array where the next packet received should come from.
  private int receivePtr = 0;

  // Number of slots in array currently in use.
  volatile int usedSlots = 0;

  // Number of senders who have called setSender() but are not closed.
  volatile int senderCount = 0;

  // The unique receiver. We need to activate() it whenever
  // packets arrive, or if connection is closed and receiver is MustRun.
  Component receiver = null;

  // The component who happens to be sending on a send()
  private Component sender;

  // LinkedList<Component> senders = null;

  // The list of types (Class objects) that senders have declared.
  // Vector senderTypes;
  // This network
  // Network network; // added for subnet support

  // The type that the receiver has declared.
  //private Class receiverType;

  private Network traceNetwork; // the network this port is visible in (needed for tracing)  

  boolean IPCount;

  private String name; // receiver name + '.' + connection name  - change to "name"

  private Port port;

  Class type;
  
  boolean optional;

  private boolean dropOldest;

  /**
   * Constructor: make a new connection of a given size
   */
  Connection(final int size) {
    array = new Packet[size];
    capacity = size;
  }

  /**
   * Invoked to tell us we have a(nother) sender.
   */
  synchronized void bumpSenderCount(/* final OutputPort op */) {
    senderCount++;

  }

  /**
   * count = size
   */
  synchronized int count() {
    return usedSlots;
  }

  /**
   * Throws an error if the input object types and output object types do not
   * match (This needs some more thinking about...)
   
  public void checkTypes() {

    return;
    //
     * if (senderTypes == null) return; if (receiverType == Object.class)
     * return; Enumeration types = senderTypes.elements(); while
     * (types.hasMoreElements()) { Class senderType =
     * (Class)(types.nextElement()); if
     * (!(receiverType.isAssignableFrom(senderType)))
     * FlowError.complain("Connection type mismatch"); }
     * 
     //

  }
  */
  /**
   * The close input connection function - this will prevent any more packets
   * from being received from this connection. Need to interrupt sender(s).
   */

  public synchronized void close() {
    traceFuncs("Close input connection");

    if (isClosed()) {
      return;
    }
    senderCount = 0; // set sender count to zero
    if (usedSlots > 0) {
      traceFuncs(usedSlots + " packets on input connection lost");
    }

    notifyAll(); // wakes up any senders waiting for slots

  }

  /**
   * Indicate one sending Component closed
   */
  synchronized void indicateOneSenderClosed() {
    try {
      getReceiver().mother.traceLocks("indicate1senderclosed - lock " + getReceiver().getName());
      //getReceiver().goLock.lockInterruptibly();
      getReceiver().goLock.lock();

      // synchronized (this) {
      if (!isClosed()) {
        --senderCount;

        if (isDrained()) { // means closed AND empty
          // closed means senderCount == 0
          if (getReceiver().status == Component.StatusValues.DORMANT
              || getReceiver().status == Component.StatusValues.NOT_STARTED) {
            getReceiver().activate();
          } else {
            notifyAll();
          }
        }
      }
      //   }

    //} catch (InterruptedException e) {
    //  return;
    } finally {
      getReceiver().goLock.unlock();
      getReceiver().mother.traceLocks("indicate1senderclosed - unlock " + getReceiver().getName());
    }
  }

  /**
   * Invoked to get receiver.
   */

  Component getReceiver() { // added for subnet support
    return receiver;
  }

  public String getName() {
    return name;
  }

  /**
   * Returns true if this connection is closed (not necessarily drained).
   */

  public synchronized boolean isClosed() {
    return senderCount == 0;
  }

  /**
   * Returns true if this connection is drained (closed and empty).
   */

  private synchronized boolean isDrained() {
    return isClosed() && isEmpty();
  }

  /**
   * Returns true if this connection is empty
   */

  private synchronized boolean isEmpty() {
    return usedSlots == 0;
  }

  /**
   * Returns true if this connection is full.
   */

  private synchronized boolean isFull() {
    return usedSlots == capacity;
  }

  /**
   * The receive function. See InputPort.receive.
   */

  @SuppressWarnings("unchecked")
  public synchronized Packet receive() {

    traceFuncs("Receiving:");

    //receiver.currentConnection = this;
    if (isDrained()) {
      traceFuncs("Recv/close");
      return null;
    }
    getReceiver().network.receives.getAndIncrement();
    while (isEmpty()) {

      getReceiver().status = Component.StatusValues.SUSP_RECV;
      getReceiver().curConn = this;
      getReceiver().mother.traceFuncs(getReceiver().getName() + ": Recv/susp");

      try {
        wait();
      } catch (InterruptedException e) {
        // throw new ThreadDeath();
        close();
        FlowError.complain(getReceiver().getName() + ": Interrupted");
        // unreachable
        return null;

      }

      getReceiver().status = Component.StatusValues.ACTIVE;
      getReceiver().mother.traceFuncs(getReceiver().getName() + ": Recv/resume ");

      if (isDrained()) {
        getReceiver().mother.traceFuncs(getName() + ": Receive drained ");
        return null;
      }

    }

    if (isDrained()) {
      traceFuncs(": Receive drained ");
      return null;
    }
    //if (isFull()) {
    if (usedSlots == capacity) {
      notifyAll(); // notify components waiting to send
    }
    Packet packet = array[receivePtr];
    array[receivePtr] = null;
    if (capacity == (receivePtr = receivePtr + 1)) {
      receivePtr = 0;
    }
    //notifyAll(); // only needed if it was full
    usedSlots--;

    packet.setOwner(getReceiver());

    if (null == packet.getContent()) {
      traceFuncs("Received null packet");
    } else {
      traceFuncs("Received: " + packet.toString());

      Class c = packet.getContent().getClass();
      if (!type.isAssignableFrom(c)) {
        FlowError.complain(getName() + " received packet containing " + c.getSimpleName() + " - "
            + type.getSimpleName() + " is required");
      }
    }

    if (IPCount) {

      BigInteger bi = getReceiver().network.getIPCounts().get(getFullName());
      BigInteger bi2;
      if (bi == null) {
        bi2 = BigInteger.valueOf(0);
      } else {
        bi2 = bi.add(BigInteger.valueOf(1));
      }
      getReceiver().network.getIPCounts().put(getFullName(), bi2);
    }

    getReceiver().network.active = true;
    return packet;
  }

  private String getFullName() {
    String s = getName();
    Network m = getReceiver().mother;
    while (true) {
      s = m.getName() + "." + s;
      if (!(m instanceof SubNet)) {
        break;
      }
      m = m.mother;
    }
    return s;
  }

  /**
   * The send function. See OutputPort.send.
   */

  @SuppressWarnings("unchecked")
  synchronized boolean send(final Packet packet, final OutputPort op) {

    sender = op.sender;

    Class c1 = op.type;
    Class c2 = packet.getContent().getClass();
    if (c1 != null && !c1.isAssignableFrom(c2)) {
      FlowError.complain(getName() + " trying to send packet containing " + c2.getSimpleName() + " - should be "
          + c1.getSimpleName());
    }

    if (isClosed()) {
      sender.mother.traceFuncs(sender.getName() + ": Send closed ");
      return false;
    }
    //sender.currentConnection = this;
    //sender.mother.traceFuncs("Sending: " + packet.toString());
    while (isFull()) {
      if (dropOldest) {
        //Packet p = array[receivePtr];
        array[receivePtr] = null;
        if (capacity == (receivePtr = receivePtr + 1)) {
          receivePtr = 0;
        }
        //sender.drop(p);
        sender.network.dropOlds.getAndIncrement();
        sender.mother.traceFuncs(sender.getName() + ": DropOld ");
        usedSlots--;
      } else {
        sender.curOutPort = op;
        sender.status = Component.StatusValues.SUSP_SEND;
        sender.mother.traceFuncs(sender.getName() + ": Send/susp ");

        try {
          wait();
        } catch (InterruptedException e) {
          // throw new ThreadDeath();
          indicateOneSenderClosed();
          FlowError.complain(sender.getName() + ": interrupted");
          // unreachable code
          return false;
        }

        sender = op.sender;
        sender.status = Component.StatusValues.ACTIVE;
        sender.mother.traceFuncs(sender.getName() + ": Send/resume");
      }
    }

    if (isClosed()) {
      sender.mother.traceFuncs(sender.getName() + ": Send/close");
      return false;
    }
    sender.mother.traceLocks("send - lock " + getReceiver().getName());
    try {
      getReceiver().goLock.lockInterruptibly();
      packet.clearOwner();
      array[sendPtr] = packet;
      if (capacity == (sendPtr = sendPtr + 1)) {
        sendPtr = 0;
      }
      usedSlots++; // move this to here
      if (getReceiver().getStatus() == Component.StatusValues.DORMANT
          || getReceiver().getStatus() == Component.StatusValues.NOT_STARTED) {
        getReceiver().activate(); // start or wake up if necessary
      } else {
        notifyAll(); // notify receiver
        // other components waiting to send to this connection may also get
        // notified,
        // but this is handled by while statement 
      }

      op.sender.status = Component.StatusValues.ACTIVE;
      // Component.network.GenTraceLine("Send OK: " + op.sender.getName());
      sender.network.active = true;
      //sender = null;

    } catch (InterruptedException ex) {
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } finally {
      getReceiver().goLock.unlock();
      getReceiver().mother.traceLocks("send - unlock " + getReceiver().getName());
    }
    sender.network.sends.getAndIncrement();
    sender = null;
    return true;
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

  /**
   * Invoked to tell us the type of packet content being sent or expected. The
   * receiver's type must be a supertype of every sender's type, or the
   * Component.network is ill-formed.
    
  */
  public void setType(final Class tp) {

    if (tp == null) {
      return;
    }

    //receiverType = tp;

  }

  /**
   * Issues tracing messages belonging to this input port. 
   */
  private void traceFuncs(final String msg) {
    traceNetwork.traceFuncs(name + ": " + msg);
  }

  public int getCapacity() {
    return capacity;
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

  public void setDropOldest() {
    dropOldest = true;
  }


}