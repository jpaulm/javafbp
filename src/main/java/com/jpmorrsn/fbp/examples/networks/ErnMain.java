package com.jpmorrsn.fbp.examples.networks; // Change as required 


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.engine.Network;


/* This test is to try out Ernesto's ideas about subnet arrayports - it uses ErnSub
 */

public class ErnMain extends Network {

  @Override
  protected void define() {
    component("DAM_and_Ch_Manifolds", ErnSub.class);
    for (int i = 0; i < 2; i++) {
      component("ChAdapter_" + i, Discard.class);
      component("EventAdapter_" + i, Discard.class);
      connect("DAM_and_Ch_Manifolds.OUTCHINFO_" + i, "ChAdapter_" + i + ".IN");
      connect("DAM_and_Ch_Manifolds.OUTCHDATA_" + i, "EventAdapter_" + i + ".IN");
    }
  }

  public static void main(final String[] argv) throws Throwable {
    new ErnMain().go();
  }

}
