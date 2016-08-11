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

package com.jpmorrsn.fbp.examples.networks; // Change as required 


import com.jpmorrsn.fbp.core.components.Discard;
import com.jpmorrsn.fbp.core.components.WriteToConsole;
import com.jpmorrsn.fbp.core.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenSS;


/**
 *  This test tests a subnet with stream-sensitive ports, and all the IPs going through the
 * subnet are discarded; only the *SUBEND triggers (should be 3 nulls) are displayed 
 * 
 */
public class TSS extends Network {

  static final String copyright = "Copyright 2007, ... 2011";

  @Override
  protected void define() {
    //tracing = true;
    component("Generate", GenSS.class);
    //component("Display (3)", WriteFile.class);
    component("Discard", Discard.class);
    component("Subnet", SubnetX.class);
    component("WTC", WriteToConsole.class);

    connect(component("Generate"), port("OUT"), component("Subnet"), port("IN"));

    initialize("100", component("Generate"), port("COUNT"));
    connect(component("Subnet"), port("OUT"), component("Discard"), port("IN"));

    //initialize("src\\com\\jpmorrsn\\fbp\\test\\data\\output".replace("\\", File.separator),
    //    component("Display (3)"), port("DESTINATION"));
    connect("Subnet.*SUBEND", "WTC.IN");

  }

  public static void main(final String[] argv) throws Exception {
    new TSS().go();
  }
}