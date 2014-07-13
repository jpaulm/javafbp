package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Component to generate a single packet - mostly used for debugging.
*/
@ComponentDescription("Outputs single packet containing blank")
@OutPort(value = "OUT", description = "Single packet containing blank", type = String.class)
public class Kick extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private OutputPort outport;

  @Override
  protected void execute() {

    Packet p = create(" ");
    outport.send(p);

  }

  @Override
  protected void openPorts() {
    outport = openOutput("OUT");
  }
}
