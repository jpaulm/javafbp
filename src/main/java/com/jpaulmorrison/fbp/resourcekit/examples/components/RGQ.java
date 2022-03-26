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


import java.util.HashMap;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/** Component to remove unnecessary quotes in GEDCOM files
 */
@ComponentDescription("Remove unnecessary quotes in GEDCOM files")
@InPort("IN")
@OutPort("OUT")
public class RGQ extends Component {

  
  private InputPort inport;
  private OutputPort outport;

  @Override
	protected void execute() {

		Packet p;

		// HashMap<String, String> map = new HashMap<String, String>();
		String str;
		int newPerson = 0;
		String str2;
		while ((p = inport.receive()) != null) {
			str = (String) p.getContent();
			str2 = str;
			if (str.length() >= 6) {
				if (str.substring(0, 1).equals("0")) {
					newPerson = 1;
				}
				if (str.substring(0, 6).equals("1 NAME")) {
					if (newPerson == 1) {
						newPerson = 2;
						if (str.substring(7, 8).equals("\"")) {
							str2 = str.substring(0, 7) + str.substring(8);
							int i = str2.substring(7).indexOf("\"");
							str2 = str2.substring(0, 7) + str2.substring(7, 7 + i) + str2.substring(7 + i + 1);
						}
					}
				}
			}
			Packet p2 = create(str2);
			outport.send(p2);
			drop(p);
		}
	}

  @Override
  protected void openPorts() {

    inport = openInput("IN");
    outport = openOutput("OUT");

  }
}
