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


package com.jpaulmorrison.fbp.resourcekit.examples.components;


import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/** Generates stream of 5-packet substreams under control of a counter
*/
@ComponentDescription("Generates stream of 5-packet substreams under control of a counter")
@OutPort("OUT")
@InPort("COUNT")
public class GenSS extends Component {

 
  private OutputPort outport;

  InputPort count;

  @Override
	protected void execute() {
		Packet ctp = count.receive();
		if (ctp == null) {
			return;
		}
		String cti = (String) ctp.getContent();
		cti = cti.trim();
		int ct = 0;
		try {
			ct = Integer.parseInt(cti);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		drop(ctp);
		count.close();

		Packet p = create(Packet.OPEN, "");
		outport.send(p);

		for (int i = 0; i < ct; i++) {

			String s = String.format("%1$06d", ct - i) + "abcd";

			p = create(s);
			outport.send(p);
			if (i < ct - 1) {    // prevent empty bracket pair at end
				if (i % 5 == 5 - 1) {
					p = create(Packet.CLOSE, "");
					outport.send(p);

					p = create(Packet.OPEN, "");
					outport.send(p);
				}
			}
		}
		p = create(Packet.CLOSE, "");
		outport.send(p);
	}

  @Override
  protected void openPorts() {

    outport = openOutput("OUT");
    count = openInput("COUNT");

  }
}
