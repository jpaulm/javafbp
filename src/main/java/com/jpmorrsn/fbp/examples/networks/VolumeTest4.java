/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */

package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.SendRecvNoSusp;


/** This network is intended for timing runs */

public class VolumeTest4 extends Network {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    component("SingleProcess", SendRecvNoSusp.class);

    initialize("100000000", component("SingleProcess"), port("COUNT"));
    //initialize("1000000", component("SingleProcess"), port("COUNT"));
    connect("SingleProcess.OUT", "SingleProcess.IN");

    /*** on my (AMD 925) machine (4 processors) - JavaFBP 2.6.6 - 366 secs
     * 100,000,000 sends, receives each
     * gives  3.66 microsecs per send/receive pair 
     * One would expect one processor only - this job runs 4 processors at about 25% 
     * So that gives .9 microsecs per send/receive pair
     * Aug. 15, 2012
     * 
     * Took out all the synchonized terms in send and receive - 
     *   I could do this as we are single-threading...
     * Hardly any change to run time (323 secs)->
     * 3.23 microsecs per send/receive w. 25% utilization -> .8 microsecs effective
     * */

  }

  public static void main(final String[] argv) throws Exception {
    new VolumeTest4().go();
  }
}
