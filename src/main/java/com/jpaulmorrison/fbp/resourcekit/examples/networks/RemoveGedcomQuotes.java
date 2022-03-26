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


import com.jpaulmorrison.fbp.core.components.io.ReadFile;
import com.jpaulmorrison.fbp.core.components.io.WriteFile;
import com.jpaulmorrison.fbp.core.engine.Network;
import com.jpaulmorrison.fbp.resourcekit.examples.components.RGQ;

/** 
 * Read file; write to other file
 * 
 */
public class RemoveGedcomQuotes extends Network {
  

  @Override
  protected void define() {
    connect(component("Read", ReadFile.class), port("OUT"), component("RGQ", RGQ.class), port("IN"));
    connect(component("RGQ"), port("OUT"), component("Write", WriteFile.class), port("IN"));
    initialize("C:\\Users\\Paul\\Documents\\NonBusiness\\GeneWeb\\GeneWeb-4.09 Win\\gw\\a.ged", component("Read"), port("SOURCE"));
    initialize("C:\\Users\\Paul\\Documents\\NonBusiness\\GeneWeb\\GeneWeb-4.09 Win\\gw\\a2.ged", component("Write"), port("DESTINATION"));
  }

  public static void main(final String[] argv) throws Throwable {
    new RemoveGedcomQuotes().go();
  }
}
