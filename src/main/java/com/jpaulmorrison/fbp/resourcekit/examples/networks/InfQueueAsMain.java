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


import java.io.File;

import com.jpaulmorrison.fbp.core.components.io.ReadFile;
import com.jpaulmorrison.fbp.core.components.io.WriteFile;
import com.jpaulmorrison.fbp.core.components.misc.WriteToConsole;
import com.jpaulmorrison.fbp.core.engine.Network;
import com.jpaulmorrison.fbp.core.components.misc.GenerateTestData;


public class InfQueueAsMain extends Network {

  /**
   *  This network contains the "infinite queue" as part of the application
   */

  
  @Override
  protected void define() {
    //tracing = true;
    component("Generate", GenerateTestData.class);
    connect(component("Generate"), port("OUT"), component("Write", WriteFile.class), port("IN"));
    initialize("40", component("Generate"), port("COUNT"));
    connect(component("Write"), port("*"), component("Read", ReadFile.class), port("*"));

    initialize("src/main/resources/testdata/temp".replace("/", File.separator), component("Write"), port("DESTINATION"));
    initialize("src/main/resources/testdata/temp".replace("/", File.separator), component("Read"), port("SOURCE"));
    component("Display", WriteToConsole.class);
    connect(component("Read"), port("OUT"), component("Display"), port("IN"));
  }

  public static void main(final String[] argv) throws Exception {
    for (int i = 0; i < 50; i++) {
      new InfQueueAsMain().go();
    }
  }
}
