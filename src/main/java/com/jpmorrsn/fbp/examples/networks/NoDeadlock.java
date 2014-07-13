package com.jpmorrsn.fbp.examples.networks;


import java.io.File;

import com.jpmorrsn.fbp.components.Concatenate;
import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.Passthru;
import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.Splitter1;
import com.jpmorrsn.fbp.engine.Network;


/** This network is similar to the one called Deadlock, but has been modified slightly 
so that it doesn't deadlock - or maybe the other way round */

public class NoDeadlock extends Network {

  static final String copyright = "Copyright 1999, 2000, 2001, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    // component("MONITOR", Monitor.class);
    //tracing = true;

    component("Read", ReadFile.class);
    component("Splitter1", Splitter1.class);
    component("Concatenate", Concatenate.class);
    component("Passthru", Passthru.class);
    component("Passthru2", Passthru.class);

    connect("Read.OUT", "Splitter1.IN");
    initialize("testdata/testdata.txt".replace("/", File.separator), component("Read"), port("SOURCE"));

    connect("Splitter1.OUT[0]", "Concatenate.IN[0]");

    connect(component("Concatenate"), port("OUT"), component("Discard", Discard.class), port("IN"));

    connect(component("Splitter1"), port("OUT", 1), component("Passthru"), port("IN"));
    connect(component("Passthru"), port("OUT"), component("Concatenate"), port("IN[1]"));

    connect(component("Splitter1"), port("OUT", 2), component("Passthru2"), port("IN"));

    connect(component("Passthru2"), port("OUT"), component("Concatenate"), port("IN", 2));
  }

  public static void main(final String[] argv) throws Exception {

    new NoDeadlock().go();
  }
}
