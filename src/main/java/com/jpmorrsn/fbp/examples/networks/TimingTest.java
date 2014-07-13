package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.Passthru;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


/** This network is intended for timing runs */

public class TimingTest extends Network {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {

    /* This network funnels 50 Generates into one Discard component  

    component("Discard", Discard.class);
    for (int i = 0; i < 50; i++) {
      connect(component("Generate" + i, Generate.class), port("OUT"), component("Discard"), port("IN"), 100);
      initialize("20000", component("Generate" + i), port("COUNT"));
    }
     */
    // This alternative network funnels 50 Generates into 50 Discard components 

    for (int i = 0; i < 50; i++) {
      connect(component("Generate" + i, GenerateTestData.class), port("OUT"), component("Passthru" + i, Passthru.class),
          port("IN"), 5);
      connect(component("Passthru" + i), port("OUT"), component("Discard" + i, Discard.class), port("IN"), 5);
      initialize("20000", component("Generate" + i), port("COUNT"));
    }

  }

  public static void main(final String[] argv) throws Exception {
    new TimingTest().go();
  }
}
