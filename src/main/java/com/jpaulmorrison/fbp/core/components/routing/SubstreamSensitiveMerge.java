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

package com.jpaulmorrison.fbp.core.components.routing;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;

/**
 * First-come, first-served - sensitive to substreams
 **/

@ComponentDescription("Merge multiple input streams, first-come, first-served, but sensitive to substreams")
@InPort(value = "IN", arrayPort = true, description = "Incoming packets")
@OutPort(value = "OUT", description = "Merged output")

public class SubstreamSensitiveMerge extends Component {
	

	
	private InputPort[] inportArray;
	private OutputPort outport;

	@Override
	protected void execute() {

		@SuppressWarnings("rawtypes")
		Packet p = null;
		int i = -1;
		int substream_level = 0;
		while (true) {
			if (substream_level != 0) {
				p = inportArray[i].receive();
				if (p == null)
					break;
			} else {
				while (true) {
					try {
						i = findInputPortElementWithData(inportArray); //"secret" API call
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // will suspend if all elements empty but not drained
					if (i == -1) // all elements are drained
						return;
					p = inportArray[i].receive();
					if (p != null)
						break;
				}
				// p = inportArray[i].receive();
			}
			if (p.getType() == Packet.OPEN)
				substream_level++;
			else if (p.getType() == Packet.CLOSE)
				substream_level--;
			outport.send(p);
		}
	}

	@Override
	protected void openPorts() {

		inportArray = openInputArray("IN");
		outport = openOutput("OUT");

	}
}
