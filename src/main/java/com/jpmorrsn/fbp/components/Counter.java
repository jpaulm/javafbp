package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.MustRun;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Component to count a stream of packets, and output the result on the COUNT port.
*/
@ComponentDescription("Counts stream of packets and outputs result")
@InPort(value = "IN", description = "Incoming stream", type = String.class)
@OutPorts({ @OutPort(value = "OUT", description = "Stream being passed through", type = String.class, optional = true),
    @OutPort(value = "COUNT", description = "Count packet to be output", type = String.class) })
@MustRun
public class Counter extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private OutputPort countPort, outPort;

  private InputPort inPort;

  @Override
  protected void execute() {
    int count = 0;

    Packet p;
    while ((p = inPort.receive()) != null) {
      count++;
      if (outPort.isConnected()) {
        outPort.send(p);
      } else {
        drop(p);
      }
    }
    Packet ctp = create(Integer.toString(count));
    countPort.send(ctp);
  }

  @Override
  protected void openPorts() {
    inPort = openInput("IN");
    outPort = openOutput("OUT");
    countPort = openOutput("COUNT");
  }
}
