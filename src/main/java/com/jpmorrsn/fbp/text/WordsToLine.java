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
 * Provide measured lines from a stream of words
 * Bob Corrick December 2011
 */
@ComponentDescription("Take words IN and deliver OUT a line no longer than MEASURE characters")
@OutPorts({ @OutPort(value = "OUT") })
@InPorts({ @InPort("IN"), @InPort("MEASURE") })
public class WordsToLine extends Component {

  static final String copyright = "...";

  private InputPort inport;

  InputPort mport;

  private OutputPort outport;

  @Override
  protected void execute() {
    // Get measure
    Packet pMeas = mport.receive();
    if (pMeas == null) {
      return;
    }
    mport.close();
    String sMeasure = ((String) pMeas.getContent()).trim();
    drop(pMeas);

    // Interpret measure
    int measure = 0;
    try {
      measure = Integer.parseInt(sMeasure);
    } catch (NumberFormatException e) {
      System.err.println("Value " + sMeasure + " cannot be interpreted as a number");
      e.printStackTrace();
    }

    String line = "";
    Packet pIn;
    while ((pIn = inport.receive()) != null) {
      String w = ((String) pIn.getContent()).trim();
      drop(pIn);

      if (line.length() + 1 + w.length() > measure) {
        Packet pOut = create(line);
        outport.send(pOut);
        line = w;
      } else {
        if (line.length() > 0) {
          line += " ";
        }
        line += w;
      }
    }
    if (line.length() > 0) {
      Packet pEnd = create(line);
      outport.send(pEnd);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    mport = openInput("MEASURE");
    outport = openOutput("OUT");
  }
}
