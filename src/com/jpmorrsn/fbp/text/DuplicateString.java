/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.text;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to copy all incoming packets to OUT and DUPLICATE
 */
@ComponentDescription("Copy all incoming packets to outputs OUT and DUPLICATE")
@InPort(value = "IN", type = String.class)
@OutPorts({ @OutPort("OUT"), @OutPort(value = "DUPLICATE", type = String.class) })
public class DuplicateString extends Component {

  static final String copyright = "At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport;

  private OutputPort outport;

  OutputPort duplicatePort;

  @Override
  protected void execute() {
    Packet p;
    while ((p = inport.receive()) != null) {
      String s = (String) p.getContent();
      String s2 = new String(s);
      Packet duplicate = create(s2);

      outport.send(p);

      duplicatePort.send(duplicate);

    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");
    duplicatePort = openOutput("DUPLICATE");
  }
}
