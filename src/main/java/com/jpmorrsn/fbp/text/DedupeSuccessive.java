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
