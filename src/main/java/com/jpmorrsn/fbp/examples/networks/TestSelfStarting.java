package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Copy;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.CopySSt;


public class TestSelfStarting extends Network {

  static final String copyright = "Copyright 2007, 2008, ... 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    connect(component("Copy", Copy.class), port("OUT"), component("CopySSt", CopySSt.class), port("IN"));
    connect(component("CopySSt"), port("OUT"), component("Copy"), port("IN"));
  }

  public static void main(final String[] argv) throws Exception {
    new TestSelfStarting().go();
  }
}
