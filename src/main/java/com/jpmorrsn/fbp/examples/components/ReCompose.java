/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Build output records from words.
 */
@ComponentDescription("Build output records from words")
@OutPort("OUT")
@InPort("IN")
public class ReCompose extends Component {

  static final String copyright = "Copyright 2009, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  InputPort inport, size;

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet szp = size.receive();
    if (szp == null) {
      return;
    }
    size.close();
    String szi = (String) szp.getContent();
    szi = szi.trim();
    int sz = 0;
    try {
      sz = Integer.parseInt(szi);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    drop(szp);

    String s = "";

    Packet p, op;
    while ((p = inport.receive()) != null) {
      String t = (String) p.getContent();
      if (s.length() + t.length() > sz) {
        op = create(s);
        outport.send(op);
        s = "";
      }
      s += t;
      if (s.length() + 1 < sz) {
        s += " ";
      }
      drop(p);
    }
    if (s.length() > 0) {
      op = create(s);
      outport.send(op);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    size = openInput("SIZE");

    outport = openOutput("OUT");
  }
}
