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


/** Look after output from subnet - added for subnet support 
 *  Differs from SubOut in that it adds an open bracket at the beginning, and a
 *  close bracket at the end 
 */
@InPorts({ @InPort("IN"), @InPort("NAME") })
public class SubOutSS extends Component {

 
  private InputPort inport, nameport;

  private OutputPort outport;

  @Override
  protected void execute()/* throws Throwable*/{
    Packet np = nameport.receive();
    if (np == null) {
      return;
    }
    nameport.close();
    String pname = (String) np.getContent();
    drop(np);

    outport = mother.getOutports().get(pname);
    mother.traceFuncs(getName() + ": Accessing output port: " + outport.getName());
    outport.setSender(this);
    Packet p;
    p = create(Packet.OPEN, "");
    outport.send(p);

    while ((p = inport.receive()) != null) {
      outport.send(p);

    }
    p = create(Packet.CLOSE, "");
    outport.send(p);

    mother.traceFuncs(getName() + ": Releasing output port: " + outport.getName());
    outport = null;
  }

  @Override
  protected void openPorts() {

    nameport = openInput("NAME");
    inport = openInput("IN");
  }
}
