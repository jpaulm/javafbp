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
package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to break up input packets into words.
 */
@ComponentDescription("Break up input packets into words")
@OutPort("OUT")
@InPort("IN")
public class DeCompose extends Component {

 
  private InputPort inport;

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet p;
    while ((p = inport.receive()) != null) {
      String s = (String) p.getContent();
      boolean in_word = false;
      int word_start = 0;
      for (int i = 0; i < s.length(); i++) {
        if (!in_word && !(s.substring(i, i + 1).matches("\\s|\\p{Punct}"))) {  
          in_word = true;
          word_start = i;
        }
        if (in_word && s.substring(i, i + 1).matches("\\s|\\p{Punct}")) {
          in_word = false;
          String t = s.substring(word_start, i);
          Packet q = create(t);
          outport.send(q);
        }
      }
      drop(p);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");
  }
}
