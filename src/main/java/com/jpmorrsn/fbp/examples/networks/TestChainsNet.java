/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.ChainOps;


public class TestChainsNet extends Network {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {

    connect(component("TestChains", ChainOps.class), port("OUT"), component("Discard", Discard.class), port("IN"));

  }

  public static void main(final String[] argv) throws Exception {
    new TestChainsNet().go();
  }
}
