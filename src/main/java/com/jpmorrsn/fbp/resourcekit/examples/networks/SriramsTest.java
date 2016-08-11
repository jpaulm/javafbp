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


import com.jpmorrsn.fbp.core.components.Discard;
import com.jpmorrsn.fbp.core.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


/** 
 * This network is intended for timing runs 
 * 
 */

public class SriramsTest extends Network {

  

  @Override
  protected void define() {
    /*  Trying to get the same number of send/receive pairs */
    component("Discard", Discard.class);
    int lim = 1000;
    for (int i = 0; i < lim; i++) {
      component("Generate " + i, GenerateTestData.class);
      initialize("1000", component("Generate " + i), port("COUNT"));
      connect(component("Generate " + i), port("OUT"), component("Discard"), port("IN"));
    }

  }

  public static void main(final String[] argv) throws Exception {
    new SriramsTest().go();
  }
}
