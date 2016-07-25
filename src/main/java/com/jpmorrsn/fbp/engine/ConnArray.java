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
package com.jpmorrsn.fbp.engine;


/**
 * This class is an internal control block used while processing a component's annotations; 
 * it is also used for checking 'connect' invocations 
 *   
 */

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
