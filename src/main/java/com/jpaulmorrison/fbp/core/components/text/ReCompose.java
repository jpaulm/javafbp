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
package com.jpaulmorrison.fbp.core.components.text;


import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/**
 * Build output records from words.
 */
@ComponentDescription("Build output records from words")
@OutPort("OUT")
@InPort("IN")
public class ReCompose extends Component {

  

  InputPort inport, size;

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet szp = size.receive();
    if (szp == null) {
      return;
    }
    size.close();
    String szi = (String) szp.getContent();
    szi = szi.trim();
    int sz = 0;
    try {
      sz = Integer.parseInt(szi);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    drop(szp);

    String s = "";

    Packet p, op;
    while ((p = inport.receive()) != null) {
      String t = (String) p.getContent();
      if (s.length() + t.length() > sz) {
        op = create(s);
        outport.send(op);
        s = "";
      }
      s += t;
      if (s.length() + 1 < sz) {
        s += " ";
      }
      drop(p);
    }
    if (s.length() > 0) {
      op = create(s);
      outport.send(op);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    size = openInput("SIZE");

    outport = openOutput("OUT");
  }
}
