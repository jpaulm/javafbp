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


package com.jpmorrsn.fbp.examples.components;

import java.util.HashMap;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

/**
 * Component to generate word counts
 */
@ComponentDescription("Generate word counts")
@OutPort("OUT")
@InPort("IN")
public class GenerateWordCounts extends Component {


  private InputPort inport;

  private OutputPort outport;

  @SuppressWarnings("unchecked")
@Override
  protected void execute() {
    
    HashMap<String, Integer> hm = new HashMap<String, Integer>();
    int j;
    Packet<String> p;
    while ((p = inport.receive()) != null) {
    	String s = (String) p.getContent();
    	Integer i = hm.get(s);    	
    	if (i == null)
    		j = 0;
    	else 
    		j = i.intValue();
    	j++;
    	hm.put(s, Integer.valueOf(j));   
    	drop(p);        
    }
    
    for (String key : hm.keySet()) {
    	p = create(key + ", " + hm.get(key));    	
    	outport.send(p);    	
    }

  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");
  }
}
