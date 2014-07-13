/*
 * Copyright (C) J.P. Morrison Enterprises, Ltd. 2009, 2014 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Connection;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;
import com.jpmorrsn.fbp.examples.components.SlowPass;


public class TestDropOldest extends Network {

  static final String copyright = "Copyright 2007, 2008, 2014, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {

    Connection c = connect(component("Generate", GenerateTestData.class), port("OUT"),
        component("SlowPass", SlowPass.class), port("IN"));
    c.setDropOldest();
    connect(component("SlowPass"), port("OUT"), component("Display", WriteToConsole.class), port("IN"));

    initialize("2000", component("Generate"), port("COUNT"));

  }

  public static void main(final String[] argv) throws Exception {
    new TestDropOldest().go();
  }
}
