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
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/** Replicate a stream of Packets to 'n' output streams, where
*  each Packet contains a String.
*  
* Note: this is a specific variant (restricted to Strings) of the general Replicate verb (q.v.)
**/
@ComponentDescription("Replicate stream of String packets to multiple output streams")
@OutPort(value = "OUT", arrayPort = true, description = "Replicated packets", type = String.class)
@InPort(value = "IN", description = "Incoming packets", type = String.class)
public class ReplString extends Component {

  
  private InputPort inport;

  private OutputPort[] outportArray;

  @Override
  protected void execute() {

    int no = outportArray.length;

    Packet p;
    //long count = 0;
    while ((p = inport.receive()) != null) {
      //++count;
      String o = (String) p.getContent();
      drop(p);

      for (int i = 0; i < no; i++) {
        String o2 = new String(o);
        Packet p2 = create(o2);
        outportArray[i].send(p2);

      }
    }
    //network.traceFuncs("Repl complete. " + getName());
  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");

    outportArray = openOutputArray("OUT");

  }
}
