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


import com.jpmorrsn.fbp.core.engine.Network;
import com.jpmorrsn.fbp.core.components.misc.GenerateTestData;
import com.jpmorrsn.fbp.resourcekit.examples.subnets.InfiniteQueue;
import com.jpmorrsn.fbp.core.components.misc.WriteToConsole;


public class TestInfiniteQueue extends Network {

  String description = "Test Infinite Queue";

  @Override
  protected void define() {
    component("__ Generate", GenerateTestData.class);
    component("_ Infinite_  Queue", InfiniteQueue.class);
    component("__  Display", WriteToConsole.class);
    connect(component("_ Infinite_  Queue"), port("OUT"), component("__  Display"), port("IN"));
    initialize("100", component("__ Generate"), port("COUNT"));
    connect(component("__ Generate"), port("OUT"), component("_ Infinite_  Queue"), port("IN"));
    initialize("temp.data", component("_ Infinite_  Queue"), port("TEMPFILENAME"));

  }

  public static void main(final String[] argv) throws Exception {
    // try this test 50 times!
    for (int i = 0; i < 50; i++) {
      new TestInfiniteQueue().go();
    }
  }

}
