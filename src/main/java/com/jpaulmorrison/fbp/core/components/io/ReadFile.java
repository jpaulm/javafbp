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

package com.jpaulmorrison.fbp.core.components.io;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/**
 * Component to read data from a file, generating a stream of packets. The file
 * name is specified as a String via an InitializationConnection.
 * This component converts the specified format (if one is specified) to Unicode.
 */
@ComponentDescription("Generate stream of packets from I/O file")
@OutPort(value = "OUT", description = "Generated packets", type = String.class)
@InPort(value = "SOURCE", description = "File name and optional format, separated by a comma", type = String.class)
// filename [, format ]
public class ReadFile extends Component {

  
  private OutputPort outport;

  private InputPort source;

  @Override
  protected void execute() {
    Packet<?> rp = source.receive();
    if (rp == null) {
      return;
    }
    //source.close();

    String sf = (String) rp.getContent();
    String format = null;
    int i = sf.indexOf(",");
    if (i != -1) {
      format = sf.substring(i + 1);
      format = format.trim();
      sf = sf.substring(0, i);
    }

    try {
      drop(rp);
      FileInputStream in = new FileInputStream(new File(sf));
      BufferedReader b = null;
      if (format == null) {
        b = new BufferedReader(new InputStreamReader(in));
      } else {
        b = new BufferedReader(new InputStreamReader(in, format));
      }

      String s;
      while ((s = b.readLine()) != null) {
        Packet<?> p = create(s);
        if (outport.isClosed()) {
          break;
        }

        outport.send(p);
      }
      b.close();
    } catch (IOException e) {
      System.out.println(e.getMessage() + " - file: " + sf + " - component: " + this.getName());
    }
  }

  @Override
  protected void openPorts() {

    outport = openOutput("OUT");

    source = openInput("SOURCE");

  }
}
