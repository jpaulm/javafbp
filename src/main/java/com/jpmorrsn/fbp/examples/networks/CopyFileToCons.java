package com.jpmorrsn.fbp.examples.networks;


import java.io.File;

import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.text.DuplicateString;


public class CopyFileToCons extends Network {

  static final String copyright = "Copyright 1999, 2000, 2001, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    connect(component("Read", ReadFile.class), port("OUT"), component("Dup", DuplicateString.class), port("IN"));
    connect(component("Dup"), port("DUPLICATE"), component("Write", WriteToConsole.class), port("IN"));
    connect(component("Dup"), port("OUT"), component("Disc", Discard.class), port("IN"));
    initialize("testdata/testdata.txt".replace("/", File.separator), component("Read"), port("SOURCE"));

  }

  public static void main(final String[] argv) throws Exception {
    new CopyFileToCons().go();
  }
}
