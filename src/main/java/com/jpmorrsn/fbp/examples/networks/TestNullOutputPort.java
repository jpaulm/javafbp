/* Sven Steinseifer - 2010 */

package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.ReplString;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


public class TestNullOutputPort {

  public static void main(final String[] args) {
    try {
      new Network() {

        @Override
        protected void define() {
          component("generate", GenerateTestData.class);
          component("replicate", ReplString.class);
          component("discard", Discard.class);
          connect("generate.OUT", "replicate.IN");
          connect("replicate.OUT[2]", "discard.IN");
          initialize("100", "generate.COUNT");
        }

      }.go();
    } catch (Exception e) {
      System.err.println("Exception trapped here");
      e.printStackTrace();
    }
  }
}
