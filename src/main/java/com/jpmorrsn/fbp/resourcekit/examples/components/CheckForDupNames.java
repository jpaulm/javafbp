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


import java.util.HashMap;

import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/** Component to check for duplicate names in GEDCOM files
 */
@ComponentDescription("Check for duplicate names in GEDCOM files")
@InPort("IN")
public class CheckForDupNames extends Component {

  
  private InputPort inport;

  @Override
  protected void execute() {

    Packet p;

    HashMap<String, String> map = new HashMap<String, String>();
    String str;
    String key;

    while ((p = inport.receive()) != null) {
      str = (String) p.getContent();
      if (str.substring(0, 6).equals("1 NAME")) {
        key = str.substring(7).trim();
        if (map.containsKey(key)) {
          System.out.println("Duplicate name: " + key);
        } else {
          map.put(key, "Y");
        }
      }
      drop(p);
    }

  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");

  }
}
