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


import com.jpaulmorrison.fbp.core.engine.*;

/** Component to select a specific item from a stream, by number - contributed by 
 *  Bob Corrick, November 2011
  */
@ComponentDescription("Select from IN one packet by NUMBER (0 means first), sending via ACC, rejected packets via REJ")
@OutPorts({ @OutPort(value = "ACC"), @OutPort(value = "REJ", optional = true) })
@InPorts({ @InPort("IN"), @InPort("NUMBER") })
public class SelNthItem extends Component {

 
  private InputPort inport, numport;

  private OutputPort accport, rejport;

  @Override
  protected void execute() {

    Packet ctp = numport.receive();
    if (ctp == null) {
      return;
    }
    numport.close();

    String cti = (String) ctp.getContent();
    cti = cti.trim();
    int ct = 0;
    try {
      ct = Integer.parseInt(cti);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    drop(ctp);

    Packet p;
    int i = 0;

    while ((p = inport.receive()) != null) {
      if (i == ct) {
    	  if (accport.isConnected()) {
              accport.send(p);
            } else {
              drop(p);
            }
      } else {
        if (rejport.isConnected()) {
          rejport.send(p);
        } else {
          drop(p);
        }
      }
      i++;
    }
  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");
    numport = openInput("NUMBER");

    accport = openOutput("ACC");
    rejport = openOutput("REJ");

  }
}
