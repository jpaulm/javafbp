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
import com.jpaulmorrison.fbp.core.components.routing.Sort;
import com.jpaulmorrison.fbp.core.engine.Network;
import com.jpaulmorrison.fbp.core.components.misc.GenerateTestData;

/** 
 * Network to Merge, Sort and Drop
 *  
 */

public class MergeSortDrop extends Network {

  String description = "Network to Merge, Sort and Drop";

  @Override
  protected void define() {
    component("_Generate", GenerateTestData.class);
    component("_Generate2", GenerateTestData.class);
    component("_Sort", Sort.class);
    component("_Discard", Discard.class);
    component("Passthru", Passthru.class);
    component("Passthru2", Passthru.class);
    connect(component("_Generate2"), port("OUT"), component("Passthru2"), port("IN"));
    connect(component("_Generate"), port("OUT"), component("Passthru"), port("IN"));
    connect("Passthru2.OUT", "Passthru.IN");
    connect("Passthru.OUT", "_Sort.IN");
    initialize("100", component("_Generate"), port("COUNT"));
    initialize("100", component("_Generate2"), port("COUNT"));
    connect(component("_Sort"), port("OUT"), component("_Discard"), port("IN"));

  }

  public static void main(final String[] argv) throws Exception {
    // run test 50 times
    for (int i = 0; i < 50; i++) {
      new MergeSortDrop().go();
    }
  }
}
