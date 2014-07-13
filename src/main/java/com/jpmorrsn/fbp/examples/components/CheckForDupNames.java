package com.jpmorrsn.fbp.examples.components;


import java.util.HashMap;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.Packet;


/** Component to check for duplicate names in GEDCOM files
 */
@ComponentDescription("Check for duplicate names in GEDCOM files")
@InPort("IN")
public class CheckForDupNames extends Component {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport;

  @Override
  protected void execute() {

    Packet p;

    HashMap<String, String> map = new HashMap<String, String>();
    String str;
    String key;

    while ((p = inport.receive()) != null) {
      str = (String) p.getContent();
      if (str.substring(0, 6).equals("1 NAME")) {
        key = str.substring(7).trim();
        if (map.containsKey(key)) {
          System.out.println("Duplicate name: " + key);
        } else {
          map.put(key, "Y");
        }
      }
      drop(p);
    }

  }

  @Override
  protected void openPorts() {

    inport = openInput("IN");

  }
}
