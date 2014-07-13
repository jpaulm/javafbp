package com.jpmorrsn.fbp.examples.networks; // Change as required 


import com.jpmorrsn.fbp.components.Kick;
import com.jpmorrsn.fbp.engine.Network;


/* This test is to try out Ernesto's "weird" problem
 */

public class ErnMain2 extends Network {

  @Override
  protected void define() {
    component("Test", SN_DamsManager.class);
    component("Kick", Kick.class);
    connect("Kick.OUT", "Test.IN_DBDAMINFO");

  }

  public static void main(final String[] argv) throws Exception {
    new ErnMain2().go();
  }
}
