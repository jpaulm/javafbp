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


/** Provide maximum field lengths from a stream of character-separated records
 */
@ComponentDescription("Pass through a CSV stream, also output LIMITS of field lengths as CSV")
@OutPorts({ @OutPort(value = "OUT"), @OutPort(value = "LIMITS") })
@InPorts({ @InPort("IN"), @InPort("SEP") })
public class FieldLimits extends Component {

  static final String copyright = "Copyright 2007, 2010, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  InputPort inport, sepport;

  private OutputPort limitport, outport;

  @Override
  protected void execute() {
    // Default separator
    String sep = ",";

    // Get separator from SEP IIP
    Packet pSep = sepport.receive();
    if (pSep != null) {
      sepport.close();
      sep = (String) pSep.getContent();
      drop(pSep);
    }

    // Pass through IN to OUT, keeping greatest field lengths
    int[] nLimits = null;
    Packet p;
    while ((p = inport.receive()) != null) {
      String o = (String) p.getContent();

      // Get fields for this record
      String[] fields = o.split(sep);

      // Prepare limits data
      if (nLimits == null) {
        nLimits = new int[fields.length];
      }
      // Remember greatest field length
      for (int i = 0; i < nLimits.length; i++) {
        if (fields[i].length() > nLimits[i]) {
          nLimits[i] = fields[i].length();
        }
      }
      // Pass through
      outport.send(p);
    }
    if (nLimits != null) {
      // Send LIMITS as single CSV record
      String sLimits = "";
      for (int j = 0; j < nLimits.length; j++) {
        sLimits = sLimits + nLimits[j];
        if (j < nLimits.length - 1) {
          sLimits += sep;
        }
      }
      Packet pLimits = create(sLimits);
      limitport.send(pLimits);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    sepport = openInput("SEP");
    outport = openOutput("OUT");
    limitport = openOutput("LIMITS");
  }
}
