package com.jpmorrsn.fbp.examples.networks; // Change as required 


import com.jpmorrsn.fbp.components.DispIPCounts;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateSlowly;


/* This test should not report a deadlock - before Sven's changes to the
 * scheduler, it would have reported a spurious deadlock 
 */

public class TestDeadlockDetection {

  public static void main(final String[] args) {
    try {
      new Network() {

        @Override
        protected void define() {
          component("generate", GenerateSlowly.class);
          component("subD", SubnetD.class);
          component("DispCounts", DispIPCounts.class);

          connect("generate.OUT", "subD.IN", true);
          connect("subD.*", "DispCounts.CLSDN");
          initialize("500", component("DispCounts"), port("INTVL")); // DispIPCounts is SelfStarting
          connect("DispCounts.OUT", component("Display", WriteToConsole.class), port("IN"));

        }
      }.go();
    } catch (Exception e) {
      System.err.println("Exception trapped here");
      e.printStackTrace();
    }

  }

}
