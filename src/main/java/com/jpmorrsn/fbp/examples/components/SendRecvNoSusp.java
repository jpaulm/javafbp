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


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.jpmorrsn.fbp.engine.SelfStarting;


/** Component to generate a stream of 'n' packets, where 'n' is
* specified in an InitializationConnection.
*/
@ComponentDescription("Testing send/recv with no suspension")
@OutPort(value = "OUT")
@InPorts({ @InPort(value = "COUNT"), @InPort(value = "IN") })
@SelfStarting
public class SendRecvNoSusp extends Component {


  private OutputPort outport;

  InputPort count, inport;

  @Override
  protected void execute() {
    Packet ctp = count.receive();
    if (ctp == null) {
      return;
    }
    count.close();

    String cti = (String) ctp.getContent();
    cti = cti.trim();
    int ct = 0;
    try {
      ct = Integer.parseInt(cti);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    drop(ctp);
    Packet p = create("testing");

    for (int i = ct; i > 0; i--) {
      outport.send(p);
      p = inport.receive();
    }
    drop(p);
  }

  @Override
  protected void openPorts() {
    outport = openOutput("OUT");
    count = openInput("COUNT");
    inport = openInput("IN");
  }
}
