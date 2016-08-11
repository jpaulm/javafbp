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


/**
 * This class is used within components to declare instance variables
 * that hold input ports.  Such instance variables should be assigned
 * within the <code>openPorts</code> routine of the component and never
 * changed thereafter.  Packets can be received, and the status of the
 * port manipulated, using the API specified by this interface.
 * <p> InputPort is not a class, but an interface which is implemented
 * either by a Connection object or by an InitializationConnection object. 
 **/

public interface InputPort {

  
  /**
   * Close Connection 
    */
  void close();

  boolean isClosed();
  
  
  /**
   * Receive the next available packet from this InputPort.
   * The thread is suspended if no packets are currently available.
   * At the end of input (when all upstream threads have closed their
   * connected OutputPorts), <code>null</code> is returned.
   * @return next packet, <code>null</code> if none
   **/

  public Packet receive();

  /**
   * Specify the type of packet content that will be accepted from this
   * InputPort.  Specifying <code>null</code> is equivalent to specifying
   * <code>Object.class</code> -- in other words, any packet content is
   * acceptable.
   * @param type the class of acceptable packet content
    
  */
  public void setType(Class type);

  /**
   * @return string containing port name
   */
  String getName();

  /**
   * @return Port object
   */
  Port getPort();

  /**
   * @param port
   */
  void setPort(Port port);
  void setReceiver(Component comp);

}
