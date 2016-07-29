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


import com.jpmorrsn.fbp.components.ReplString;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;

/** 
 * Generate 2000 test IPs, pass them through Replicate and write them to console
 *  
 *
 */

public class Copy2 extends Network {

 
  @Override
  protected void define() {

    connect(component("Generate", GenerateTestData.class), port("OUT"), component("ReplString", ReplString.class), port("IN"));

    connect(component("ReplString"), port("OUT", 0), component("Write", WriteToConsole.class), port("IN"));

    initialize("2000", component("Generate"), port("COUNT"));

  }

  public static void main(final String[] argv) throws Exception {
    new Copy2().go();
  }
}
