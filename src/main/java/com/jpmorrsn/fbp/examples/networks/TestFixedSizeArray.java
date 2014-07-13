/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateFixedSizeArray;


public class TestFixedSizeArray extends Network {

  @Override
  protected void define() {
    component("Generate", GenerateFixedSizeArray.class);
    component("Discard0", Discard.class);
    component("Discard1", Discard.class);
    connect("Generate.OUT[0]", "Discard0.IN");
    connect("Generate.OUT[1]", "Discard1.IN");

    initialize("100", component("Generate"), port("COUNT"));
  }

  public static void main(final String[] argv) throws Exception {
    new TestFixedSizeArray().go();

  }

}
