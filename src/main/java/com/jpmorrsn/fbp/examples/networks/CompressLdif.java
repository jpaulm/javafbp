package com.jpmorrsn.fbp.examples.networks;


import java.io.File;

import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.StartsWith;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;


public class CompressLdif extends Network {

  static final String copyright = "Copyright 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    connect(component("Read", ReadFile.class), port("OUT"), component("StartsWith", StartsWith.class), port("IN"));
    connect(component("StartsWith"), port("ACC"), component("Write", WriteToConsole.class), port("IN"));
    connect(component("StartsWith"), port("REJ"), component("Discard", Discard.class), port("IN"));
    initialize("testdata/testfile.ldif".replace("/", File.separator), component("Read"), port("SOURCE"));
    initialize("dn:", component("StartsWith"), port("TEST"));

  }

  public static void main(final String[] argv) throws Exception {
    new CompressLdif().go();
  }
}
