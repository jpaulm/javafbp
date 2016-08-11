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
 * Provide words from a stream of space-separated records
 * Bob Corrick December 2011
 */
@ComponentDescription("Take space-separated words in a record IN and deliver individual words OUT")
@OutPorts({ @OutPort(value = "OUT") })
@InPorts({ @InPort("IN") })
public class LineToWords extends Component {

  static final String copyright = "...";

  private InputPort inport;

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet pIn;
    while ((pIn = inport.receive()) != null) {
      String w = (String) pIn.getContent();
      drop(pIn);

      // Get words for this record
      String[] words = w.split(" ");

      // Send words as individual packets
      for (String word : words) {
        Packet pOut = create(word);
        outport.send(pOut);
      }
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    outport = openOutput("OUT");
  }
}
