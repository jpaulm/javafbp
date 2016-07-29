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
package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Copy;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.CopySSt;


public class TestSelfStarting extends Network {

 
  @Override
  protected void define() {
    connect(component("Copy", Copy.class), port("OUT"), component("CopySSt", CopySSt.class), port("IN"));
    connect(component("CopySSt"), port("OUT"), component("Copy"), port("IN"));
  }

  public static void main(final String[] argv) throws Exception {
    new TestSelfStarting().go();
  }
}
