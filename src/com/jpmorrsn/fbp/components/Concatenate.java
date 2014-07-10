package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Component to concatenate two or more streams of packets
*/
// 3 dummy output ports added for testing purposes - now commented out
//@OutPorts({@OutPort("OUT"), @OutPort("OUTM"),@OutPort("OUTN"),@OutPort("OUTO")})
@ComponentDescription("Concatenate two or more streams of packets")
@OutPort("OUT")
@InPort(value = "IN", arrayPort = true)
public class Concatenate extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort[] inportArray;

  private OutputPort outport;

  @Override
  protected void execute() {

    int no = inportArray.length;

    Packet p;
    for (int i = 0; i < no; i++) {
      while ((p = inportArray[i].receive()) != null) {
        outport.send(p);
      }

    }
  }

  @Override
  protected void openPorts() {

    inportArray = openInputArray("IN");
    //		inport.setType(Object.class);

    outport = openOutput("OUT");

  }
}
