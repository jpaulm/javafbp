/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 * Contributed by Bob Corrick - Feb., 2012 - for the AppKatas exercise on the FBP Google Group
 */
package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to interpret incoming command.
 */
@ComponentDescription("Interpret incoming packet to create output")
@OutPort(value = "OUT", description = "Seek and count to be read from file", type = String.class)
@InPorts({ @InPort(value = "IN", description = "Packets to be interpreted", type = String.class) })
public class Interpret extends Component {

  static final String copyright = "Copyright 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport;

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet c;
    String cmd = "";
    if ((c = inport.receive()) != null) {
      cmd = (String) c.getContent();
      drop(c); // packets must be disposed of (or passed on)
    }

    // Interpret first character as option from user
    String result = "";
    char option = 'X'; // default eXit
    if (cmd != null && cmd.trim().length() > 0) {
      option = cmd.trim().charAt(0);
    }
    if (option == 'X') {
      outport.close(); // eXit
      return;
    } else if (option == 'F') {
      result = "0,4"; // go to First page
    }

    // Send result
    Packet p;
    p = create(result);
    System.out.println("Interpret: OUT: " + result);
    outport.send(p);
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    outport = openOutput("OUT");
  }
}
