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


/**
 * Component to pass packets through with random delay (0 - 2 secs.).
 */
@ComponentDescription("Delays incoming packets for random amount of time (0 - 2 secs.)")
@OutPort(value = "OUT", description = "Packets being output")
@InPort(value = "IN", description = "Incoming packets")
public class RandomDelay extends Component {

  

  private InputPort inport;

  private OutputPort outport;

  @Override
  protected void execute() {

    Packet p;
    while ((p = inport.receive()) != null) {
      long r = new Double(Math.random()).longValue();
      long rl = 2000 * r; // range is from 0 to 2 secs
      try {
        Thread.sleep(rl);
      } catch (InterruptedException e) {
        // do nothing
      }
      outport.send(p);
    }
  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");

    outport = openOutput("OUT");

  }
}
