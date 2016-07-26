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


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Component to concatenate two or more streams of packets
*/

@ComponentDescription("Concatenate two or more streams of packets")
@OutPort("OUT")
@InPort(value = "IN", arrayPort = true)
public class Concatenate extends Component {

  

  private InputPort[] inportArray;

  private OutputPort outport;

  @SuppressWarnings("rawtypes")
@Override
  protected void execute() {

    int no = inportArray.length;

    Packet p;
    for (int i = 0; i < no; i++) {
      while ((p = inportArray[i].receive()) != null) {
        outport.send(p);
      }

    }
  }

  @Override
  protected void openPorts() {

    inportArray = openInputArray("IN");
    //		inport.setType(Object.class);

    outport = openOutput("OUT");

  }
}
