package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Component to concatenate all packets in the input stream into 
* one long String, which is then sent to the output port.
*/
@ComponentDescription("Concatenate all packets in the input stream into one long String")
@OutPort("OUT")
@InPort(value = "IN", arrayPort = true)
public class ConcatStr extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport;

  private OutputPort outport;

  @SuppressWarnings("unchecked")
  @Override
  protected void execute() {

    String target = "";
    Packet<String> p;
    while ((p = inport.receive()) != null) {
      String s = p.getContent();
      target = target + s;
      drop(p);
    }
    Packet out = create(target);
    outport.send(out);

  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");

    outport = openOutput("OUT");

  }
}
