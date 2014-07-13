package com.jpmorrsn.fbp.examples.networks; // Change as required 


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.SubIn;
import com.jpmorrsn.fbp.engine.SubNet;


@InPort("IN")
public class SubnetD extends SubNet {

  /**
   * @see com.jpmorrsn.fbp.engine.Network#define()
   *
   * {@inheritDoc}
   */
  @Override
  protected void define() throws Exception {
    component("SI", SubIn.class);
    component("discard", Discard.class);
    connect("SI.OUT", "discard.IN", true);
    initialize("IN", "SI.NAME");
  }

}
