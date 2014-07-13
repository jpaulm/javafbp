/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */

package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Copy;
import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


/** This network is intended for timing runs */

public class VolumeTest3 extends Network {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    component("Generate", GenerateTestData.class);
    component("Copy1", Copy.class);
    component("Copy2", Copy.class);
    component("Copy3", Copy.class);
    component("Discard", Discard.class);

    connect(component("Generate"), port("OUT"), component("Copy1"), port("IN"));
    connect(component("Copy1"), port("OUT"), component("Copy2"), port("IN"));
    connect(component("Copy2"), port("OUT"), component("Copy3"), port("IN"));
    connect(component("Copy3"), port("OUT"), component("Discard"), port("IN"));

    initialize("25000000", component("Generate"), port("COUNT"));

    /*** on my (AMD 925) machine (4 processors) - JavaFBP 2.6.6 - approx. 284 secs
     * approx. 100,000,000 sends, receives each, and 25,000,000 creates and drops each ->
     * implies 1.77 microsecs per create/discard pair; 2.40 per send/receive pair 
     * Kept all processors at close to 100% - needed at least 5 processes to do this!
     * Aug. 15, 2012
     * */

  }

  public static void main(final String[] argv) throws Exception {
    new VolumeTest3().go();
  }
}
