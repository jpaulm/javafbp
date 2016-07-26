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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to read data from Thunderbird Inbox file, generating a stream of emails. The file
 * name is specified as a String via an InitializationConnection.
 */
@ComponentDescription("Generate stream of emails from I/O file")
@OutPort(value = "OUT", description = "Generated emails", type = String.class)
@InPort(value = "SOURCE", description = "File name", type = String.class)
public class ReadEmails extends Component {

  
  private OutputPort outport;

  private InputPort source;

  @Override
  protected void execute() {
    Packet rp = source.receive();
    if (rp == null) {
      return;
    }
    source.close();

    String sf = (String) rp.getContent();
    try {
      drop(rp);
      FileInputStream in = new FileInputStream(new File(sf));
      BufferedReader b = new BufferedReader(new InputStreamReader(in));

      String s, t, u = "";
      int i, j, k;
      char[] bs = new char[1000000];
      while (true) {
        i = b.read(bs, 0, bs.length);
        if (i == -1) {
          break;
        }
        s = new String(bs, 0, i);
        j = 0;
        while (true) {
          k = s.indexOf("\nFrom - ", j);
          if (k == -1) {
            break;
          }
          t = s.substring(j, k + 1);
          Packet p = create(u + t);
          u = "";
          outport.send(p);
          j = k + 1;
        }
        //u = u + new String(bs, j, i - j);
        if (u.length() == 0) {
          u = new String(bs, j, i - j); //fudge to save space
        }
      }
      b.close();
    } catch (IOException e) {
      System.out.println("I/O Error on file: " + sf);
    }
  }

  @Override
  protected void openPorts() {

    outport = openOutput("OUT");

    source = openInput("SOURCE");

  }
}
