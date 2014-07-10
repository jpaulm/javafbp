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


/** Pad fields to given length in a stream of character-separated records
 */
@ComponentDescription("Pass through a character SEParated stream, adding PAD up to LIMITS of field lengths")
@OutPorts({ @OutPort(value = "OUT") })
@InPorts({ @InPort("IN"), @InPort(value = "LIMITS"), @InPort(value = "PAD"), @InPort("SEP") })
public class PadFields extends Component {

  static final String copyright = "Copyright 2007, 2010, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  InputPort inport, limitport, padport, sepport;

  private OutputPort outport;

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

    // Default pad
    String pad = " ";

    // get pad from PAD IIP
    Packet pPad = padport.receive();
    if (pPad != null) {
      padport.close();
      pad = (String) pPad.getContent();
      drop(pPad);
    }

    /// get LIMITS
    int[] nLimits = null;
    Packet pLimit = limitport.receive();
    if (pLimit != null) {
      limitport.close();
      String[] sLimits = ((String) pLimit.getContent()).split(sep);
      nLimits = new int[sLimits.length];
      for (int j = 0; j < nLimits.length; j++) {
        nLimits[j] = 0;
        try {
          nLimits[j] = Integer.parseInt(sLimits[j]);
        } catch (NumberFormatException e) {
          e.printStackTrace();
        }
      }
      drop(pLimit);
    }

    // Pass through IN to OUT, PADding fields to LIMITS
    Packet pIn;
    while ((pIn = inport.receive()) != null) {
      String in = (String) pIn.getContent();

      // Get fields for this record
      String[] fields = in.split(sep);

      // Pad each field to limit
      for (int i = 0; i < nLimits.length; i++) {
        while (fields[i].length() < nLimits[i]) {
          fields[i] += pad;
        }
      }
      String sPadded = "";
      for (int k = 0; k < fields.length; k++) {
        sPadded += fields[k];
        if (k < fields.length - 1) {
          sPadded += sep;
        }
      }

      // Pass through
      Packet pOut = create(sPadded);
      outport.send(pOut);
      drop(pIn);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    limitport = openInput("LIMITS");
    padport = openInput("PAD");
    sepport = openInput("SEP");
    outport = openOutput("OUT");
  }
}
