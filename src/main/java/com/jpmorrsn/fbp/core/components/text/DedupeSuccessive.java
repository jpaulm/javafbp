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

package com.jpmorrsn.fbp.core.components.text;


import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InPorts;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutPorts;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/**
 * Prevent successive identical strings.
 * Bob Corrick December 2011
 */
@ComponentDescription("Take text IN and only send OUT where this differs from the previous text.")
@OutPorts({ @OutPort(value = "OUT") })
@InPorts({ @InPort("IN") })
public class DedupeSuccessive extends Component {

  static final String copyright = "...";

  private InputPort inport;

  private OutputPort outport;

  @Override
  protected void execute() {
    String previous = "";
    Packet pIn;
    while ((pIn = inport.receive()) != null) {
      String sIn = (String) pIn.getContent();
      drop(pIn);

      if (!previous.equals(sIn)) {
        Packet pOut = create(sIn);
        outport.send(pOut);
      }
      previous = sIn;
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    outport = openOutput("OUT");
  }
}
