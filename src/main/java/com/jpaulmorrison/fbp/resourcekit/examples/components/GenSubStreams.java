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


/* Sven Steinseifer - 2010 */

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


@OutPort("OUT")
public class GenSubStreams extends Component {

  OutputPort outputPort;

  @Override
  protected void execute() {
    outputPort.send(create(Packet.OPEN, "OPEN(1)"));
    outputPort.send(create("A"));
    outputPort.send(create(Packet.OPEN, "OPEN(2)"));
    outputPort.send(create("B"));
    outputPort.send(create(Packet.CLOSE, "CLOSE(2)"));
    outputPort.send(create(Packet.CLOSE, "CLOSE(1)"));
    outputPort.close();
  }

  @Override
  protected void openPorts() {
    outputPort = openOutput("OUT");
  }

}
