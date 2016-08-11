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

package com.jpmorrsn.fbp.examples.networks; // Change as required 


import com.jpmorrsn.fbp.core.components.Passthru;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.SubInSS;
import com.jpmorrsn.fbp.core.engine.SubNet;
import com.jpmorrsn.fbp.core.engine.SubOutSS;

/** 
 * Simple subnet for testing, showing substream sensitivity 
 *  
 */

@OutPort("OUT")
@InPort("IN")
public class SubnetX extends SubNet {

  @Override
  protected void define() {

    component("SUBIN", SubInSS.class);
    component("SUBOUT", SubOutSS.class);
    component("Pass", Passthru.class);

    initialize("IN", component("SUBIN"), port("NAME"));
    connect(component("SUBIN"), port("OUT"), component("Pass"), port("IN"));
    connect(component("Pass"), port("OUT"), component("SUBOUT"), port("IN"));
    initialize("OUT", component("SUBOUT"), port("NAME"));

  }
}