/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved.
 * Contributed by Bob Corrick - Feb., 2012 - for the AppKatas exercise on the FBP Google Group
 */
package com.jpmorrsn.fbp.examples.components;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to write menu and data to the console, then read input from the user and send as a command
 */
@ComponentDescription("Write MENU and IN to console, then capture user input and send to CMD")
@InPorts({
    @InPort(value = "IN", description = "SEEK: initial file pointer, PAGE: of lines, NEXT: file pointer", type = Hashtable.class),
    @InPort(value = "MENU", description = "Text to prompt the user", type = String.class) })
@OutPorts({ @OutPort(value = "CMD", description = "User input", type = String.class) })
public class WriteReadConsole extends Component {

  static final String copyright = "Copyright 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  InputPort inport, menuport;

  OutputPort cmdport;

  @Override
  protected void execute() {

    // Display MENU at the "top"
    Packet pMenu;
    String menu = "";
    if ((pMenu = menuport.receive()) != null) {
      menu = (String) pMenu.getContent();
      drop(pMenu);
    }
    System.out.println(menu + "\n"); // followed by a blank line

    // Display INput which is the full data set (not looping)
    Packet pIn = inport.receive();
    if (pIn != null) {
      Hashtable hIn = (Hashtable) pIn.getContent();
      String[] page = (String[]) hIn.get("PAGE");
      for (String element : page) {
        System.out.println(element);
      }
      drop(pIn); // packets must be dropped (or sent on)

      // Capture CMD from user and send it
      String cmd = "";
      System.out.print("> ");
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      try {
        cmd = br.readLine();
      } catch (IOException e) {
        System.out.println(e.getMessage());
      }
      Packet pCmd = create(cmd);
      cmdport.send(pCmd);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    menuport = openInput("MENU");

    cmdport = openOutput("CMD");
  }
}