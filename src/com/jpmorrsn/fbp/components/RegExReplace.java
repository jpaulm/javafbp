/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.components;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.FlowError;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to find occurrences of a pattern and replace them with the second pattern.
 * The first character in the IIP string is used as the separator between the patterns, e.g.
 *    |(a*)b|$1x
 *    would find all patterns of zero or more a's, followed by b, and replace the b with an x
 */
@ComponentDescription("Find occurrences of specified regex, and replace with 2nd pattern")
@OutPort(value = "OUT", description = "modified blob")
@InPorts({ @InPort(value = "IN", description = "blob to be processed"),
    @InPort(value = "MASKS", description = "search & replace patterns") })
public class RegExReplace extends Component {

  static final String copyright = "Copyright 2009, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport, masks;

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet mp = masks.receive();
    if (mp == null) {
      FlowError.complain("No masks specified");
    }
    masks.close();

    String ms = (String) mp.getContent();
    ms = ms.trim();
    String delim = ms.substring(0, 1);
    int i = ms.indexOf(delim, 1);
    String m1 = ms.substring(1, i);
    String m2 = ms.substring(i + 1);
    System.out.println(getName() + "- Pattern: |" + m1 + "|");
    System.out.println(getName() + "- Replace with: |" + m2 + "|");
    drop(mp);
    Pattern ptn = Pattern.compile(m1);

    Packet p = null;
    StringBuffer sb = new StringBuffer();
    while (null != (p = inport.receive())) {
      String s = (String) p.getContent();
      Matcher m = ptn.matcher(s);
      m.reset();
      while (m.find()) {
        if (m.groupCount() > 0) {
          System.out.println("G1: " + m.group(1));
        }
        if (m.groupCount() == 2) {
          System.out.println("  G2: " + m.group(2));
        }
        m.appendReplacement(sb, m2);
      }
      m.appendTail(sb);
      s = sb.toString();
      outport.send(create(s));
      drop(p);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    masks = openInput("MASKS");

    outport = openOutput("OUT");
  }
}
