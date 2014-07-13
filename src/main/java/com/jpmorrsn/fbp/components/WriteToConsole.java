package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.MustRun;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to write data to the console, using a stream of packets. It is
 * specified as "must run" so that the output file will be cleared even if no
 * data packets are input.
 */
@ComponentDescription("Write stream of packets to console")
@InPort(value = "IN", description = "Packets to be displayed", type = String.class)
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
@MustRun
public class WriteToConsole extends Component {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport;

  private final double _timeout = 5.0; // 5 secs

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet p;

    while ((p = inport.receive()) != null) {
      longWaitStart(_timeout);
      // sleep(5000L); //force timeout - testing only
      if (p.getType() == Packet.OPEN) {
        System.out.println("===> Open Bracket");
      } else if (p.getType() == Packet.CLOSE) {
        System.out.println("===> Close Bracket");
      } else {
        System.out.println((String) p.getContent());
      }
      longWaitEnd();
      if (outport.isConnected()) {
        outport.send(p);
      } else {
        drop(p);
      }
    }

  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");

  }
}
