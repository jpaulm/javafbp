/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2008, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.engine;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Port {

  /* *
   * Copyright 2007,..., 2012, J. Paul Morrison.  At your option, you may copy, 
   * distribute, or make derivative works under the terms of the Clarified Artistic License, 
   * based on the Everything Development Company's Artistic License.  A document describing 
   * this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. 
   * THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.
   * */

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
