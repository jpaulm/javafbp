package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.Kick;
import com.jpmorrsn.fbp.components.ShowURL;
import com.jpmorrsn.fbp.engine.Network;


public class TryBrowser extends Network {

  /* This does not work from Eclipse - it has to be driven by JWS
   * using a JNLP file, which in turn invokes this network definition.
   * ShowURL drives an HTML file called tryform.htm, which uses JavaScript. 
   *  */

  static final String copyright = "Copyright 2007, 2008, ... 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    connect(component("Kick", Kick.class), port("OUT"), component("ShowURL", ShowURL.class), port("IN"));
    component("Discard", Discard.class);
    connect("ShowURL.OUT", "Discard.IN");
  }

  public static void main(final String[] argv) throws Exception {
    new TryBrowser().go();
  }
}
