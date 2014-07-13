/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */

package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


/** This network is intended for timing runs */

public class VolumeTest2 extends Network {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    component("Generate", GenerateTestData.class);

    component("Discard", Discard.class);

    connect(component("Generate"), port("OUT"), component("Discard"), port("IN"));

    initialize("100000000", component("Generate"), port("COUNT"));

    /*** on my (AMD 925) machine (4 processors) - JavaFBP 2.6.5 - approx. 1200 secs
     * approx. 100,000,000 sends, receives each, and 100,000,000 creates and drops each ->
     * only uses 2 processors, so elapsed time unrealistic
     * Aug. 11, 2012
     * */

  }

  public static void main(final String[] argv) throws Exception {
    new VolumeTest2().go();
  }
}
