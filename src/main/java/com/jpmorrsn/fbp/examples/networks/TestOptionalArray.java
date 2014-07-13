/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.test.networks;


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.test.components.GenerateOptionalArray;


public class TestOptionalArray extends Network {

  @Override
  protected void define() {
    component("Generate", GenerateOptionalArray.class);
    component("行動", Discard.class);
    component("Discard2", Discard.class);
    connect("Generate.出", "行動.IN");
    connect(component("Generate"), port("出[2]"), "Discard2.IN");

    initialize("100", component("Generate"), port("COUNT"));
  }

  public static void main(final String[] argv) throws Exception {
    new TestOptionalArray().go();

  }

}
