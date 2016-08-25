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
package com.jpaulmorrison.fbp.resourcekit.examples.components;


import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;

/**
 * Component to test chaining API calls
 *
 */

@OutPort("OUT")
public class ChainOps extends Component {

  
  private OutputPort outputPort;

  @Override
  protected void openPorts() {
    outputPort = openOutput("OUT");
  }

  @Override
  protected void execute() {
    Packet a = create("a");
    Packet b = create("b");
    Packet c = create("c");
    Packet d = create("d");

    attach(a, "Chain1", b);
    attach(b, "Chain1", c);
    attach(b, "Chain1", d);
    detach(b, "Chain1", c);

    detach(a, "Chain1", b);
    outputPort.send(b);
    outputPort.send(a);

    outputPort.send(c);
  }
}
