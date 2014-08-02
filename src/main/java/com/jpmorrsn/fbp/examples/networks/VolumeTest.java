package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Copy;
import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


/** This network is intended for timing runs */

public class VolumeTest extends Network {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    component("Generate", GenerateTestData.class);
    int lim = 1000;
    for (int i = 0; i < lim; i++) {
      component("Copy " + i, Copy.class);
    }
    component("Discard", Discard.class);

    connect(component("Generate"), port("OUT"), component("Copy 0"), port("IN"));

    for (int i = 0; i < lim - 1; i++) {
      connect(component("Copy " + i), port("OUT"), component("Copy " + (i + 1)), port("IN"));
    }
    connect(component("Copy " + (lim - 1)), port("OUT"), component("Discard"), port("IN"));

    initialize("100000", component("Generate"), port("COUNT"));

    /*** 
     * on my (AMD 925) machine (4 processors) - JavaFBP 2.6.5 - approx. 230 secs (4 mins)
     * approx. 100,000,000 sends, receives each, and 100,000 creates and drops each ->
     * gives roughly 2.3 microsecs / send-receive pair
     * Kept all processors at close to 100%!
     * Apr. 29, 2012
     * 
     * July 2014 - JavaFBP-2.8 - run time now 155 seconds. Don't know reason for the variation...
     *  
     */
  }

  public static void main(final String[] argv) throws Exception {
    new VolumeTest().go();
  }
}
