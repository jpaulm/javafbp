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


import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InPorts;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/**
 * Component to select names and email addresses from an LDIF file, and generate fixed layout packets.
 */
@ComponentDescription("Select names and email addresses from an LDIF file, and generate fixed layout packets")
@OutPort("OUT")
@InPorts( { @InPort(value = "LABEL", description = "Label for output packets", type = String.class), @InPort("IN") })
public class LDIFScan extends Component {

 
  InputPort inport, labelport;

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet lp = labelport.receive();
    if (lp == null) {
      return;
    }
    labelport.close();
    drop(lp);

    String label = (String) lp.getContent();
    label = label.trim();
    Packet p, p2 = null;
    String email = "", name = "";
    boolean started = false;
    int i;

    while ((p = inport.receive()) != null) {
      String s = (String) p.getContent();

      if (s.length() > 3 && s.substring(0, 3).equals("dn:")) {
        if (started) {
          name += "                                                  ".substring(0, 50 - name.length());
          email += "                                                  ".substring(0, 50 - email.length());
          p2 = create(name + email + label);
          outport.send(p2);
          name = "";
          email = "";
        }

        started = true;
      }
      if (s.length() > 3 && s.substring(0, 3).equals("cn:")) {
        i = s.indexOf(4, ' ');
        if (i == -1) {
          i = s.length();
        }
        name = s.substring(4, i);

      }
      if (s.length() > 5 && s.substring(0, 5).equals("mail:")) {
        i = s.indexOf(6, ' ');
        if (i == -1) {
          i = s.length();
        }
        email = s.substring(6, i);

      }

      drop(p);

    }
    name += "                                                  ".substring(0, 50 - name.length());
    email += "                                                  ".substring(0, 50 - email.length());

    p2 = create(name + email + label);
    outport.send(p2);
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    labelport = openInput("LABEL");

    outport = openOutput("OUT");
  }
}
