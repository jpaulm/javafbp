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
package com.jpmorrsn.fbp.core.components.text;


import com.jpmorrsn.fbp.core.engine.*;


/** Component to ConcatStreams all packets in the input stream into 
* one long String, which is then sent to the output port.
*/
@ComponentDescription("ConcatStreams all packets in the input stream into one long String")
@OutPort("OUT")
@InPort(value = "IN", arrayPort = true)
public class ConcatString extends Component {

 

  private InputPort inport;

  private OutputPort outport;

  @SuppressWarnings("unchecked")
  @Override
  protected void execute() {

    String target = "";
    Packet<String> p;
    while ((p = inport.receive()) != null) {
      String s = p.getContent();
      target = target + s;
      drop(p);
    }
    Packet out = create(target);
    outport.send(out);

  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");

    outport = openOutput("OUT");

  }
}
