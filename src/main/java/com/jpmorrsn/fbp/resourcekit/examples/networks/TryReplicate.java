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


import java.io.File;

import com.jpmorrsn.fbp.core.components.ReadFile;
import com.jpmorrsn.fbp.core.components.ReplString;
import com.jpmorrsn.fbp.core.components.WriteToConsole;
import com.jpmorrsn.fbp.core.engine.Network;


public class TryReplicate extends Network {

 
  @Override
  protected void define() {
    connect(component("Read", ReadFile.class), port("OUT"), component("Replicate", ReplString.class), port("IN"));
    initialize("src/main/resources/testdata/testdata.txt".replace("/", File.separator), component("Read"), port("SOURCE"));

    connect(component("Replicate"), port("OUT[0]"), component("Write1", WriteToConsole.class), port("IN"));

    connect(component("Replicate"), port("OUT[1]"), component("Write2", WriteToConsole.class), port("IN"));

    connect(component("Replicate"), port("OUT[2]"), component("Write3", WriteToConsole.class), port("IN"));

  }

  public static void main(final String[] argv) throws Exception {
    new TryReplicate().go();
  }
}
