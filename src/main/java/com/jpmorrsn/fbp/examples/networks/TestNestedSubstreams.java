/* Sven Steinseifer - 2010 */

package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Output;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenSubStreams;


public class TestNestedSubstreams {

  public static void main(final String[] args) {
    try {
      new Network() {

        @Override
        protected void define() {
          component("generate", GenSubStreams.class);
          component("subnet", SubnetX.class);
          component("output", Output.class);
          connect("generate.OUT", "subnet.IN");
          connect("subnet.OUT", "output.IN");
        }
      }.go();

    } catch (Exception e) {
      System.err.println("Exception trapped here");
      e.printStackTrace();
    }
  }
}
