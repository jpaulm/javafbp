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
  
 * Contributed by Bob Corrick - Feb., 2012 - for the AppKatas exercise on the FBP Google Group
 */
package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.Interpret;
import com.jpmorrsn.fbp.examples.components.WriteReadConsole;


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
