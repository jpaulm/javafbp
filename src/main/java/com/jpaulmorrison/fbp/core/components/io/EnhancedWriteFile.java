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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InPorts;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.MustRun;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/**
 * Component to write data to a file, using a stream of packets. The file name
 * is specified as a string via an InitializationConnection. It is specified as
 * "must run" so that the output file will be cleared even if no data packets
 * are input.
 * This differs from the old WriteFile in that it uses a 1/2 Mbyte buffer, and only
 * writes the buffer out when it is full (or at end of job).
 */
@ComponentDescription("Writes a stream of packets to an I/O file")
@InPorts({ @InPort(value = "IN", description = "Packets to be written", type = String.class),
    @InPort(value = "DESTINATION", description = "File name", type = String.class) })
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
@MustRun
public class EnhancedWriteFile extends Component {

  
  private InputPort inport;

  private InputPort destination;

  private static String linesep = System.getProperty("line.separator");

  private static double _timeout = 3.0; // 3 secs

  private OutputPort outport;

  @Override
  protected void execute() {
    FileOutputStream out = null;
    Packet dp = destination.receive();
    if (dp == null) {
      return;
    }
    destination.close();

    String s = (String) dp.getContent();
    try {
      out = new FileOutputStream(new File(s));
    } catch (FileNotFoundException e) {
      System.err.println(e.getMessage() + " - component: " + this.getName());
      return;
    }

    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

    // Writer w = new BufferedWriter(new FileWriter(s));
    char[] buffer = new char[500000];
    int bsp = 0;

    drop(dp);
    Packet p;

    while ((p = inport.receive()) != null) {
      char[] stringArray = ((String) p.getContent()).toCharArray();
      char[] stringArray2 = new char[stringArray.length + 1];
      System.arraycopy(stringArray, 0, stringArray2, 0, stringArray.length);
      stringArray2[stringArray.length] = linesep.charAt(0);
      int asp = 0;
      while (true) {

        if (bsp >= buffer.length) {
          longWaitStart(_timeout);
          try {
            bw.write(buffer, 0, bsp);
          } catch (IOException e) {
            System.err.println(e.getMessage() + " - component: " + this.getName());
            return;
          }
          longWaitEnd();
          bsp = 0;
        }
        int len = Math.min(stringArray2.length - asp, buffer.length - bsp);
        System.arraycopy(stringArray2, asp, buffer, bsp, len);
        bsp += len;
        asp += len;
        if (asp >= stringArray2.length) {
          break;
        }
      }

      if (outport.isConnected()) {
        outport.send(p);
      } else {
        drop(p);
      }
    }
    if (bsp > 0) {
      try {
        bw.write(buffer, 0, bsp);
        //bw.close();
      } catch (IOException e) {
        System.err.println(e.getMessage() + " - component: " + this.getName());
      }
    }
    try {
      bw.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    destination = openInput("DESTINATION");
    outport = openOutput("OUT");

  }
}
