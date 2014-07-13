package com.jpmorrsn.fbp.examples.networks;


import java.io.File;

import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.Text2IntArray;


public class TestTune extends Network {

  String description = " ";

  @Override
  protected void define() {
    component("_Read_Tune_File", com.jpmorrsn.fbp.components.ReadFile.class);
    component("_Text_to_Int_Array", Text2IntArray.class);
    //component("_Play_Tune", com.jpmorrsn.fbp.components.PlayTune.class);
    component("Display", com.jpmorrsn.fbp.components.WriteToConsole.class);
    component("GenSamples", com.jpmorrsn.fbp.components.GenSamples.class);
    component("SoundMixer", com.jpmorrsn.fbp.components.SoundMixer.class);

    connect(component("_Read_Tune_File"), port("OUT"), component("_Text_to_Int_Array"), port("IN"));
    initialize("testdata/tune.txt".replace("/", File.separator), component("_Read_Tune_File"), port("SOURCE"));
    connect(component("_Text_to_Int_Array"), port("OUT"), component("GenSamples"), port("IN"));
    connect("GenSamples.OUT", "SoundMixer.IN");

  }

  public static void main(final String[] argv) throws Exception {
    new TestTune().go();
  }
}
