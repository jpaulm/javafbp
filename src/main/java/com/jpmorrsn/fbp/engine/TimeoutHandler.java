package com.jpmorrsn.fbp.engine;


public class TimeoutHandler {

  private long dur;

  private final Component comp;

  public TimeoutHandler(final double intvl, final Component c) {
    /* *
     * Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, 
     * distribute, or make derivative works under the terms of the Clarified Artistic License, 
     * based on the Everything Development Company's Artistic License.  A document describing 
     * this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. 
     * THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.
     * */
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
