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
package com.jpmorrsn.fbp.resourcekit.examples.components;


import java.util.Random;

import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/**
 * Pass a stream of packets to an output stream... slowly!
 * This is like Passthru, but adds a random delay between the receive and send for each IP - the interval should
 * range between 0 and 126 milliseconds (inclusive)
 */
@ComponentDescription("Pass a stream of packets to an output stream... slowly!")
@OutPort("OUT")
@InPort("IN")
public class SlowPass extends Component {

 

  private InputPort inport;

  private OutputPort outport;

	@Override
	protected void execute() {

		Packet p;

		Random rnd = new Random();

		while (null != (p = inport.receive())) {

			try {
				long intvl = (long) rnd.nextInt(500);
				sleep(intvl);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			outport.send(p);
		}
	}

  @Override
  protected void openPorts() {

    inport = openInput("IN");

    outport = openOutput("OUT");

  }
}
