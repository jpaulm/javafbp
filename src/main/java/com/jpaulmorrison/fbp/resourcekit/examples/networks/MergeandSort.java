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


import com.jpaulmorrison.fbp.core.engine.Network;
import com.jpaulmorrison.fbp.core.components.swing.ShowText;
import com.jpaulmorrison.fbp.core.components.routing.Sort;
import com.jpaulmorrison.fbp.core.components.misc.GenerateTestData;

/** 
 * Network to Merge and Sort - output is written to Swing pane
 *
 */


public class MergeandSort extends Network {

  String description = "Network to Merge and Sort - output is written to Swing pane";

  @Override
  protected void define() {
    
	//component("_Discard", com.jpaulmorrison.fbp.core.components.routing.Discard.class);
    component("_Write_text_to_pane", ShowText.class);
    component("_Sort", Sort.class);
    component("_Generate_1st_group", GenerateTestData.class);
    component("_Generate_2nd_group", GenerateTestData.class);
    initialize("100 ", component("_Generate_1st_group"), port("COUNT"));
    connect(component("_Generate_2nd_group"), port("OUT"), component("_Sort"), port("IN"));
    connect(component("_Generate_1st_group"), port("OUT"), component("_Sort"), port("IN"));
    //connect(component("_Write_text_to_pane"), port("OUT"), component("_Discard"), port("IN"));
    initialize("Sorted Data", component("_Write_text_to_pane"), port("TITLE"));
    connect(component("_Sort"), port("OUT"), component("_Write_text_to_pane"), port("IN"));
    initialize("50", component("_Generate_2nd_group"), port("COUNT"));

  }

  public static void main(final String[] argv) throws Exception {
    //for (int i = 0; i < 50; i++) {
    new MergeandSort().go();
    //}
  }
}
