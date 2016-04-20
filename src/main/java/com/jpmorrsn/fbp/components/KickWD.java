package com.jpmorrsn.fbp.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to kick-start processing, emitting an IP with specified data
 */
@ComponentDescription("Kick with data")
@OutPort(value = "OUT", description = "Generated packets", type = String.class)
@InPort(value = "SOURCE", description = "Data string", type = String.class)

public class KickWD extends Component {

  static final String copyright = "Copyright 2007, 2008, 2016, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private OutputPort outport;

  private InputPort source;

  @Override
  protected void execute() {
    Packet rp = source.receive();
    if (rp != null) 
        outport.send(rp);
  }

  @Override
  protected void openPorts() {

    outport = openOutput("OUT");

    source = openInput("SOURCE");

  }
}
