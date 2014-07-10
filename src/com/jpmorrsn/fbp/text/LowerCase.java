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
 * Convert to lower case
 * Bob Corrick December 2011
 */
@ComponentDescription("Convert text IN to lower case and send OUT")
@OutPorts({ @OutPort(value = "OUT") })
@InPorts({ @InPort("IN") })
public class LowerCase extends Component {

  static final String copyright = "...";

  private InputPort inport;

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet pIn;
    while ((pIn = inport.receive()) != null) {
      String sIn = (String) pIn.getContent();
      drop(pIn);
      String lower = sIn.toLowerCase();

      Packet pOut = create(lower);
      outport.send(pOut);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    outport = openOutput("OUT");
  }
}
