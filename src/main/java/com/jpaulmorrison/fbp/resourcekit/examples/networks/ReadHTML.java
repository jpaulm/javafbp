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




import com.jpaulmorrison.fbp.core.components.httpurl.LoadURL;
import com.jpaulmorrison.fbp.core.components.misc.WriteObjectsToConsole;
import com.jpaulmorrison.fbp.core.components.parsexml.ParseXML;
import com.jpaulmorrison.fbp.core.engine.Network;

/**
 * Read from URL; write to the console 
 *
 */


public class ReadHTML extends Network {

  @Override
  protected void define() {
   connect(component("Load URL", LoadURL.class), port("OUT"), component("Parse", ParseXML.class), port("IN"), 1);
   initialize("https://jpaulm.github.io/fbp/fbp-inspired-vs-real-fbp.html", component("Load URL"), port("SOURCE"));
   connect(component("Parse"), port("OUT"), component("Write", WriteObjectsToConsole.class), port("IN"), 1);

  }

  public static void main(final String[] argv) throws Exception {
    new ReadHTML().go();
  }
}
