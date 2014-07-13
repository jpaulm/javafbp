/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to break up input packets into words.
 */
@ComponentDescription("Break up input packets into words")
@OutPort("OUT")
@InPort("IN")
public class DeCompose extends Component {

  static final String copyright = "Copyright 2009, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport;

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet p;
    while ((p = inport.receive()) != null) {
      String s = (String) p.getContent();
      boolean in_word = false;
      int word_start = 0;
      for (int i = 0; i < s.length(); i++) {
        if (!in_word && s.charAt(i) != ' ') {
          in_word = true;
          word_start = i;
        }
        if (in_word && s.charAt(i) == ' ') {
          in_word = false;
          String t = s.substring(word_start, i);
          Packet q = create(t);
          outport.send(q);
        }
      }
      drop(p);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");
  }
}
