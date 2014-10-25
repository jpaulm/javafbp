/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.engine;


//import java.util.ArrayList;

public class ConnArray implements InputPort {

  boolean fixedSize;

  String name;

  Class type;
  
  boolean optional;

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#capacity()
   */
  // private int capacity() {
  //  return 0;
  // }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#close()
   */
  public void close() {
    //
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#count()
   */
  // private int count() {    
  //   return 0;
  //}

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#getName()
   */
  public String getName() {
    return name;
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#getReceiver()
   */
  //private Component getReceiver() {    
  //  return null;
  //}

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#receive()
   */
  public Packet receive() {
    return null;
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#setReceiver(com.jpmorrsn.fbp.engine.Component)
   */
  @SuppressWarnings("unused")
  public void setReceiver(final Component comp) {
    //
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#setType(java.lang.Class)
   */
  @SuppressWarnings("unused")
  public void setType(final Class tp) {
    // 
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#setName(java.lang.String)
   */
  @SuppressWarnings("unused")
  private void setName(final String n) {
    // 
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#getPort()
   */
  public Port getPort() {
    return null;
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#setPort(com.jpmorrsn.fbp.engine.Port)
   */
  @SuppressWarnings("unused")
  public void setPort(final Port p) {
    // 
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.InputPort#isClosed()
   */
  public boolean isClosed() {

    return false;
  }

}
