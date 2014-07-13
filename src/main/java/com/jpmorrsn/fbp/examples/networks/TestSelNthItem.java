package com.jpmorrsn.fbp.examples.networks;


import java.io.File;

import com.jpmorrsn.fbp.engine.Network;


public class TestSelNthItem extends Network {

  //String description = "Select record by number";

  @Override
  protected void define() {
    component("Discard", com.jpmorrsn.fbp.components.Discard.class);
    component("Write", com.jpmorrsn.fbp.components.WriteFile.class);
    component("Select", com.jpmorrsn.fbp.components.SelNthItem.class);
    component("Read", com.jpmorrsn.fbp.components.ReadFile.class);

    connect("Read.OUT", "Select.IN");
    connect("Select.ACC", "Write.IN");
    connect("Select.REJ", "Discard.IN");

    // The selected number is zero based: 11 will result in twelfth record
    initialize("11", component("Select"), port("NUMBER"));
    initialize("testdata/21lines.txt".replace("/", File.separator), component("Read"), port("SOURCE"));
    initialize("testdata/output".replace("/", File.separator), component("Write"), port("DESTINATION"));
  }

  public static void main(final String[] argv) throws Exception {
    new TestSelNthItem().go();
  }
}
