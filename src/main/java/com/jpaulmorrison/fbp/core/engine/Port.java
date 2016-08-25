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
package com.jpaulmorrison.fbp.core.engine;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Port {

	/**
	 * This class is used optionally in 'connect' invocations in network definitions
	 *   
	 */

  String displayName;

  String name;

  int index;

  Port(final String n, final int i) {

    name = n;
    index = i; // -1 if array port

    if (n.startsWith("*")) {
      displayName = name;
      return;
    }

    String[] sa = {"", ""};
    if (n.contains("[")){
    	sa = n.split("\\[");     	
    }
    else {
    	sa[0] = n;
    	sa[1] = "";
    }
    
    
    //Pattern p = Pattern.compile("^(\\w+)(\\[(\\d+)\\])?$");    
    //Pattern p = Pattern.compile("^([_ \\p{N}\\p{L}]+)(\\[(\\d+)\\])?$");
    Pattern p = Pattern.compile("^([a-zA-Z][\\d|\\-|\\_|.|[a-zA-Z]]*)$"); // Allow hyphen (for Humberto), period (for Tom), underscore
    //    and square brackets    (as per latest DrawFBP)
    
    Matcher m = p.matcher(sa[0]);
    
    if (!m.matches()) {
      FlowError.complain("Invalid port name: " + n);
    }
    String s1 = m.group(1);
    
    
    if (!(sa[1].equals(""))) {
      if (i > -1) {
        FlowError.complain("Cannot specify element number twice: " + n + ", index:" + i);
      }
      if (!(sa[1].endsWith("]")))
    		  FlowError.complain("Invalid port name: " + n);
      Pattern q = Pattern.compile("\\d+");
      sa[1] = sa[1].substring(0,sa[1].length() - 1);
      Matcher u = q.matcher(sa[1]);
      
      if (!u.matches())
    	  FlowError.complain("Invalid port name: " + n);
      
      name = sa[0];
      index = Integer.parseInt(sa[1]);
    }
    if (index == -1) {
      displayName = name;
    } else {
      displayName = String.format("%1$s[%2$d]", name, index);
    }
  }
}
