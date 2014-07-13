package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.jpmorrsn.fbp.engine.SelfStarting;


/**
 * Component to copy all incoming packets - mostly used for debugging purposes.
 */
@ComponentDescription("Copy all incoming packets to output, specifying SelfStarting")
@OutPort("OUT")
@InPort("IN")
@SelfStarting
public class CopySSt extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport;

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet p;
    outport.send(create("1"));
    int i = 10000;
    while ((p = inport.receive()) != null) {
      if (i-- == 0) {
        drop(p);
        outport.close();
        break;
      }
      outport.send(p);
    }

  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");
  }
}
