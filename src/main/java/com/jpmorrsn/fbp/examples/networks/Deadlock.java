package com.jpmorrsn.fbp.examples.networks;


import java.io.File;

import com.jpmorrsn.fbp.components.Concatenate;
import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.ReplString;
import com.jpmorrsn.fbp.engine.Network;


/** This network forces a deadlock condition */

public class Deadlock extends Network {

  static final String copyright = "Copyright 1999, 2000, 2001, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    //tracing = true;

    connect(component("Read", ReadFile.class), port("OUT"), component("ReplString", ReplString.class), port("IN"));
    initialize("testdata/testdata.txt".replace("/", File.separator), component("Read"), port("SOURCE"));
    connect(component("Concatenate", Concatenate.class), port("OUT"), component("Discard", Discard.class), port("IN"));

    connect(component("ReplString"), port("OUT", 0), component("Concatenate"), port("IN", 0));

    connect(component("ReplString"), port("OUT", 2), component("Concatenate"), port("IN", 1));

    connect(component("ReplString"), port("OUT", 1), component("Concatenate"), port("IN", 2));
  }

  public static void main(final String[] argv) throws Exception {
    new Deadlock().go();
  }
}
