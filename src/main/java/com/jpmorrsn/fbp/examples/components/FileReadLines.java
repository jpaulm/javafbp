/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.components;


import java.io.RandomAccessFile;
import java.util.Hashtable;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to read data from a file, generating a packet OUT.
 * The file is specified in the IIP SOURCE as a name with optional format (separated from name by comma).
 * This component converts the specified format (if one is specified) to Unicode.
 * The IP SEEK specifies an inital seek (file pointer) value and line count.
 * The OUT packet is a Hashtable with keys SEEK for the given file pointer, PAGE for a String array of lines from the file, and NEXT for the final file pointer value.
 */
@ComponentDescription("Send page of lines from a file")
@OutPort(value = "OUT", description = "SEEK: initial file pointer, PAGE: of lines, NEXT: file pointer", type = Hashtable.class)
@InPorts({ @InPort(value = "SOURCE", description = "File name, optional format (Java charset)", type = String.class),
    @InPort(value = "SEEK", description = "File pointer, line count required", type = String.class) })
public class FileReadLines extends Component {

  static final String copyright = "Copyright 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

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
