package com.jpmorrsn.fbp.examples.networks;


import java.io.File;

import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.WriteFile;
import com.jpmorrsn.fbp.engine.Network;


public class FileCopy extends Network {

  static final String copyright = "Copyright 1999, 2000, 2001, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    connect(component("Read", ReadFile.class), port("OUT"), component("Write", WriteFile.class), port("IN"));
    initialize("testdata/testdata.txt".replace("/", File.separator), component("Read"), port("SOURCE"));
    initialize("testdata/output".replace("/", File.separator), component("Write"), port("DESTINATION"));
  }

  public static void main(final String[] argv) throws Throwable {
    new FileCopy().go();
  }
}
