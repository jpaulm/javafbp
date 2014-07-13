/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2013 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


@OutPort("OUT")
public class ChainOps extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private OutputPort outputPort;

  @Override
  protected void openPorts() {
    outputPort = openOutput("OUT");
  }

  @Override
  protected void execute() {
    Packet a = create("a");
    Packet b = create("b");
    Packet c = create("c");
    Packet d = create("d");

    attach(a, "Chain1", b);
    attach(b, "Chain1", c);
    attach(b, "Chain1", d);
    detach(b, "Chain1", c);

    detach(a, "Chain1", b);
    outputPort.send(b);
    outputPort.send(a);

    outputPort.send(c);
  }
}
