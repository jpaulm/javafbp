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

package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InPorts;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/**
 * Component to interpret incoming command.
 */
@ComponentDescription("Interpret incoming packet to create output")
@OutPort(value = "OUT", description = "Seek and count to be read from file", type = String.class)
@InPorts({ @InPort(value = "IN", description = "Packets to be interpreted", type = String.class) })
public class Interpret extends Component {

  

  private InputPort inport;

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet c;
    String cmd = "";
    if ((c = inport.receive()) != null) {
      cmd = (String) c.getContent();
      drop(c); // packets must be disposed of (or passed on)
    }

    // Interpret first character as option from user
    String result = "";
    char option = 'X'; // default eXit
    if (cmd != null && cmd.trim().length() > 0) {
      option = cmd.trim().charAt(0);
    }
    if (option == 'X') {
      outport.close(); // eXit
      return;
    } else if (option == 'F') {
      result = "0,4"; // go to First page
    }

    // Send result
    Packet p;
    p = create(result);
    System.out.println("Interpret: OUT: " + result);
    outport.send(p);
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    outport = openOutput("OUT");
  }
}
