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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InPorts;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutPorts;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/**
 * Component to write menu and data to the console, then read input from the user and send as a command -
 * contributed by Bob Corrick - Feb., 2012 - for the AppKatas exercise on the FBP Google Group
 */
@ComponentDescription("Write MENU and IN to console, then capture user input and send to CMD")
@InPorts({
    @InPort(value = "IN", description = "SEEK: initial file pointer, PAGE: of lines, NEXT: file pointer", type = Hashtable.class),
    @InPort(value = "MENU", description = "Text to prompt the user", type = String.class) })
@OutPorts({ @OutPort(value = "CMD", description = "User input", type = String.class) })
public class WriteReadConsole extends Component {

  
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