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

package com.jpmorrsn.fbp.resourcekit.experimental.networks;

import com.jpmorrsn.fbp.core.components.WriteToConsole;
import com.jpmorrsn.fbp.core.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;
import com.jpmorrsn.fbp.examples.components.TabulaRasa;


public class CopyTR extends Network {

	/** 
	 *  Run network using (experimental) TabulaRasa component
	 *
	 */
  
  @Override
  protected void define() {

    connect(component("Generate", GenerateTestData.class), port("OUT"), component("TabulaRasa", TabulaRasa.class), port("IN"));

    connect(component("TabulaRasa"), port("OUT"), component("Write", WriteToConsole.class), port("IN"));

    initialize("2000", component("Generate"), port("COUNT"));
    
    initialize("com.jpmorrsn.fbp.core.components.Copy", component("TabulaRasa"), port("COMP"));

  }

  public static void main(final String[] argv) throws Exception {
    new CopyTR().go();
  }
}
