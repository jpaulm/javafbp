package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Component to split an input stream into multiple output streams,
* where the first 30 packets go to the first output port, the next 30 go
* to the second and so on.  Each output stream is closed before data starts
* being sent to the next.  This component was used for testing deadlock behaviour.
*/
@ComponentDescription("Splits a stream into multiple output streams")
@OutPort(value = "OUT", arrayPort = true)
@InPort("IN")
public class Splitter1 extends Component {

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
    int e = 0;
    int i = 0;
    Packet p;

    while ((p = inport.receive()) != null) {

      outportArray[e].send(p);
      if (i == 30) {
        if (e < no - 1) { // if last output stream, don't close it
          outportArray[e].close();
          ++e;
        }
        i = 0;
      } else {
        i++;
      }

    }
    while (e < no) {
      outportArray[e].close();
      e++;
    }

  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");

    outportArray = openOutputArray("OUT");

  }
}
