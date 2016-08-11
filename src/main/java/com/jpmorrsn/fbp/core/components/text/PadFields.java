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


/** Pad fields to given length in a stream of character-separated records
 */
@ComponentDescription("Pass through a character SEParated stream, adding PAD up to LIMITS of field lengths")
@OutPorts({ @OutPort(value = "OUT") })
@InPorts({ @InPort("IN"), @InPort(value = "LIMITS"), @InPort(value = "PAD"), @InPort("SEP") })
public class PadFields extends Component {

 
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
