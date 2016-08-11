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

package com.jpmorrsn.fbp.core.components.audio;


import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/** Convert comma-separated text to an array of ints - used in the TestTune network
**/
@ComponentDescription("Convert comma-separated text to an array of ints - used in the TestTune network")
@InPort("IN")
@OutPort("OUT")
public class Text2IntArray extends Component {

 

  InputPort _inport;

  OutputPort _outport;

  @Override
  protected void execute() {

    Packet p;
    while ((p = _inport.receive()) != null) {
      String text = (String) p.getContent();

      String[] parts = text.split(",");
      String note = parts[0].trim();
      String duration = parts[1].trim();
      int[] intArray = { Integer.parseInt(note), Integer.parseInt(duration) };
      drop(p);
      _outport.send(create(intArray));

    }

  }

  @Override
  protected void openPorts() {

    _inport = openInput("IN");
    _outport = openOutput("OUT");

  }
}
