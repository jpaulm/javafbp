package com.jpmorrsn.fbp.engine;


/**
 * Instances of this class are thrown whenever a programming error
 * in a flow network is detected.  Nobody is expected to catch these,
 * because they are considered indications of design errors, even though
 * detected only at run-time.
 **/

public class FlowError extends RuntimeException {

  /**
   * Constructs a new FlowError with a useful (but non-localized)
   * message as its text.  FlowErrors without texts are not allowed, as
   * these are considered bad practice.
   * @param text a description of the error
   **/

  static final long serialVersionUID = 362498820763181265L;

  /* *
   * Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, 
   * distribute, or make derivative works under the terms of the Clarified Artistic License, 
   * based on the Everything Development Company's Artistic License.  A document describing 
   * this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. 
   * THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.
   * */

  FlowError(final String text) {
    super(text);
  }

  /**
   * A convenience method which constructs a new FlowError and
   * throws it at once, typically never returning.
   * @param text a description of the error
   **/

  public static void complain(final String text) {
    throw new FlowError(text);
  }
}
