/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to inject an IIP String as an IP
 */
@ComponentDescription("Inject CONST from IIP to the IP OUT")
@OutPort("OUT")
@InPort("CONST")
public class Inject extends Component {

  static final String copyright = "Copyright 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private OutputPort outport;

  private InputPort cport;

  @Override
  protected void execute() {
    Packet cp = cport.receive();
    if (cp == null) {
      return;
    }
    cport.close();
    String c = (String) cp.getContent();
    drop(cp);
    Packet pOut = create(c);
    if (!outport.isClosed()) {
      outport.send(pOut);
    }
  }

  @Override
  protected void openPorts() {
    outport = openOutput("OUT");
    cport = openInput("CONST");
  }
}
