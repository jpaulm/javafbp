/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 * Contributed by Bob Corrick - Feb., 2012 - for the AppKatas exercise on the FBP Google Group
 */
package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.Interpret;
import com.jpmorrsn.fbp.examples.components.WriteReadConsole;


/* Written by Bob Corrick - Feb. 2012, for the AppKatas exercise 
 * 
 */

public class WriteReadInterpret extends Network {

  String description = "http://jpaulmorrison.com/fbp/loop.shtml";

  @Override
  protected void define() {
    component("Interpret", Interpret.class);
    component("Write IN_and MENU_Read user_CMD", WriteReadConsole.class);
    component("Start", com.jpmorrsn.fbp.components.Kick.class);
    connect(component("Start"), port("OUT"), component("Write IN_and MENU_Read user_CMD"), port("IN"));
    initialize("First, Next, Previous, Last, Jump to, eXit", component("Write IN_and MENU_Read user_CMD"), port("MENU"));
    connect(component("Write IN_and MENU_Read user_CMD"), port("CMD"), component("Interpret"), port("IN"));
    connect(component("Interpret"), port("OUT"), component("Write IN_and MENU_Read user_CMD"), port("IN"));

  }

  public static void main(final String[] argv) throws Exception {
    new WriteReadInterpret().go();
  }

}
