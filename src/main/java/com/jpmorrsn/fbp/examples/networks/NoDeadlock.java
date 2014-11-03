package com.jpmorrsn.fbp.examples.networks;




import com.jpmorrsn.fbp.components.Concatenate;
import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.Passthru;
import com.jpmorrsn.fbp.components.Splitter1;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


/** This network is similar to the one called Deadlock, but you will notice that the port numbers line up!
 * The additional Passthru's don't make any difference!
 * */

public class NoDeadlock extends Network {

  static final String copyright = "Copyright 1999, 2000, 2001, 2014, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    // component("MONITOR", Monitor.class);
    //tracing = true;

    component("Gen", GenerateTestData.class);
    component("Splitter1", Splitter1.class);
    component("Concatenate", Concatenate.class);
    component("Passthru", Passthru.class);
   component("Passthru2", Passthru.class);

    connect("Gen.OUT", "Splitter1.IN");
    initialize("1000", component("Gen"), port("COUNT"));

    connect("Splitter1.OUT[0]", "Concatenate.IN[0]");

    connect(component("Concatenate"), port("OUT"), component("Discard", Discard.class), port("IN"));
    
    connect("Splitter1.OUT[1]", "Passthru.IN");
    connect("Passthru.OUT", "Concatenate.IN[1]");

    connect("Splitter1.OUT[2]", "Passthru2.IN");
    connect("Passthru2.OUT", "Concatenate.IN[2]");
  }

  public static void main(final String[] argv) throws Exception {

    new NoDeadlock().go();
  }
}
