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


import java.io.File;
import com.jpmorrsn.fbp.core.components.routing.Discard;
import com.jpmorrsn.fbp.core.components.io.ReadFile;
import com.jpmorrsn.fbp.core.components.misc.WriteToConsole;
import com.jpmorrsn.fbp.core.engine.Network;
import com.jpmorrsn.fbp.core.components.text.DuplicateString;

/**
 * Read from file; run them through DuplicateString; write one set to the console, and the other st to Discard  * 
 *
 */


public class CopyFileToCons extends Network {

  @Override
  protected void define() {
    connect(component("Read", ReadFile.class), port("OUT"), component("Dup", DuplicateString.class), port("IN"));
    connect(component("Dup"), port("DUPLICATE"), component("Write", WriteToConsole.class), port("IN"));
    connect(component("Dup"), port("OUT"), component("Disc", Discard.class), port("IN"));
    initialize("src/main/resources/testdata/testdata.txt".replace("/", File.separator), component("Read"), port("SOURCE"));

  }

  public static void main(final String[] argv) throws Exception {
    new CopyFileToCons().go();
  }
}
