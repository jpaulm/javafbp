package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.jpmorrsn.fbp.engine.SelfStarting;


/** Component to generate a stream of 'n' packets, where 'n' is
* specified in an InitializationConnection.
*/
@ComponentDescription("Testing send/recv with no suspension")
@OutPort(value = "OUT")
@InPorts({ @InPort(value = "COUNT"), @InPort(value = "IN") })
@SelfStarting
public class SendRecvNoSusp extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private OutputPort outport;

  InputPort count, inport;

  @Override
  protected void execute() {
    Packet ctp = count.receive();
    if (ctp == null) {
      return;
    }
    count.close();

    String cti = (String) ctp.getContent();
    cti = cti.trim();
    int ct = 0;
    try {
      ct = Integer.parseInt(cti);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    drop(ctp);
    Packet p = create("testing");

    for (int i = ct; i > 0; i--) {
      outport.send(p);
      p = inport.receive();
    }
    drop(p);
  }

  @Override
  protected void openPorts() {
    outport = openOutput("OUT");
    count = openInput("COUNT");
    inport = openInput("IN");
  }
}
