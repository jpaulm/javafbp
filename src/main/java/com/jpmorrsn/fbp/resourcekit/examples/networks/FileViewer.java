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

import com.jpmorrsn.fbp.core.components.routing.Inject;
import com.jpmorrsn.fbp.core.engine.Network;
import com.jpmorrsn.fbp.resourcekit.examples.components.FileReadLines;
import com.jpmorrsn.fbp.resourcekit.examples.components.Interpret;
import com.jpmorrsn.fbp.resourcekit.examples.components.WriteReadConsole;


/** 
 * Network executing first Appkata example - not sure if this was ever tested...
 * 
 */
public class FileViewer extends Network {

  String description = "http://jpaulmorrison.com/fbp/loop.shtml";  //  This is a chapter from the 1st ed. on loop-shaped networks

  @Override
  protected void define() {
    component("Interpret", Interpret.class);
    component("Write IN_and MENU_Read user_CMD", WriteReadConsole.class);
    component("Read using_given seek_and lines", FileReadLines.class);
    component("Inject_first seek", Inject.class);
    connect(component("Interpret"), port("OUT"), component("Read using_given seek_and lines"), port("SEEK"));
    initialize("0,4", component("Inject_first seek"), port("CONST"));
    initialize("First, Next, Previous, Last, Jump to, eXit", component("Write IN_and MENU_Read user_CMD"), port("MENU"));
    connect(component("Inject_first seek"), port("OUT"), component("Read using_given seek_and lines"), port("SEEK"));
    connect(component("Write IN_and MENU_Read user_CMD"), port("CMD"), component("Interpret"), port("IN"));
    connect(component("Read using_given seek_and lines"), port("OUT"), component("Write IN_and MENU_Read user_CMD"),
        port("IN"));
    initialize("src/main/resources/testdata/persons.csv".replace("/", File.separator), component("Read using_given seek_and lines"),
        port("SOURCE"));

  }

  public static void main(final String[] argv) throws Exception {
    new FileViewer().go();
  }

}
