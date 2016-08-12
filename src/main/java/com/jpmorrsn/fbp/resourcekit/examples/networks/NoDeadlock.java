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

package com.jpmorrsn.fbp.resourcekit.examples.networks;


import com.jpmorrsn.fbp.core.components.routing.ConcatStreams;
import com.jpmorrsn.fbp.core.components.routing.Discard;
import com.jpmorrsn.fbp.core.components.routing.Passthru;
import com.jpmorrsn.fbp.core.components.routing.Splitter1;
import com.jpmorrsn.fbp.core.engine.Network;
import com.jpmorrsn.fbp.core.components.misc.GenerateTestData;


/** 
 * This network is similar to the one called Deadlock, but you will notice that the port numbers line up!
 * 
 * The additional Passthru's don't make any difference!
 * 
 */

public class NoDeadlock extends Network {
  

  @Override
  protected void define() {
    // component("MONITOR", Monitor.class);
    //tracing = true;

    component("Gen", GenerateTestData.class);
    component("Splitter1", Splitter1.class);
    component("ConcatStreams", ConcatStreams.class);
    component("Passthru", Passthru.class);
   component("Passthru2", Passthru.class);

    connect("Gen.OUT", "Splitter1.IN");
    initialize("1000", component("Gen"), port("COUNT"));

    connect("Splitter1.OUT[0]", "ConcatStreams.IN[0]");

    connect(component("ConcatStreams"), port("OUT"), component("Discard", Discard.class), port("IN"));
    
    connect("Splitter1.OUT[1]", "Passthru.IN");
    connect("Passthru.OUT", "ConcatStreams.IN[1]");

    connect("Splitter1.OUT[2]", "Passthru2.IN");
    connect("Passthru2.OUT", "ConcatStreams.IN[2]");
  }

  public static void main(final String[] argv) throws Exception {

    new NoDeadlock().go();
  }
}
