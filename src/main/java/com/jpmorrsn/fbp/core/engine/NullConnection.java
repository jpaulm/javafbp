/*
 * JavaFBP - A Java Implementation of Flow-Based Programming (FBP)
 * Copyright (C) 2009, 2016 J. Paul Morrison
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, see the GNU Library General Public License v3
 * at https://www.gnu.org/licenses/lgpl-3.0.en.html for more details.
 */

package com.jpmorrsn.fbp.core.engine;


/** This is used in a network definition when a port is not connected
*/

final class NullConnection implements InputPort {

 ;
  NullConnection() {
    // do nothing
  }

  /** Return capacity of 0
  */

  private String name;

  private Port port;

  protected Component receiver;

  Class type;
  
  boolean optional;

  //private int capacity() {
  //  return 0;
  //}

  public void close() {
    // do nothing
  }

  //private int count() {
  //  return 0;
  //}

  public String getName() {
    return name;
  }

  //private Component getReceiver() { //added for subnet support 
  //  return receiver;
  //}

  public boolean isClosed() {
    return true;
  }

  public Packet receive() {
    return null;
  }

  public void setReceiver(final Component newReceiver) { // added for subnet support 
    receiver = newReceiver;
  }

  @SuppressWarnings("unused")
  public void setType(final Class tp) {
    //
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.core.engine.InputPort#setName(java.lang.String)
   */
  void setName(final String n) {
    name = n;
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.core.engine.InputPort#getPort()
   */
  public Port getPort() {
    return port;
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.core.engine.InputPort#setPort(com.jpmorrsn.fbp.core.engine.Port)
   */
  public void setPort(final Port p) {
    port = p;
  }

}
