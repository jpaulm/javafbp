package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to pass packets through with random delay.
 */
@ComponentDescription("Delays incoming packets for random amount of time (0 - 2 secs.)")
@OutPort(value = "OUT", description = "Packets being output")
@InPort(value = "IN", description = "Incoming packets")
public class RandomDelay extends Component {

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
    while ((p = inport.receive()) != null) {
      long r = new Double(Math.random()).longValue();
      long rl = 2000 * r; // range is from 0 to 2 secs
      try {
        Thread.sleep(rl);
      } catch (InterruptedException e) {
        // do nothing
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
