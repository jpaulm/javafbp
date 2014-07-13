/* Sven Steinseifer - 2010 */

package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenSS;


public class TestLocks {

  public static void main(final String[] args) {
    try {
      new Network() {

        @Override
        protected void define() {
          component("genSS", GenSS.class);
          component("subnet", SubnetX.class);
          component("discard", Discard.class);
          connect("genSS.OUT", "subnet.IN");
          connect("subnet.OUT", "discard.IN");
          initialize("1000", "genSS.COUNT");
        }
      }.go();

    } catch (Exception e) {
      System.err.println("Exception trapped here");
      e.printStackTrace();
    }
  }
}
