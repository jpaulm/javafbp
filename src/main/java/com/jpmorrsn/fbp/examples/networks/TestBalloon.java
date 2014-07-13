package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


public class TestBalloon extends Network {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {

    connect(component("Gene\\ra\"te", GenerateTestData.class), port("OUT"),
        component("Balloon", com.jpmorrsn.fbp.engine.Balloon.class), port("IN"));

    connect(component("Balloon"), port("OUT"),
        component("Check", com.jpmorrsn.fbp.examples.components.CheckBallooning.class), port("IN"), 1);

    initialize("200", component("Gene\\ra\"te"), port("COUNT"));

  }

  public static void main(final String[] argv) throws Exception {
    new TestBalloon().go();
  }
}
