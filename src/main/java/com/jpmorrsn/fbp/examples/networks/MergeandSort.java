package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.engine.Network;


public class MergeandSort extends Network {

  String description = "Merge and Sort Network";

  @Override
  protected void define() {
    
	//component("_Discard", com.jpmorrsn.fbp.components.Discard.class);
    component("_Write_text_to_pane", com.jpmorrsn.fbp.components.ShowText.class);
    component("_Sort", com.jpmorrsn.fbp.components.Sort.class);
    component("_Generate_1st_group", com.jpmorrsn.fbp.examples.components.GenerateTestData.class);
    component("_Generate_2nd_group", com.jpmorrsn.fbp.examples.components.GenerateTestData.class);
    initialize("100 ", component("_Generate_1st_group"), port("COUNT"));
    connect(component("_Generate_2nd_group"), port("OUT"), component("_Sort"), port("IN"));
    connect(component("_Generate_1st_group"), port("OUT"), component("_Sort"), port("IN"));
    //connect(component("_Write_text_to_pane"), port("OUT"), component("_Discard"), port("IN"));
    initialize("Sorted Data", component("_Write_text_to_pane"), port("TITLE"));
    connect(component("_Sort"), port("OUT"), component("_Write_text_to_pane"), port("IN"));
    initialize("50", component("_Generate_2nd_group"), port("COUNT"));

  }

  public static void main(final String[] argv) throws Exception {
    //for (int i = 0; i < 50; i++) {
    new MergeandSort().go();
    //}
  }
}
