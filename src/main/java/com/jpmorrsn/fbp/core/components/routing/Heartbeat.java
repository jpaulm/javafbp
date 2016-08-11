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

package com.jpmorrsn.fbp.core.components.routing;


import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/** Component to generate packets every 'n' milliseconds, where 'n' is
* specified in an InitializationConnection.
*/
@ComponentDescription("Generates a packet every 'n' milliseconds")
@OutPort("OUT")
@InPort("INTERVAL")
public class Heartbeat extends Component {

  

  private OutputPort outport;

  private InputPort interval;

  @SuppressWarnings("rawtypes")
@Override
  protected void execute() {
    // receive interval in milliseconds
    Packet itvl = interval.receive();
    if (itvl == null) {
      return;
    }
    Long itl = (Long) itvl.getContent();
    long it = itl.longValue();
    drop(itvl);
    String s = " ";

    // when the send returns false, this component closes down

    while (true) {
      Packet p = create(s);
      outport.send(p);
      try {
        sleep(it);
      } catch (InterruptedException e) {
        // do nothing
      }
    }

  }

  @Override
  protected void openPorts() {

    outport = openOutput("OUT");

    interval = openInput("INTERVAL");

  }
}
