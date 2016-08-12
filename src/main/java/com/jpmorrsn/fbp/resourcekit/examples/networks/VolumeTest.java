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

public class VolumeTest extends Network {


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
