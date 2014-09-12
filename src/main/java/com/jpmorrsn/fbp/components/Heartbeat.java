package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Component to generate packets every 'n' milliseconds, where 'n' is
* specified in an InitializationConnection.
*/
@ComponentDescription("Generates a packet every 'n' milliseconds")
@OutPort("OUT")
@InPort("INTERVAL")
public class Heartbeat extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private OutputPort outport;

  private InputPort interval;

  @SuppressWarnings("rawtypes")
@Override
  protected void execute() {
    // receive interval in milliseconds
    Packet itvl = interval.receive();
    if (itvl == null) {
      return;
    }
    Long itl = (Long) itvl.getContent();
    long it = itl.longValue();
    drop(itvl);
    String s = " ";

    // when the send returns false, this component closes down

    while (true) {
      Packet p = create(s);
      outport.send(p);
      try {
        sleep(it);
      } catch (InterruptedException e) {
        // do nothing
      }
    }

  }

  @Override
  protected void openPorts() {

    outport = openOutput("OUT");

    interval = openInput("INTERVAL");

  }
}
