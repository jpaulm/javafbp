package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Replicate a stream of Packets to 'n' output streams, where
*  each Packet points at a String.
* Note: this is a specific variant of the general Replicate verb (q.v.)
**/
@ComponentDescription("Replicate stream of String packets to multiple output streams")
@OutPort(value = "OUT", arrayPort = true, description = "Replicated packets", type = String.class)
@InPort(value = "IN", description = "Incoming packets", type = String.class)
public class ReplString extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport;

  private OutputPort[] outportArray;

  @Override
  protected void execute() {

    int no = outportArray.length;

    Packet p;
    //long count = 0;
    while ((p = inport.receive()) != null) {
      //++count;
      String o = (String) p.getContent();
      drop(p);

      for (int i = 0; i < no; i++) {
        String o2 = new String(o);
        Packet p2 = create(o2);
        outportArray[i].send(p2);

      }
    }
    //network.traceFuncs("Repl complete. " + getName());
  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");

    outportArray = openOutputArray("OUT");

  }
}
