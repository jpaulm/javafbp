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

/**
 * Component to assign incoming packets to the output port that has the smallest
 * backlog of packets waiting to be processed.
 * All the IPs in a substream must go to the same output port array element (logic added Aug. 2, 2015)
 */
@ComponentDescription("Sends incoming packets to output array element with smallest backlog")
@OutPort(value = "OUT", arrayPort = true, description = "Packets being output")
@InPort(value = "IN", description = "Incoming packets")
public class LoadBalance extends Component {

	
	private InputPort inport;

	private OutputPort[] outportArray;

	@Override
	protected void execute() {

		int no = outportArray.length;
		int backlog;
		int sel = -1;
		int substream_level = 0;

		Packet p;
		while ((p = inport.receive()) != null) {
			if (substream_level == 0) {
				backlog = Integer.MAX_VALUE;
				for (int i = 0; i < no; i++) {
					int j = outportArray[i].downstreamCount();
					if (j <= backlog) {
						backlog = j;
						sel = i;
					}
				}
				if (getTracing())
				    System.out.println("Port " + sel + " selected; backlog: " + backlog);   // for debugging
			}
			if (p.getType() == Packet.OPEN)
				substream_level ++;
			else if (p.getType() == Packet.CLOSE)
				substream_level --;
			//else 
			//	System.out.println("Data: " + p.getContent());
			
			outportArray[sel].send(p);

		}
	}

	@Override
	protected void openPorts() {

		inport = openInput("IN");

		outportArray = openOutputArray("OUT");

	}
}
