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
 * Component to inject an IIP String as an IP
 */
@ComponentDescription("Inject CONST from IIP to the IP OUT")
@OutPort("OUT")
@InPort("CONST")
public class Inject extends Component {

  

  private OutputPort outport;

  private InputPort cport;

  @Override
  protected void execute() {
    Packet cp = cport.receive();
    if (cp == null) {
      return;
    }
    cport.close();
    String c = (String) cp.getContent();
    drop(cp);
    Packet pOut = create(c);
    if (!outport.isClosed()) {
      outport.send(pOut);
    }
  }

  @Override
  protected void openPorts() {
    outport = openOutput("OUT");
    cport = openInput("CONST");
  }
}
