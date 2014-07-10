/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.text;


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
