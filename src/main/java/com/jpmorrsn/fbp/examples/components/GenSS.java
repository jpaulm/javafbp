package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Generates stream of 5-packet substreams under control of a counter
*/
@ComponentDescription("Generates stream of 5-packet substreams under control of a counter")
@OutPort("OUT")
@InPort("COUNT")
public class GenSS extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private OutputPort outport;

  InputPort count;

  @Override
  protected void execute() {
    Packet ctp = count.receive();
    if (ctp == null) {
      return;
    }
    String cti = (String) ctp.getContent();
    cti = cti.trim();
    int ct = 0;
    try {
      ct = Integer.parseInt(cti);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    drop(ctp);
    count.close();

    Packet p = create(Packet.OPEN, "");
    outport.send(p);

    for (int i = 0; i < ct; i++) {

      if (i % 50 == 4) {
        p = create(Packet.CLOSE, "");
        outport.send(p);
        p = create(Packet.OPEN, "");
        outport.send(p);
      }

      int j = ct - i;
      String s = String.format("%1$06d", j) + "abcd";

      p = create(s);
      outport.send(p);

    }
    p = create(Packet.CLOSE, "");
    outport.send(p);

    // output.close();
    // terminate();
  }

  @Override
  protected void openPorts() {

    outport = openOutput("OUT");
    count = openInput("COUNT");

  }
}
