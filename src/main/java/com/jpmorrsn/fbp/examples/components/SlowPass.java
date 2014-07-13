package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Pass a stream of packets to an output stream... slowly!
 * 
 */
@ComponentDescription("Pass a stream of packets to an output stream... slowly!")
@OutPort("OUT")
@InPort("IN")
public class SlowPass extends Component {

  static final String copyright = "Copyright 2007, 2014, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport;

  private OutputPort outport;

  @Override
  protected void execute() {

    // make it a non-looper - for testing

    Packet p = inport.receive();

    try {
      sleep(100);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    outport.send(p);
  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");

    outport = openOutput("OUT");

  }
}
