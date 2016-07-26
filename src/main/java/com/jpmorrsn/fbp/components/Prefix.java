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
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Prefix each packet IN with the given String PRE and copy it to OUT -
 * contributed by Bob Corrick - Nov. 2011
 */
@ComponentDescription("Prefix each packet IN with the given String PRE and copy it to OUT")
@OutPort("OUT")
@InPorts({ @InPort("IN"), @InPort("PRE") })
public class Prefix extends Component {
 

  private InputPort inport, preport;

  private OutputPort outport;

  @Override
  protected void execute() {
    String spre = ""; // Empty string if null
    Packet pre = preport.receive();
    if (pre != null) {
      spre = (String) pre.getContent();
      drop(pre);
    }
    preport.close();

    Packet pin;
    while ((pin = inport.receive()) != null) {
      String sout = spre + (String) pin.getContent();
      drop(pin); // did you hear that?

      Packet pout = create(sout);
      outport.send(pout);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    preport = openInput("PRE");
    outport = openOutput("OUT");
  }
}