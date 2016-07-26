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
 package com.jpmorrsn.fbp.components;

/***
 *Component copies incoming packets - delayed until trigger received
 */
import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


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
    Packet tp = trigger.receive();
    if (tp == null) {
      return;
    }
    // trigger.close();
    drop(tp);

    System.out.println("got trigger");

    Packet rp = in.receive();
    System.out.println("rp = '" + rp + "'");

    if (rp == null) {
      return;
    }
    // in.close();

    // pass output
    Object o = rp.getContent();
    Packet p = create(o);
    out.send(p);
    drop(rp);
    // }
  }

  @Override
  protected void openPorts() {
    out = openOutput("OUT");
    in = openInput("IN");
    trigger = openInput("TRIGGER");
  }
}
