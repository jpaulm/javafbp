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

package com.jpaulmorrison.fbp.core.components.routing;


import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/** Component to split an input stream into multiple output streams,
* where the first 30 packets go to the first output port, the next 30 go
* to the second and so on.  Each output stream is closed before data starts
* being sent to the next.  This component is used for testing deadlock behaviour.
*/
@ComponentDescription("Splits a stream into multiple output streams")
@OutPort(value = "OUT", arrayPort = true)
@InPort("IN")
public class Splitter1 extends Component {
  
  private InputPort inport;

  private OutputPort[] outportArray;

  @Override
  protected void execute() {

    int no = outportArray.length;
    int e = 0;
    int i = 0;
    Packet p;

    while ((p = inport.receive()) != null) {

      outportArray[e].send(p);
      if (i == 30) {
        if (e < no - 1) { // if last output stream, don't close it
          outportArray[e].close();
          ++e;
        }
        i = 0;
      } else {
        i++;
      }

    }
    while (e < no) {
      outportArray[e].close();
      e++;
    }

  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");

    outportArray = openOutputArray("OUT");

  }
}
