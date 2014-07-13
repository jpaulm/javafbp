package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;


/**
 *  Component to test array port handling.
*/
@ComponentDescription("Outputs single packet containing blank")
@OutPorts({ @OutPort(value = "OUTCHINFO", setDimension = 2, type = String.class),
    @OutPort(value = "OUTCHDATA", type = String.class, arrayPort = true, optional = true) })
public class Ern1 extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  OutputPort[] outportArray;

  OutputPort outport1, outport2;

  @Override
  protected void openPorts() {
    outport1 = openOutput("OUTCHINFO0");
    outport2 = openOutput("OUTCHINFO1");
    outportArray = openOutputArray("OUTCHDATA");
  }

  @Override
  protected void execute() {

    outport1.send(create(" "));
    outport2.send(create(" "));
    if (outportArray != null) {
      if (outportArray[0].isConnected()) {
        outportArray[0].send(create(" "));
      }
      if (outportArray[1].isConnected()) {
        outportArray[1].send(create(" "));
      }
    }

  }

}
