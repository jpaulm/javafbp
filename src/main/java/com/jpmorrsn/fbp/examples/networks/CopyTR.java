package com.jpmorrsn.fbp.examples.networks;

import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;
import com.jpmorrsn.fbp.examples.components.TabulaRasa;


public class CopyTR extends Network {

  static final String copyright = "Copyright 2007, ..., 2014, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {

    connect(component("Generate", GenerateTestData.class), port("OUT"), component("TabulaRasa", TabulaRasa.class), port("IN"));

    connect(component("TabulaRasa"), port("OUT"), component("Write", WriteToConsole.class), port("IN"));

    initialize("2000", component("Generate"), port("COUNT"));
    
    initialize("com.jpmorrsn.fbp.components.Copy", component("TabulaRasa"), port("COMP"));

  }

  public static void main(final String[] argv) throws Exception {
    new CopyTR().go();
  }
}
