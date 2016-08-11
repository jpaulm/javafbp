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


import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InPorts;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/**
 * Affix each packet IN with the given Strings PRE before and POST after, and copy it to OUT.
 *
 */
@ComponentDescription("For each packet IN add the Strings PRE as a prefix and POST as a suffix, and copy to OUT")
@OutPort("OUT")
@InPorts({ @InPort("IN"), @InPort("PRE"), @InPort("POST") })
public class Affix extends Component {

  

  InputPort inport, preport, postport;

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

    String spost = ""; // Empty string if null
    Packet post = postport.receive();
    if (post != null) {
      spost = (String) post.getContent();
      drop(post);
    }
    postport.close();

    Packet pin;
    while ((pin = inport.receive()) != null) {
      String sout = spre + (String) pin.getContent() + spost;
      drop(pin); // did you hear that?

      Packet pout = create(sout);
      outport.send(pout);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    preport = openInput("PRE");
    postport = openInput("POST");
    outport = openOutput("OUT");
  }
}
