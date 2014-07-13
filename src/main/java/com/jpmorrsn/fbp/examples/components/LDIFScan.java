/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to select names and email addresses, and generate fixed layout packets.
 */
@ComponentDescription("Select names and email addresses, and generate fixed layout packets")
@OutPort("OUT")
@InPorts( { @InPort(value = "LABEL", description = "Label for output packets", type = String.class), @InPort("IN") })
public class LDIFScan extends Component {

  static final String copyright = "Copyright 2009, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  InputPort inport, labelport;

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet lp = labelport.receive();
    if (lp == null) {
      return;
    }
    labelport.close();
    drop(lp);

    String label = (String) lp.getContent();
    label = label.trim();
    Packet p, p2 = null;
    String email = "", name = "";
    boolean started = false;
    int i;

    while ((p = inport.receive()) != null) {
      String s = (String) p.getContent();

      if (s.length() > 3 && s.substring(0, 3).equals("dn:")) {
        if (started) {
          name += "                                                  ".substring(0, 50 - name.length());
          email += "                                                  ".substring(0, 50 - email.length());
          p2 = create(name + email + label);
          outport.send(p2);
          name = "";
          email = "";
        }

        started = true;
      }
      if (s.length() > 3 && s.substring(0, 3).equals("cn:")) {
        i = s.indexOf(4, ' ');
        if (i == -1) {
          i = s.length();
        }
        name = s.substring(4, i);

      }
      if (s.length() > 5 && s.substring(0, 5).equals("mail:")) {
        i = s.indexOf(6, ' ');
        if (i == -1) {
          i = s.length();
        }
        email = s.substring(6, i);

      }

      drop(p);

    }
    name += "                                                  ".substring(0, 50 - name.length());
    email += "                                                  ".substring(0, 50 - email.length());

    p2 = create(name + email + label);
    outport.send(p2);
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    labelport = openInput("LABEL");

    outport = openOutput("OUT");
  }
}
