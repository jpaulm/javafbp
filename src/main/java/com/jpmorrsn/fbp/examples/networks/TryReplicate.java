package com.jpmorrsn.fbp.examples.networks;


import java.io.File;

import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.ReplString;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;


public class TryReplicate extends Network {

  static final String copyright = "Copyright 2007, 2008, ... 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    connect(component("Read", ReadFile.class), port("OUT"), component("Replicate", ReplString.class), port("IN"));
    initialize("testdata/testdata.txt".replace("/", File.separator), component("Read"), port("SOURCE"));

    connect(component("Replicate"), port("OUT[0]"), component("Write1", WriteToConsole.class), port("IN"));

    connect(component("Replicate"), port("OUT[1]"), component("Write2", WriteToConsole.class), port("IN"));

    connect(component("Replicate"), port("OUT[2]"), component("Write3", WriteToConsole.class), port("IN"));

  }

  public static void main(final String[] argv) throws Exception {
    new TryReplicate().go();
  }
}
