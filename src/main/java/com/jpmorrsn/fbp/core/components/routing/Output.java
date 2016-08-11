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
package com.jpmorrsn.fbp.core.components.routing;


import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.Packet;
/**
 *  
 *
 * This component displays the content of incoming IPs; open and close brackets 
 *  are shown with their level numbers - the first open bracket is shown as OPEN(1)
 *  
 *  @author Sven Steinseifer - 2010
 */

@InPort("IN")
public class Output extends Component {

  private InputPort inputPort;

  @Override
  protected void execute() {
    Packet p;
    int level = 1;
    while ((p = inputPort.receive()) != null) {
      switch (p.getType()) {
        case Packet.OPEN:
          System.out.println("OPEN(" + level + ")");
          level++;
          break;
        case Packet.CLOSE:
          level--;
          System.out.println("CLOSE(" + level + ")");
          break;
        default:
          System.out.println(p.getContent());
      }
      drop(p);
    }
  }

  @Override
  protected void openPorts() {
    inputPort = openInput("IN");
  }

}
