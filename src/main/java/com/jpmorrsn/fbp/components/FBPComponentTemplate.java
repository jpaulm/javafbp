/*
 * JavaFBP - A Java Implementation of Flow-Based Programming (FBP)
 * Copyright (C) 2009, 2016   --  Your name --
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


/** 
 * Template for a component - there will actually be a description here
 * 
 * Change the annotations as appropriate for the component - 
 * 
 * if more than one @InPort is needed, bracket them using @InPorts
 * 
 * if more than one @OutPort is needed, bracket them using @OutPorts
 * 
 */

@ComponentDescription("...")
@OutPort(value = "OUT", arrayPort = true)
@InPort("IN")

public class FBPComponentTemplate extends Component {

  
  private InputPort inport;

  private OutputPort[] outport;

  @Override
  protected void openPorts() {

    inport = openInput("IN");

    outport = openOutputArray("OUT");
  }

  @Override
  protected void execute() {

    /* execute logic */

    Packet p = inport.receive();

    outport[0].send(p);
  }

}
