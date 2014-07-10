package com.jpmorrsn.fbp.engine;


import java.util.LinkedList;


/** This is a package-private class which is just used to hold
* chains attached to Packets.  There are no methods here,
* since all the work is being done in class Packet.
* This could have been an inner class of Packet.
*/

final class Chain {

  /* *
     * Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, 
     * distribute, or make derivative works under the terms of the Clarified Artistic License, 
     * based on the Everything Development Company's Artistic License.  A document describing 
     * this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. 
     * THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.
     * */

  //private Packet head;

  final LinkedList<Packet> members;

  protected final String name;

  Chain(final String n) {
    name = n;
    members = new LinkedList<Packet>();
  }
}
