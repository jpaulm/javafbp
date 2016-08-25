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


import com.jpaulmorrison.fbp.core.components.routing.Discard;
import com.jpaulmorrison.fbp.core.engine.Network;
import com.jpaulmorrison.fbp.resourcekit.examples.components.GenerateFixedSizeArray;


public class TestFixedSizeArray extends Network {

  @Override
  protected void define() {
    component("Generate", GenerateFixedSizeArray.class);
    component("Discard0", Discard.class);
    component("Discard1", Discard.class);
    connect("Generate.OUT[0]", "Discard0.IN");
    connect("Generate.OUT[1]", "Discard1.IN");

    initialize("100", component("Generate"), port("COUNT"));
  }

  public static void main(final String[] argv) throws Exception {
    new TestFixedSizeArray().go();

  }

}
