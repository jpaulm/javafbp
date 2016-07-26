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

package com.jpmorrsn.fbp.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


	
	@ComponentDescription("Merge multiple input streams, following Round Robin system")
	@OutPort(value = "IN", arrayPort = true, description = "Incoming packets")
	@InPort(value = "OUT", description = "Merged output")
	
	public class RRMerge extends Component {
		/** "Round Robin" Merge - merges an IP from element 0, then one from 1, then one from 2, and so on until
		 * it cycles back to 0, and so on until the first end of stream 
		 * 
		 * This component will just terminate when first receive gets end of stream
		 * 
		 * The assumption is that all input streams have the same number of IPs
		**/

	  
	  
	  private InputPort[] inportArray;
	  private OutputPort outport;

	  @Override
	  protected void execute() {

	    int no = inportArray.length;
	    Packet p;   
	    for (int i = 0; i < no; i++) {
	    	if (null == (p = inportArray[i].receive()))
	    		return;
	    	outport.send(p);
	    }

	  }
	  @Override
	  protected void openPorts() {

	    inportArray = openInputArray("IN");
	    outport = openOutput("OUT");

	  }
	}


