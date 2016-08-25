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

package com.jpaulmorrison.fbp.resourcekit.experimental.networks;


import com.jpaulmorrison.fbp.core.components.routing.Discard;
import com.jpaulmorrison.fbp.core.components.routing.Kick;
import com.jpaulmorrison.fbp.resourcekit.experimental.components.ShowURL;
import com.jpaulmorrison.fbp.core.engine.Network;


public class TryBrowser extends Network {

  /**
   *  This does not work from Eclipse - it has to be driven by JWS
   * using a JNLP file, which in turn invokes this network definition.
   * ShowURL drives an HTML file called tryform.htm, which uses JavaScript. 
   *  */

 
  @Override
  protected void define() {
    connect(component("Kick", Kick.class), port("OUT"), component("ShowURL", ShowURL.class), port("IN"));
    component("Discard", Discard.class);
    connect("ShowURL.OUT", "Discard.IN");
  }

  public static void main(final String[] argv) throws Exception {
    new TryBrowser().go();
  }
}
