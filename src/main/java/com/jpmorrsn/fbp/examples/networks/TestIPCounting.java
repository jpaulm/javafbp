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


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.engine.DispIPCounts;
import com.jpmorrsn.fbp.components.ReplString;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


public class TestIPCounting extends Network {

  

  @Override
  protected void define() {

    connect(component("DispCounts", DispIPCounts.class), port("OUT"), component("Display", WriteToConsole.class),
        port("IN"));

    connect(component("Generate", GenerateTestData.class), port("OUT"), component("ReplStr", ReplString.class), port("IN"),
        true);
    boolean MONITOR = true;
    connect(component("ReplStr"), port("OUT"), component("Discard", Discard.class), port("IN"), MONITOR);

    connect("Discard.*", "DispCounts.CLSDN");

    initialize("1000000", component("Generate"), port("COUNT"));
    initialize("500", component("DispCounts"), port("INTVL")); // DispIPCounts is MustRun

  }

  public static void main(final String[] argv) throws Exception {
    new TestIPCounting().go();
  }
}
