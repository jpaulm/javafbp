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


/** Provide maximum field lengths from a stream of character-separated records
 */
@ComponentDescription("Pass through a CSV stream, also output LIMITS of field lengths as CSV")
@OutPorts({ @OutPort(value = "OUT"), @OutPort(value = "LIMITS") })
@InPorts({ @InPort("IN"), @InPort("SEP") })
public class FieldLimits extends Component {

 
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
