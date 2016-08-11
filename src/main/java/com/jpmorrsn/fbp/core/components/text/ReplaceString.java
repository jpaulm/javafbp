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
 
 * * Written by Bob Corrick - Feb., 2012
 */
package com.jpmorrsn.fbp.core.components.text;


import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InPorts;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/**
 * Replace FIND String in each packet IN with the given REPLacement String and copy to OUT.
 *
 */
@ComponentDescription("Replace all occurrences of text matching FIND (case-sensitive) in each packet IN with the given REPL and send to OUT")
@OutPort("OUT")
@InPorts({ @InPort(value = "IN", description = "Strings to be modified", type = String.class),
    @InPort(value = "FIND", description = "Search target", type = String.class),
    @InPort(value = "REPL", description = "Replacement text", type = String.class) })
public class ReplaceString extends Component {

  

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

    String repl = ""; // Default empty string
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
      out = replace(in, find, repl);
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

  static String replace(final String str, final String pattern, final String replace) {
    int s = 0;
    int e = 0;
    StringBuffer result = new StringBuffer();
    while ((e = str.indexOf(pattern, s)) >= 0) {
      result.append(str.substring(s, e));
      result.append(replace);
      s = e + pattern.length();
    }
    result.append(str.substring(s));
    return result.toString();
  }
}
