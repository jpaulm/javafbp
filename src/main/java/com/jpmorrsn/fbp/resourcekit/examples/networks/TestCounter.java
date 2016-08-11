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


import com.jpmorrsn.fbp.core.components.Counter;
import com.jpmorrsn.fbp.core.components.WriteToConsole;
import com.jpmorrsn.fbp.core.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


public class TestCounter extends Network {

  @Override
  protected void define() {

    connect(component("Generate", GenerateTestData.class), port("OUT"), component("Counter", Counter.class), port("IN"));

    connect(component("Counter"), port("COUNT"), component("Display", WriteToConsole.class), port("IN"));

    initialize("0", component("Generate"), port("COUNT"));

  }

  public static void main(final String[] argv) throws Exception {
    new TestCounter().go();
  }
}
