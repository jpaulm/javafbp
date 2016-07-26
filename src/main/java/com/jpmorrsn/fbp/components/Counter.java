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
package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.MustRun;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Component to count a stream of packets, and output the result on the COUNT port.
*/
@ComponentDescription("Counts stream of packets and outputs result")
@InPort(value = "IN", description = "Incoming stream", type = String.class)
@OutPorts({ @OutPort(value = "OUT", description = "Stream being passed through", type = String.class, optional = true),
    @OutPort(value = "COUNT", description = "Count packet to be output", type = String.class) })
@MustRun
public class Counter extends Component {

  
  private OutputPort countPort, outPort;

  private InputPort inPort;

  @Override
  protected void execute() {
    int count = 0;

    Packet p;
    while ((p = inPort.receive()) != null) {
      count++;
      if (outPort.isConnected()) {
        outPort.send(p);
      } else {
        drop(p);
      }
    }
    Packet ctp = create(Integer.toString(count));
    countPort.send(ctp);
  }

  @Override
  protected void openPorts() {
    inPort = openInput("IN");
    outPort = openOutput("OUT");
    countPort = openOutput("COUNT");
  }
}
