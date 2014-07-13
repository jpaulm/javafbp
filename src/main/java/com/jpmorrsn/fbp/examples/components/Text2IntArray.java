package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Convert comma-separated text to an array of ints
**/
@ComponentDescription("Convert comma-separated text to an array of ints")
@InPort("IN")
@OutPort("OUT")
public class Text2IntArray extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  InputPort _inport;

  OutputPort _outport;

  @Override
  protected void execute() {

    Packet p;
    while ((p = _inport.receive()) != null) {
      String text = (String) p.getContent();

      String[] parts = text.split(",");
      String note = parts[0].trim();
      String duration = parts[1].trim();
      int[] intArray = { Integer.parseInt(note), Integer.parseInt(duration) };
      drop(p);
      _outport.send(create(intArray));

    }

  }

  @Override
  protected void openPorts() {

    _inport = openInput("IN");
    _outport = openOutput("OUT");

  }
}
