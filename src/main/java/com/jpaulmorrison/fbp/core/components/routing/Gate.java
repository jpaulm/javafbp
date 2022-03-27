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
import com.jpaulmorrison.fbp.core.engine.InPorts;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;

/***
 *Component copies incoming packets - delayed until trigger received
 */

@ComponentDescription("Copies incoming packets - delayed until trigger received")
@OutPort("OUT")
@InPorts({ @InPort("IN"), @InPort("TRIGGER") })
public class Gate extends Component {

  

  private InputPort in;

  private InputPort trigger;

  private OutputPort out;

  @Override
  protected void execute() {
    // receive trigger
    Packet<?> tp = trigger.receive();
    if (tp == null) {
      return;
    }
    trigger.close();
    drop(tp);

    //System.out.println("got trigger");

    Packet<?> rp;
    while ((rp = in.receive()) != null) {
        out.send(rp);
    }    

  }

  @Override
  protected void openPorts() {
    out = openOutput("OUT");
    in = openInput("IN");
    trigger = openInput("TRIGGER");
  }
}
