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

package com.jpmorrsn.fbp.core.engine;

import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;

/**
 * Look after input to subnet - added for subnet support
 */
@InPort("NAME")
@OutPort("OUT")
public class SubIn extends Component {

  

  private InputPort inport, nameport;

  private OutputPort outport;

  @Override
  protected void execute() {

    Packet np = nameport.receive();
    if (np == null) {
      return;
    }
    nameport.close();
    String pname = (String) np.getContent();
    drop(np);

    if (outport.isClosed) {
      return;
    }

    inport = mother.getInports().get(pname);
    mother.traceFuncs(getName() + ": Accessing input port: " + inport.getName());
    Packet p;
    // I think this works!
    Component oldReceiver;
    if (inport instanceof InitializationConnection) {
      InitializationConnection iico = (InitializationConnection) inport;
      oldReceiver = iico.getReceiver();
      InitializationConnection iic = new InitializationConnection(iico.content, this);
      iic.name = iico.name;
      //iic.network = iico.network;

      p = iic.receive();
      p.setOwner(this);
      outport.send(p);
      iic.close();
    } else {
      oldReceiver = ((Connection) inport).getReceiver();
      ((Connection) inport).setReceiver(this);
      //Connection c = (Connection) inport;

      while ((p = inport.receive()) != null) {
        p.setOwner(this);
        outport.send(p);
      }
    }

    // inport.close();
    mother.traceFuncs(getName() + ": Releasing input port: " + inport.getName());

    inport.setReceiver(oldReceiver);

  }

  @Override
  protected void openPorts() {

    nameport = openInput("NAME");
    outport = openOutput("OUT");
  }
}
