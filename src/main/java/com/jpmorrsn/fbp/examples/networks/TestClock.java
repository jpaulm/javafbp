/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.NoFloClock;


public class TestClock extends Network {

  @Override
  protected void define() {

    component("NoFloClock", NoFloClock.class);

  }

  public static void main(final String[] argv) throws Exception {
    new TestClock().go();
  }
}
