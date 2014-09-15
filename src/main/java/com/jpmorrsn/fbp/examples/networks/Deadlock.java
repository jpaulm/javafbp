package com.jpmorrsn.fbp.examples.networks;




import com.jpmorrsn.fbp.components.Concatenate;
import com.jpmorrsn.fbp.components.Discard;

import com.jpmorrsn.fbp.components.ReplString;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


/** This network forces a deadlock condition */

public class Deadlock extends Network {

  static final String copyright = "Copyright 1999, 2000, 2001, 2014, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    //tracing = true;

    connect(component("Gen", GenerateTestData.class), port("OUT"), component("ReplString", ReplString.class), port("IN"));
    initialize("10000", component("Gen"), port("COUNT"));
    connect(component("Concatenate", Concatenate.class), port("OUT"), component("Discard", Discard.class), port("IN"));

    connect(component("ReplString"), port("OUT", 0), component("Concatenate"), port("IN", 0));

    connect(component("ReplString"), port("OUT", 2), component("Concatenate"), port("IN", 1));

    connect(component("ReplString"), port("OUT", 1), component("Concatenate"), port("IN", 2));
  }

  public static void main(final String[] argv) throws Exception {
    new Deadlock().go();
  }
}
