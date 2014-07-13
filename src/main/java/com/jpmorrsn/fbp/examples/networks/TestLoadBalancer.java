package com.jpmorrsn.fbp.examples.networks; // Change as required 


import com.jpmorrsn.fbp.components.LoadBalance;
import com.jpmorrsn.fbp.components.Passthru;
import com.jpmorrsn.fbp.components.Sort;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


public class TestLoadBalancer {

  public static void main(final String[] args) {
    try {
      new Network() {

        @Override
        protected void define() {
          Runtime runtime = Runtime.getRuntime();
          int nrOfProcessors = runtime.availableProcessors();
          int multiplex_factor = nrOfProcessors * 10;
          component("generate", GenerateTestData.class);
          component("sort", Sort.class);
          component("display", WriteToConsole.class);
          component("lbal", LoadBalance.class);
          connect("generate.OUT", "lbal.IN");
          initialize("5000 ", component("generate"), port("COUNT"));
          for (int i = 0; i < multiplex_factor; i++) {
            connect(component("lbal"), port("OUT", i), component("passthru" + i, Passthru.class), port("IN"));
            connect(component("passthru" + i), port("OUT"), "sort.IN");
          }
          connect("sort.OUT", "display.IN");
        }
      }.go();
    } catch (Exception e) {
      System.err.println("Error:");
      e.printStackTrace();
    }

  }

}
