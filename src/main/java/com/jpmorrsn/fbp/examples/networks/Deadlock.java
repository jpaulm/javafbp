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


import com.jpmorrsn.fbp.components.Concatenate;
import com.jpmorrsn.fbp.components.Discard;

import com.jpmorrsn.fbp.components.ReplString;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


/** This network forces a deadlock condition */

public class Deadlock extends Network {

 
  @Override
  protected void define() {
    //tracing = true;

    connect(component("Gen", GenerateTestData.class), port("OUT"), component("ReplString", ReplString.class), port("IN"));
    initialize("10000", component("Gen"), port("COUNT"));
    connect(component("Concatenate", Concatenate.class), port("OUT"), component("Discard", Discard.class), port("IN"));

    connect(component("ReplString"), port("OUT", 0), component("Concatenate"), port("IN", 0));

    connect(component("ReplString"), port("OUT", 2), component("Concatenate"), port("IN", 1));

    connect(component("ReplString"), port("OUT", 1), component("Concatenate"), port("IN", 2));
  }

  public static void main(final String[] argv) throws Exception {
    new Deadlock().go();
  }
}
