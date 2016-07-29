/*
 * JavaFBP - A Java Implementation of Flow-Based Programming (FBP)
 * Copyright (C) 2009, 2016 J. Paul Morrison
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, see the GNU Library General Public License v3
 * at https://www.gnu.org/licenses/lgpl-3.0.en.html for more details.
 */

package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.SendRecvNoSusp;


/** This network is intended for timing runs */

public class VolumeTest4 extends Network {

  
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
