package com.jpmorrsn.fbp.engine;


/**
 * A clone of java.util.Comparator from JDK 1.2.
 * @see java.util.Comparator
 **/

public interface Comparator {

  /**
   * @param first object to be compared
   * @param second object to be compared
   * @return -1 if first lt second, 0 if first == second, 1 if first gt second
   **/

  /* *
     * Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, 
     * distribute, or make derivative works under the terms of the Clarified Artistic License, 
     * based on the Everything Development Company's Artistic License.  A document describing 
     * this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. 
     * THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.
     * */
  int compareTo(Object first, Object second);
}
