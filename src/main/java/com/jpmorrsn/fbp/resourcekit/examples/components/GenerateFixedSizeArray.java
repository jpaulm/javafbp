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

package com.jpmorrsn.fbp.resourcekit.examples.components;


import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/** 
 * Component to generate stream of 'n' packets to each element of an array output port, 
 * where 'n' is specified in an InitializationConnection.
 */
@ComponentDescription("Generates stream of packets under control of a counter")
@OutPort(value = "OUT", fixedSize = true, description = "Generated stream", type = String.class, arrayPort = true /*, optional = true */)
@InPort(value = "COUNT", description = "Count of packets to be generated", type = String.class)
public class GenerateFixedSizeArray extends Component {

 
  OutputPort outPortArray[];

  InputPort count;

  @Override
  protected void openPorts() {
    outPortArray = openOutputArray("OUT", 3);
    count = openInput("COUNT");
  }

  @Override
  protected void execute() {
    Packet ctp = count.receive();
    if (ctp == null) {
      return;
    }
    count.close();

    String cti = (String) ctp.getContent();
    cti = cti.trim();
    int ct = 0;
    try {
      ct = Integer.parseInt(cti);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    drop(ctp);

    int no = outPortArray.length;

    for (int k = 0; k < no; k++) {
      // int k2 = k;
      for (int i = ct; i > 0; i--) {
        String s = String.format("%1$06d", i) + "abcd";

        Packet p = create(s);
        //  if (outPortArray[k].isConnected()) {
        outPortArray[k].send(p);
        //  } else {
        //    drop(p);
        //  }
      }
    }

  }

}
