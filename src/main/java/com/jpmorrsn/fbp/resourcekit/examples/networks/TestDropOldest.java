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


import com.jpmorrsn.fbp.core.components.misc.WriteToConsole;
import com.jpmorrsn.fbp.core.engine.Connection;
import com.jpmorrsn.fbp.core.engine.Network;
import com.jpmorrsn.fbp.core.components.misc.GenerateTestData;
import com.jpmorrsn.fbp.resourcekit.examples.components.SlowPass;


public class TestDropOldest extends Network {

  

  @Override
  protected void define() {

    Connection c = connect(component("Generate", GenerateTestData.class), port("OUT"),
        component("SlowPass", SlowPass.class), port("IN"));
    c.setDropOldest();
    connect(component("SlowPass"), port("OUT"), component("Display", WriteToConsole.class), port("IN"));

    initialize("2000", component("Generate"), port("COUNT"));

  }

  public static void main(final String[] argv) throws Exception {
    new TestDropOldest().go();
  }
}
