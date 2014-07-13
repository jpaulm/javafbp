package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Component to select a specific item from a stream, by number
 *  Bob Corrick, November 2011: Make REJ port optional, and update the description.
  */
@ComponentDescription("Select from IN one packet by NUMBER (0 means first), sending via ACC, rejected packets via REJ")
@OutPorts({ @OutPort(value = "ACC"), @OutPort(value = "REJ", optional = true) })
@InPorts({ @InPort("IN"), @InPort("NUMBER") })
public class SelNthItem extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport, numport;

  private OutputPort accport, rejport;

  @Override
  protected void execute() {

    Packet ctp = numport.receive();
    if (ctp == null) {
      return;
    }
    numport.close();

    String cti = (String) ctp.getContent();
    cti = cti.trim();
    int ct = 0;
    try {
      ct = Integer.parseInt(cti);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    drop(ctp);

    Packet p;
    int i = 0;

    while ((p = inport.receive()) != null) {
      if (i == ct) {
        accport.send(p);
      } else {
        if (rejport.isConnected()) {
          rejport.send(p);
        } else {
          drop(p);
        }
      }
      i++;
    }
  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");
    numport = openInput("NUMBER");

    accport = openOutput("ACC");
    rejport = openOutput("REJ");

  }
}
