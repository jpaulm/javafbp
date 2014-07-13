package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.DispIPCounts;
import com.jpmorrsn.fbp.components.ReplString;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


public class TestIPCounting extends Network {

  static final String copyright = "Copyright 2007, 2008, ..., 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {

    connect(component("DispCounts", DispIPCounts.class), port("OUT"), component("Display", WriteToConsole.class),
        port("IN"));

    connect(component("Generate", GenerateTestData.class), port("OUT"), component("ReplStr", ReplString.class), port("IN"),
        true);
    boolean MONITOR = true;
    connect(component("ReplStr"), port("OUT"), component("Discard", Discard.class), port("IN"), MONITOR);

    connect("Discard.*", "DispCounts.CLSDN");

    initialize("1000000", component("Generate"), port("COUNT"));
    initialize("500", component("DispCounts"), port("INTVL")); // DispIPCounts is MustRun

  }

  public static void main(final String[] argv) throws Exception {
    new TestIPCounting().go();
  }
}
