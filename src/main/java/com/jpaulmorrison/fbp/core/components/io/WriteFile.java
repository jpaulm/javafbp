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


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

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
 * Component to write data to a file, using a stream of
 * packets. The file name is specified as a string via an
 * InitializationConnection. It is specified as "must run" so that the output
 * file will be cleared even if no data packets are input, and it will start at beginning of run.
 * This component converts Unicode to the specified format (if one is specified).
 */
@ComponentDescription("Writes a stream of packets to an I/O file")
@InPorts({
    @InPort(value = "IN", description = "Packets to be written", type = String.class),
    @InPort(value = "DESTINATION", description = "File name and optional format, separated by a comma", type = String.class) })
// filename [, format ]
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
@MustRun
public class WriteFile extends Component {

 
  private InputPort inport;

  private InputPort destination;

  private static String linesep = System.getProperty("line.separator");

  private final double _timeout = 10.0; // 10 secs

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet<?> dp = destination.receive();
    if (dp == null) {
      return;
    }
    destination.close();

    String sf = (String) dp.getContent();
    String format = null;
    int i = sf.indexOf(",");
    if (i != -1) {
      format = sf.substring(i + 1);
      format = format.trim();
      sf = sf.substring(0, i);
    }
    try {
      Writer w = null;
      FileOutputStream fos = new FileOutputStream(sf);
      if (format == null) {
        w = new OutputStreamWriter(fos);
      } else {
        w = new OutputStreamWriter(fos, format);
      }

      drop(dp);
      Packet<?> p;

      while ((p = inport.receive()) != null) {
        longWaitStart(_timeout);

        try {
          w.write((String) p.getContent());
          //System.out.println("WT" + p.getContent());
        } catch (IOException e) {
          System.err.println(e.getMessage() + " - component: " + this.getName());
        }
        w.write(linesep);
        longWaitEnd();

        //if (fp != null)
        w.flush();
        if (outport.isConnected()) {
          outport.send(p);
        } else {
          drop(p);
        }
      }
      w.close();
    } catch (IOException e) {
      System.out.println(e.getMessage() + " - file: " + sf + " - component: " + this.getName());
    }

  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    destination = openInput("DESTINATION");
    outport = openOutput("OUT");

  }
}
