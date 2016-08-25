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

import com.jpaulmorrison.fbp.core.engine.Network;


public class TestSelNthItem extends Network {

  //String description = "Select record by number";

  @Override
  protected void define() {
    component("Discard", com.jpaulmorrison.fbp.core.components.routing.Discard.class);
    component("Write", com.jpaulmorrison.fbp.core.components.io.WriteFile.class);
    component("Select", com.jpaulmorrison.fbp.core.components.routing.SelNthItem.class);
    component("Read", com.jpaulmorrison.fbp.core.components.io.ReadFile.class);

    connect("Read.OUT", "Select.IN");
    connect("Select.ACC", "Write.IN");
    connect("Select.REJ", "Discard.IN");

    // The selected number is zero based: 11 will result in twelfth record
    initialize("11", component("Select"), port("NUMBER"));
    initialize("src/main/resources/testdata/21lines.txt".replace("/", File.separator), component("Read"), port("SOURCE"));
    initialize("src/main/resources/testdata/output".replace("/", File.separator), component("Write"), port("DESTINATION"));
  }

  public static void main(final String[] argv) throws Exception {
    new TestSelNthItem().go();
  }
}
