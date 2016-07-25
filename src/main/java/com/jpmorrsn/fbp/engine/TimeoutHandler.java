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

package com.jpmorrsn.fbp.engine;


public class TimeoutHandler {
	
	/**
	 * This class is used by the 'longWaitStart' method which can be used by components  
	 *   
	 */

  private long dur;

  private final Component comp;

  public TimeoutHandler(final double intvl, final Component c) {
    
    double ms = intvl * 1000.0 + 500.0;
    dur = new Double(ms).longValue(); // convert to msecs
    comp = c;
    c.timeout = this;
  }

  void dispose(final Component c) {
    synchronized (c.network) {
      c.network.timeouts.remove(c);
    }
    c.timeout = null;
    c.status = Component.StatusValues.ACTIVE;
  }

  void decrement(final long freq) {
    dur -= freq; // reduce by frequency, in msecs
    if (dur < 0) {
      FlowError.complain("Component " + comp.getName() + " timed out");
    }

  }

}
