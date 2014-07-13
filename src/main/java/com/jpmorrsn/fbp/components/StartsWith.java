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


/**
 * Select packets starting with specified string. * 
 */
@ComponentDescription("Select packets starting with specified string")
@OutPorts({ @OutPort("ACC"), @OutPort("REJ") })
@InPorts({ @InPort("IN"), @InPort("TEST") })
public class StartsWith extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport, testport;

  private OutputPort accport, rejport;

  @Override
  protected void execute() {

    Packet testPkt = testport.receive();
    if (testPkt == null) {
      return;
    }
    String testStr = (String) testPkt.getContent();
    testport.close();
    drop(testPkt);

    Packet p = inport.receive();
    while (p != null) {
      String content = (String) p.getContent();
      if (content.startsWith(testStr)) {
        accport.send(p);
      } else {
        rejport.send(p);
      }
      p = inport.receive();
    }
  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");
    testport = openInput("TEST");

    accport = openOutput("ACC");
    rejport = openOutput("REJ");

  }
}
