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

package com.jpaulmorrison.fbp.core.engine;


/** Look after (synchronous) output/input from/to subnet.
 * This component sends a single packet out to the (external) output port, and then 
 * immediately does a receive from the corresponding (external) input port. This process
 * repeats until a null is received on the input port.
 */
@InPorts({ @InPort("NAME"), @InPort("IN") })
@OutPort("OUT")
public class SubOI extends Component {

  
  private InputPort inport, nameport, extinport;

  private OutputPort outport, extoutport;

  @Override
  protected void execute()/* throws Throwable*/{
    Packet<?> np = nameport.receive();
    if (np == null) {
      return;
    }
    nameport.close();
    String pname = (String) np.getContent();
    drop(np);

    int i = pname.indexOf(":");
    String oname = pname.substring(0, i);
    String iname = pname.substring(i + 1);
    extoutport = mother.getOutports().get(oname);
    mother.traceFuncs(getName() + ": Accessing output port: " + extoutport.getName());
    Component oldSender = extoutport.getSender();
    extoutport.setSender(this);

    extinport = mother.getInports().get(iname);
    mother.traceFuncs(getName() + ": Accessing input port: " + extinport.getName());
    Component oldReceiver = ((Connection) extinport).getReceiver();
    ((Connection) extinport).setReceiver(this);

    Packet<?> p;
    while ((p = inport.receive()) != null) {
      extoutport.send(p);

      p = extinport.receive();
      p.setOwner(this);
      outport.send(p);

    }

    mother.traceFuncs(getName() + ": Releasing input port: " + extinport.getName());
    ((Connection) extinport).setReceiver(oldReceiver);
    extinport = null;

    mother.traceFuncs(getName() + ": Releasing output port: " + extoutport.getName());
    extoutport.setSender(oldSender);
    extoutport = null;
  }

  @Override
  protected void openPorts() {

    nameport = openInput("NAME");

    inport = openInput("IN");
    outport = openOutput("OUT");
  }
}
