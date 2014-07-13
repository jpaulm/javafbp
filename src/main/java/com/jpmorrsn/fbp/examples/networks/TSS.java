package com.jpmorrsn.fbp.examples.networks; // Change as required 


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenSS;


/* This test tests a subnet with stream-sensitive ports, and all the IPs going through the
 * subnet are discarded; only the *SUBEND triggers (should be 3 nulls) are displayed 
 */
public class TSS extends Network {

  static final String copyright = "Copyright 2007, ... 2011";

  @Override
  protected void define() {
    //tracing = true;
    component("Generate", GenSS.class);
    //component("Display (3)", WriteFile.class);
    component("Discard", Discard.class);
    component("Subnet", SubnetX.class);
    component("WTC", WriteToConsole.class);

    connect(component("Generate"), port("OUT"), component("Subnet"), port("IN"));

    initialize("100", component("Generate"), port("COUNT"));
    connect(component("Subnet"), port("OUT"), component("Discard"), port("IN"));

    //initialize("src\\com\\jpmorrsn\\fbp\\test\\data\\output".replace("\\", File.separator),
    //    component("Display (3)"), port("DESTINATION"));
    connect("Subnet.*SUBEND", "WTC.IN");

  }

  public static void main(final String[] argv) throws Exception {
    new TSS().go();
  }
}