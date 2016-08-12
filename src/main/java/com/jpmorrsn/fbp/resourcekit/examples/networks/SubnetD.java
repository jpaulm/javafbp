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

package com.jpmorrsn.fbp.resourcekit.examples.networks; // Change as required 


import com.jpmorrsn.fbp.core.components.routing.Discard;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.SubIn;
import com.jpmorrsn.fbp.core.engine.SubNet;

/**
 * Simple subnet for testing  
 *
 */
@InPort("IN")
public class SubnetD extends SubNet {
 
  @Override
  protected void define() throws Exception {
    component("SI", SubIn.class);
    component("discard", Discard.class);
    connect("SI.OUT", "discard.IN", true);
    initialize("IN", "SI.NAME");
  }

}
