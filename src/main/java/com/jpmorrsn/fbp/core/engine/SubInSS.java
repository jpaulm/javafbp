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


/**
 * Look after input to subnet - added for subnet support. This version of SubIn
 * supports Substream Sensitivity
 */
@InPort("NAME")
@OutPort("OUT")
public class SubInSS extends Component {

  

  /*  Changes thanks to Sven Steinseifer */

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

    Component oldReceiver = ((Connection) inport).getReceiver();
    if (inport instanceof InitializationConnection) {
      FlowError.complain("SubinSS cannot support IIP - use Subin");
    }

    ((Connection) inport).setReceiver(this);
    int level = 0;
    while ((p = inport.receive()) != null) {
      p.setOwner(this);
      if (p.getType() == Packet.OPEN) {
        if (level > 0) {
          outport.send(p);
        } else {
          drop(p);
          mother.traceFuncs(this.getName() + " open bracket detected");
        }
        level++;
      } else if (p.getType() == Packet.CLOSE) {
        if (level > 1) {
          // pass on nested brackets
          outport.send(p);
          level--;
        } else {
          drop(p);
          mother.traceFuncs(this.getName() + " close bracket detected");
          break;
        }
      } else {
        outport.send(p);
      }
    }

    mother.traceFuncs(getName() + ": Releasing input port: " + inport.getName());
    ((Connection) inport).setReceiver(oldReceiver);

    inport = null;
    // outport.close();
  }

  @Override
  protected void openPorts() {

    nameport = openInput("NAME");
    outport = openOutput("OUT");
  }
}
