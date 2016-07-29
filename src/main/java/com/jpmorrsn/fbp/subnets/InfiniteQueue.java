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

package com.jpmorrsn.fbp.subnets;


/*
 */
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.SubIn;
import com.jpmorrsn.fbp.engine.SubNet;
import com.jpmorrsn.fbp.engine.SubOut;


@ComponentDescription("Component to store large number of packets temporarily on disk")
@InPorts({ @InPort("IN"), @InPort("TEMPFILENAME") })
@OutPort("OUT")
public class InfiniteQueue extends SubNet {

  String description = " Infinite Queue";

  @Override
  protected void define() {
    component("__  Write", com.jpmorrsn.fbp.components.WriteFile.class);
    component("__  Read_", com.jpmorrsn.fbp.components.ReadFile.class);
    component("SUBOUT", SubOut.class);
    initialize("OUT", component("SUBOUT"), port("NAME"));
    component("SUBIN", SubIn.class);
    initialize("IN", component("SUBIN"), port("NAME"));
    component("SUBIN_2", SubIn.class);
    initialize("TEMPFILENAME", component("SUBIN_2"), port("NAME"));
    component("_ Replicate", com.jpmorrsn.fbp.components.ReplString.class);
    connect(component("SUBIN"), port("OUT"), component("__  Write"), port("IN"));
    connect(component("SUBIN_2"), port("OUT"), component("_ Replicate"), port("IN"));
    connect(component("_ Replicate"), port("OUT[0]"), component("__  Write"), port("DESTINATION"));
    connect(component("_ Replicate"), port("OUT[1]"), component("__  Read_"), port("SOURCE"));
    connect(component("__  Read_"), port("OUT"), component("SUBOUT"), port("IN"));
    connect(component("__  Write"), port("*"), component("__  Read_"), port("*"));
  }

}
