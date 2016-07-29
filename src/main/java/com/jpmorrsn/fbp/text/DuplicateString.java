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

package com.jpmorrsn.fbp.text;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to copy all incoming packets to OUT and DUPLICATE
 */
@ComponentDescription("Copy all incoming packets to outputs OUT and DUPLICATE")
@InPort(value = "IN", type = String.class)
@OutPorts({ @OutPort("OUT"), @OutPort(value = "DUPLICATE", type = String.class) })
public class DuplicateString extends Component {

  
  private InputPort inport;

  private OutputPort outport;

  OutputPort duplicatePort;

  @Override
  protected void execute() {
    Packet p;
    while ((p = inport.receive()) != null) {
      String s = (String) p.getContent();
      String s2 = new String(s);
      Packet duplicate = create(s2);

      outport.send(p);

      duplicatePort.send(duplicate);

    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");
    duplicatePort = openOutput("DUPLICATE");
  }
}
