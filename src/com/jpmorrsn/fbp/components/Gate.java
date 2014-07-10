package com.jpmorrsn.fbp.components;


/*Component copies incoming packets - delayed until trigger received
 * 
 */
import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


@ComponentDescription("Copies incoming packets - delayed until trigger received")
@OutPort("OUT")
@InPorts({ @InPort("IN"), @InPort("TRIGGER") })
public class Gate extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort in;

  private InputPort trigger;

  private OutputPort out;

  @Override
  protected void execute() {
    // receive trigger
    Packet tp = trigger.receive();
    if (tp == null) {
      return;
    }
    // trigger.close();
    drop(tp);

    System.out.println("got trigger");

    Packet rp = in.receive();
    System.out.println("rp = '" + rp + "'");

    if (rp == null) {
      return;
    }
    // in.close();

    // pass output
    Object o = rp.getContent();
    Packet p = create(o);
    out.send(p);
    drop(rp);
    // }
  }

  @Override
  protected void openPorts() {
    out = openOutput("OUT");
    in = openInput("IN");
    trigger = openInput("TRIGGER");
  }
}
