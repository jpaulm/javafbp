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

package com.jpmorrsn.fbp.resourcekit.examples.networks; // Change as required 


import com.jpmorrsn.fbp.core.components.routing.LoadBalance;
import com.jpmorrsn.fbp.core.components.routing.Passthru;
import com.jpmorrsn.fbp.core.components.routing.Sort;
import com.jpmorrsn.fbp.core.components.misc.WriteToConsole;
import com.jpmorrsn.fbp.core.engine.Network;
import com.jpmorrsn.fbp.core.components.misc.GenerateTestData;


public class TestLoadBalancer {

  public static void main(final String[] args) {
    try {
      new Network() {

        @Override
        protected void define() {
          Runtime runtime = Runtime.getRuntime();
          int nrOfProcessors = runtime.availableProcessors();
          int multiplex_factor = nrOfProcessors * 10;
          component("generate", GenerateTestData.class);
          component("sort", Sort.class);
          component("display", WriteToConsole.class);
          component("lbal", LoadBalance.class);
          connect("generate.OUT", "lbal.IN");
          initialize("5000 ", component("generate"), port("COUNT"));
          for (int i = 0; i < multiplex_factor; i++) {
            connect(component("lbal"), port("OUT", i), component("passthru" + i, Passthru.class), port("IN"));
            connect(component("passthru" + i), port("OUT"), "sort.IN");
          }
          connect("sort.OUT", "display.IN");
        }
      }.go();
    } catch (Exception e) {
      System.err.println("Error:");
      e.printStackTrace();
    }

  }

}
