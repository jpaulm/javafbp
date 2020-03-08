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
package com.jpaulmorrison.fbp.core.components.misc;


import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.MustRun;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/**
 * Component to write data to the console, using a stream of packets. It is
 * specified as "must run" so that the output file will be cleared even if no
 * data packets are input.
 */
@ComponentDescription("Write stream of packets to console")
@InPort(value = "IN", description = "Packets to be displayed", type = String.class)
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
@MustRun
public class WriteToConsole extends Component {

  
  private InputPort inport;

  private final double _timeout = 5.0; // 5 secs

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet<?> p;

    while ((p = inport.receive()) != null) {
      longWaitStart(_timeout);
      // sleep(5000L); //force timeout - testing only
      if (p.getType() == Packet.OPEN) {
        System.out.println("===> Open Bracket");
      } else if (p.getType() == Packet.CLOSE) {
        System.out.println("===> Close Bracket");
      } else {
        System.out.println((String) p.getContent());
      }
      longWaitEnd();
      if (outport.isConnected()) {
        outport.send(p);
      } else {
        drop(p);
      }
    }

  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");

  }
}
