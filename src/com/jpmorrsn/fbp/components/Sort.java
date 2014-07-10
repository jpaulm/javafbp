package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Sort a stream of Packets to an output stream
**/
@ComponentDescription("Sorts up to 9999 packets, based on contents")
@OutPort(value = "OUT", description = "Output port", type = String.class)
@InPort(value = "IN", description = "Packets to be sorted", type = String.class)
public class Sort extends Component {

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
    int i = 0, j, k, n;
    Packet[] array = new Packet[9999];
    while ((p = inport.receive()) != null) {
      array[i] = p;
      //System.out.println("in: " + p.getContent());
      ++i;
    }

    //network.traceFuncs(this.getName() + ": No. of elements:" + i);
    j = 0;
    k = i;
    n = k; // no. of packets to be sent out

    while (n > 0) {
      String t = "\uffff";

      for (i = 0; i < k; i++) {
        if (array[i] != null) {

          String s = (String) array[i].getContent();

          if (s.compareTo(t) < 0) {
            j = i;
            t = (String) array[j].getContent();
          }
        }
      }
      //  if (array[j] == null) break;
      outport.send(array[j]);
      array[j] = null;

      --n;
    }

  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");

    outport = openOutput("OUT");

  }
}
