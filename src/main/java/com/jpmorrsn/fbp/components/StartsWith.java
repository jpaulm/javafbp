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
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Select packets starting with specified string.  
 */
@ComponentDescription("Select packets starting with specified string")
@OutPorts({ @OutPort("ACC"), @OutPort("REJ") })
@InPorts({ @InPort("IN"), @InPort("TEST") })
public class StartsWith extends Component {

  
  private InputPort inport, testport;

  private OutputPort accport, rejport;

  @Override
  protected void execute() {

    Packet testPkt = testport.receive();
    if (testPkt == null) {
      return;
    }
    String testStr = (String) testPkt.getContent();
    testport.close();
    drop(testPkt);

    Packet p = inport.receive();
    while (p != null) {
      String content = (String) p.getContent();
      if (content.startsWith(testStr)) {
        accport.send(p);
      } else {
        rejport.send(p);
      }
      p = inport.receive();
    }
  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");
    testport = openInput("TEST");

    accport = openOutput("ACC");
    rejport = openOutput("REJ");

  }
}
