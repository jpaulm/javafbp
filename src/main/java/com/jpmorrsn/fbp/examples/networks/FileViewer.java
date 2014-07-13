/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.networks;


import java.io.File;

import com.jpmorrsn.fbp.components.Inject;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.FileReadLines;
import com.jpmorrsn.fbp.examples.components.Interpret;
import com.jpmorrsn.fbp.examples.components.WriteReadConsole;


public class FileViewer extends Network {

  String description = "http://jpaulmorrison.com/fbp/loop.shtml";

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
    initialize("testdata/persons.csv".replace("/", File.separator), component("Read using_given seek_and lines"),
        port("SOURCE"));

  }

  public static void main(final String[] argv) throws Exception {
    new FileViewer().go();
  }

}
