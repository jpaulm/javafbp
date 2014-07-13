/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.text;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Affix each packet IN with the given Strings PRE before and POST after, and copy it to OUT.
 *
 */
@ComponentDescription("For each packet IN add the Strings PRE as a prefix and POST as a suffix, and copy to OUT")
@OutPort("OUT")
@InPorts({ @InPort("IN"), @InPort("PRE"), @InPort("POST") })
public class Affix extends Component {

  static final String copyright = "Copyright 2007, 2010, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  InputPort inport, preport, postport;

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

    String spost = ""; // Empty string if null
    Packet post = postport.receive();
    if (post != null) {
      spost = (String) post.getContent();
      drop(post);
    }
    postport.close();

    Packet pin;
    while ((pin = inport.receive()) != null) {
      String sout = spre + (String) pin.getContent() + spost;
      drop(pin); // did you hear that?

      Packet pout = create(sout);
      outport.send(pout);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    preport = openInput("PRE");
    postport = openInput("POST");
    outport = openOutput("OUT");
  }
}
