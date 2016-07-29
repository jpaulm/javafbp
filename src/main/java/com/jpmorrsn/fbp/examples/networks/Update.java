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

package com.jpmorrsn.fbp.examples.networks; // Change as required 


import java.io.File;

import com.jpmorrsn.fbp.components.Collate;
import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;


/** This is really the front end of an Update app - instead of routing the merged stream 
 * to a processing component, we just display it.
 * 
 * @author HP_Administrator
 *
 */
public class Update extends Network {

  @Override
  protected void define() {
    //tracing = true;
    component("Read Master", ReadFile.class);
    component("Read Details", ReadFile.class);
    component("Collate", Collate.class);
    component("Display", WriteToConsole.class);
    connect(component("Read Master"), port("OUT"), component("Collate"), port("IN[0]"));
    connect(component("Read Details"), port("OUT"), component("Collate"), port("IN[1]"));
    connect(component("Collate"), port("OUT"), component("Display"), port("IN"));
    initialize("src/main/resources/testdata/mfile".replace("/", File.separator), component("Read Master"), port("SOURCE"));
    initialize("src/main/resources/testdata/dfile".replace("/", File.separator), component("Read Details"), port("SOURCE"));

    initialize("3, 2, 5", component("Collate"), port("CTLFIELDS"));

  }

  public static void main(final String[] argv) throws Exception {
    new Update().go();
  }

}