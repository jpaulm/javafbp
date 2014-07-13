package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Counter;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


public class TestCounter extends Network {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {

    connect(component("Generate", GenerateTestData.class), port("OUT"), component("Counter", Counter.class), port("IN"));

    connect(component("Counter"), port("COUNT"), component("Display", WriteToConsole.class), port("IN"));

    initialize("0", component("Generate"), port("COUNT"));

  }

  public static void main(final String[] argv) throws Exception {
    new TestCounter().go();
  }
}
