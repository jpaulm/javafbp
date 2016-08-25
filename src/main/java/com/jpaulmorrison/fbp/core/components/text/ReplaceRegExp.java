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
 * 

 * Written by Bob Corrick - Feb., 2012
 */
package com.jpaulmorrison.fbp.core.components.text;


import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InPorts;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/**
 * Replace every FIND String (Java regular expression) in each packet IN with the given REPLacement String and copy to OUT.
 *
 */
@ComponentDescription("Replace all occurrences of FIND in each packet IN with the given REPL and copy to OUT")
@OutPort("OUT")
@InPorts({
    @InPort(value = "IN", description = "Strings to have replacement applied", type = String.class),
    @InPort(value = "FIND", description = "String to find (you can use a Java regular expression)", type = String.class),
    @InPort(value = "REPL", description = "Replacement String", type = String.class) })
public class ReplaceRegExp extends Component {

  
  InputPort inport, findport, replport;

  private OutputPort outport;

  @Override
  protected void execute() {
    String find = ""; // Default empty string
    Packet pfind = findport.receive();
    if (pfind != null) {
      find = (String) pfind.getContent();
      drop(pfind);
    }
    findport.close();

    String repl = " "; // Default empty string
    Packet prepl = replport.receive();
    if (prepl != null) {
      repl = (String) prepl.getContent();
      drop(prepl);
    }
    replport.close();

    Packet pin;
    while ((pin = inport.receive()) != null) {
      String out = "";
      String in = (String) pin.getContent();
      out = in.replaceAll(find, repl);
      drop(pin);

      Packet pout = create(out);
      outport.send(pout);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    findport = openInput("FIND");
    replport = openInput("REPL");
    outport = openOutput("OUT");
  }
}
