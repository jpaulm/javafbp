package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.Passthru;
import com.jpmorrsn.fbp.components.Sort;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


public class MergeSortDrop extends Network {

  String description = "Merge, Sort and Drop Network";

  @Override
  protected void define() {
    component("_Generate", GenerateTestData.class);
    component("_Generate2", GenerateTestData.class);
    component("_Sort", Sort.class);
    component("_Discard", Discard.class);
    component("Passthru", Passthru.class);
    component("Passthru2", Passthru.class);
    connect(component("_Generate2"), port("OUT"), component("Passthru2"), port("IN"));
    connect(component("_Generate"), port("OUT"), component("Passthru"), port("IN"));
    connect("Passthru2.OUT", "Passthru.IN");
    connect("Passthru.OUT", "_Sort.IN");
    initialize("100", component("_Generate"), port("COUNT"));
    initialize("100", component("_Generate2"), port("COUNT"));
    connect(component("_Sort"), port("OUT"), component("_Discard"), port("IN"));

  }

  public static void main(final String[] argv) throws Exception {
    // run test 50 times
    for (int i = 0; i < 50; i++) {
      new MergeSortDrop().go();
    }
  }
}
