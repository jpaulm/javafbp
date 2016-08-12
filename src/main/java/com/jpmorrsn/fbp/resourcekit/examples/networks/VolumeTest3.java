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


import com.jpmorrsn.fbp.core.components.routing.Copy;
import com.jpmorrsn.fbp.core.components.routing.Discard;
import com.jpmorrsn.fbp.core.engine.Network;
import com.jpmorrsn.fbp.core.components.misc.GenerateTestData;


/** This network is intended for timing runs */

public class VolumeTest3 extends Network {

  
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
