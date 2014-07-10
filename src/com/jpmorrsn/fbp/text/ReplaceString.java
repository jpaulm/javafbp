/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 * * Written by Bob Corrick - Feb., 2012
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
 * Replace FIND String in each packet IN with the given REPLacement String and copy to OUT.
 *
 */
@ComponentDescription("Replace all occurrences of text matching FIND (case-sensitive) in each packet IN with the given REPL and send to OUT")
@OutPort("OUT")
@InPorts({ @InPort(value = "IN", description = "Strings to be modified", type = String.class),
    @InPort(value = "FIND", description = "Search target", type = String.class),
    @InPort(value = "REPL", description = "Replacement text", type = String.class) })
public class ReplaceString extends Component {

  static final String copyright = "Copyright 2007, 2010, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  InputPort inport, findport, replport;

  private OutputPort outport;

  @Override
  protected void execute() {
    String find = ""; // Default empty string
    Packet pfind = findport.receive();
    if (pfind != null) {
      find = (String) pfind.getContent();
      drop(pfind);
    }
    findport.close();

    String repl = ""; // Default empty string
    Packet prepl = replport.receive();
    if (prepl != null) {
      repl = (String) prepl.getContent();
      drop(prepl);
    }
    replport.close();

    Packet pin;
    while ((pin = inport.receive()) != null) {
      String out = "";
      String in = (String) pin.getContent();
      out = replace(in, find, repl);
      drop(pin);

      Packet pout = create(out);
      outport.send(pout);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    findport = openInput("FIND");
    replport = openInput("REPL");
    outport = openOutput("OUT");
  }

  static String replace(final String str, final String pattern, final String replace) {
    int s = 0;
    int e = 0;
    StringBuffer result = new StringBuffer();
    while ((e = str.indexOf(pattern, s)) >= 0) {
      result.append(str.substring(s, e));
      result.append(replace);
      s = e + pattern.length();
    }
    result.append(str.substring(s));
    return result.toString();
  }
}
