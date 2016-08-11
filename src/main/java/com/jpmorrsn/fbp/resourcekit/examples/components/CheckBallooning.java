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
import com.jpmorrsn.fbp.core.engine.FlowError;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.MustRun;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/**
 * Component to write data to the console, using a stream of packets. It is
 * specified as "must run" so that the output file will be cleared even if no
 * data packets are input.  It also checks that upstream Balloon is working.
 */
@ComponentDescription("Check ballooning logic")
@InPort(value = "IN", description = "Packets to be checked", type = String.class)
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
@MustRun
public class CheckBallooning extends Component {

  
  private InputPort inport;

  double _timeout = 5.0; // 5 secs

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet p;
    int num = 200;

    while ((p = inport.receive()) != null) {
      longWaitStart(_timeout);
      if (num > 100 && num < 150) {
        try {
          sleep(120);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } // force ballooning
      }
      String s = (String) p.getContent();
      String t = String.format("%1$06d", num);
      if (!s.startsWith(t)) {
        FlowError.complain("Out of sequence");
        break;
      }

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
      num--;
    }

  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");

  }
}
