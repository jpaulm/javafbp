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

package com.jpaulmorrison.fbp.resourcekit.examples.networks;


import com.jpaulmorrison.fbp.core.components.routing.Discard;
import com.jpaulmorrison.fbp.core.components.routing.Passthru;
import com.jpaulmorrison.fbp.core.engine.Network;
import com.jpaulmorrison.fbp.core.components.misc.GenerateTestData;


/** 
 * This network is intended for timing runs
 */

public class TimingTest extends Network {

 
  @Override
  protected void define() {

    /* This network funnels 50 Generates into one Discard component  

    component("Discard", Discard.class);
    for (int i = 0; i < 50; i++) {
      connect(component("Generate" + i, Generate.class), port("OUT"), component("Discard"), port("IN"), 100);
      initialize("20000", component("Generate" + i), port("COUNT"));
    }
     */
	  
    /** This alternative network funnels 50 Generates into 50 Discard components */

    for (int i = 0; i < 50; i++) {
      connect(component("Generate" + i, GenerateTestData.class), port("OUT"), component("Passthru" + i, Passthru.class),
          port("IN"), 5);
      connect(component("Passthru" + i), port("OUT"), component("Discard" + i, Discard.class), port("IN"), 5);
      initialize("20000", component("Generate" + i), port("COUNT"));
    }

  }

  public static void main(final String[] argv) throws Exception {
    new TimingTest().go();
  }
}
