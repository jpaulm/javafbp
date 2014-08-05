package com.jpmorrsn.fbp.engine;


public class NullOutputPort extends OutputPort

{

  /***************************************************************************
   * Copyright 2007, 2012, J. Paul Morrison. At your option, you may copy,
   * distribute, or make derivative works under the terms of the Clarified
   * Artistic License, based on the Everything Development Company's Artistic
   * License. A document describing this License may be found at
   * http://www.jpaulmorrison.com/fbp/artistic2.htm. THERE IS NO WARRANTY; USE
   * THIS PRODUCT AT YOUR OWN RISK.
   */

  //String name;
  //Component sender;
  // The send function.
  @Override
  public void send(final Packet packet) {
    /*sender.drop(packet);*/
	 // do nothing - experimental 
  }

  @Override
  public void close() {
    // do nothing
  }

  /**
   * This method returns true if the output port is connected
   * 
   * @return boolean
   */
  @Override
  public boolean isConnected() {
    return false;
  }

}
