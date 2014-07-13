/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2008, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.components;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.MustRun;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to write data to a file, using a stream of packets. The file name
 * is specified as a string via an InitializationConnection. It is specified as
 * "must run" so that the output file will be cleared even if no data packets
 * are input.
 * This differs from the old WriteFile in that it uses a 1/2 Mbyte buffer, and only
 * write the buffer out when it is full (or at end of job).
 */
@ComponentDescription("Writes a stream of packets to an I/O file")
@InPorts({ @InPort(value = "IN", description = "Packets to be written", type = String.class),
    @InPort(value = "DESTINATION", description = "File name", type = String.class) })
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
@MustRun
public class EnhancedWriteFile extends Component {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

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
