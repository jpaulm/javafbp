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

package com.jpaulmorrison.fbp.resourcekit.examples.components;


import java.io.RandomAccessFile;
import java.util.Hashtable;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InPorts;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/**
 * Component to read data from a file, generating a packet OUT.
 * The file is specified in the IIP SOURCE as a name with optional format (separated from name by comma).
 * This component converts the specified format (if one is specified) to Unicode.
 * The IP SEEK specifies an inital seek (file pointer) value and line count.
 * The OUT packet is a Hashtable with keys SEEK for the given file pointer, PAGE for a String array of lines from the file, and NEXT for the final file pointer value.
 * 
 * Developed for Appkata project (?)
 * 
 */
@ComponentDescription("Send page of lines from a file")
@OutPort(value = "OUT", description = "SEEK: initial file pointer, PAGE: of lines, NEXT: file pointer", type = Hashtable.class)
@InPorts({ @InPort(value = "SOURCE", description = "File name, optional format (Java charset)", type = String.class),
    @InPort(value = "SEEK", description = "File pointer, line count required", type = String.class) })
public class FileReadLines extends Component {

 
  private OutputPort outport;

  InputPort source;

  InputPort seekport;

  @SuppressWarnings({ "unchecked" })
  @Override
  protected void execute() {
    // Determine SOURCE details
    String format = null; // Default to use stream reader readLine

    Packet rp = source.receive();
    if (rp == null) {
      return;
    }
    source.close();
    String sf = (String) rp.getContent(); // filename
    int i = sf.indexOf(",");
    if (i != -1) {
      format = sf.substring(i + 1);
      format = format.trim(); // Assume UTF format and use readUTF
      sf = sf.substring(0, i);
    }
    drop(rp);

    //Determine SEEK details
    long seek1 = 0; // Default start of file
    int count = 1; // Default read one line of input
    long seek2 = 0; // Place holder for next file pointer value

    Packet pSeek = seekport.receive();
    if (pSeek == null) {
      return;
    }
    //  seekport.close();
    String seekCmd = (String) pSeek.getContent();
    String seekCmds[] = seekCmd.split(","); // Expect filePointer, pageHeight
    try {
      seek1 = Long.parseLong(seekCmds[0]);
      seek2 = seek1;
      count = Integer.parseInt(seekCmds[1]);
    } catch (Exception e) {
      // Expect array index out of bounds if no comma, null pointer if empty seek, number format exception if not a number
      e.printStackTrace();
    }
    drop(pSeek);

    // Read from file
    try {
      // Prepare output information
      Hashtable hOut = new Hashtable(3); // for SEEK, PAGE, NEXT
      hOut.put("SEEK", new Long(seek1));
      String[] page = new String[count];

      // Open read only and seek to required start
      RandomAccessFile in = new RandomAccessFile(sf, "r");
      in.seek(seek1);
      {
        int j = 0;
        String s;
        while (j < count && (s = format == null ? in.readLine() : in.readUTF()) != null) {
          page[j++] = s;
          seek2 = in.getFilePointer();
        }

      }
      // Send OUT
      hOut.put("PAGE", page);
      hOut.put("NEXT", new Long(seek2));
      Packet pOut = create(hOut);
      if (!outport.isClosed()) {
        outport.send(pOut);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage() + " - file: " + sf + " - component: " + this.getName());
    }
  }

  @Override
  protected void openPorts() {
    outport = openOutput("OUT");
    source = openInput("SOURCE");
    seekport = openInput("SEEK");
  }
}
