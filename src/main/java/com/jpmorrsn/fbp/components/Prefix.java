package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Prefix each packet IN with the given String PRE and copy it to OUT.
 * Contributed by Bob Corrick - Nov. 2011
 */
@ComponentDescription("Prefix each packet IN with the given String PRE and copy it to OUT")
@OutPort("OUT")
@InPorts({ @InPort("IN"), @InPort("PRE") })
public class Prefix extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison. At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport, preport;

  private OutputPort outport;

  @Override
  protected void execute() {
    String spre = ""; // Empty string if null
    Packet pre = preport.receive();
    if (pre != null) {
      spre = (String) pre.getContent();
      drop(pre);
    }
    preport.close();

    Packet pin;
    while ((pin = inport.receive()) != null) {
      String sout = spre + (String) pin.getContent();
      drop(pin); // did you hear that?

      Packet pout = create(sout);
      outport.send(pout);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    preport = openInput("PRE");
    outport = openOutput("OUT");
  }
}