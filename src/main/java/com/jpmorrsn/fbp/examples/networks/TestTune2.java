package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.engine.Network;


public class TestTune2 extends Network {

  String description = "This is similar to TestTune, but we have added 3 more signals into the SoundMixer component; "
      + "also, the tune file has been hard-wired into a component (JingleBells), and 3 copies for other voices";

  @Override
  protected void define() {
    //component("_Read_Tune_File", com.jpmorrsn.fbp.components.ReadFile.class);
    //component("_Text_to_Int_Array", com.jpmorrsn.fbp.components.Text2IntArray.class);
    component("JB", com.jpmorrsn.fbp.examples.components.JingleBells.class);
    component("JB2", com.jpmorrsn.fbp.examples.components.JingleBells2.class);
    component("JB3", com.jpmorrsn.fbp.examples.components.JingleBells3.class);
    component("JB4", com.jpmorrsn.fbp.examples.components.JingleBells4.class);
    component("GS", com.jpmorrsn.fbp.components.GenSamples.class);
    component("GS2", com.jpmorrsn.fbp.components.GenSamples.class);
    component("GS3", com.jpmorrsn.fbp.components.GenSamples.class);
    component("GS4", com.jpmorrsn.fbp.components.GenSamples.class);
    component("SoundMixer", com.jpmorrsn.fbp.components.SoundMixer.class);
    component("Display", com.jpmorrsn.fbp.components.WriteToConsole.class);

    connect(component("JB"), port("OUT"), component("GS"), port("IN"));
    connect(component("JB2"), port("OUT"), component("GS2"), port("IN"));
    connect(component("JB3"), port("OUT"), component("GS3"), port("IN"));
    connect(component("JB4"), port("OUT"), component("GS4"), port("IN"));
    connect("GS.OUT", "SoundMixer.IN");
    connect("GS2.OUT", "SoundMixer.IN[1]");
    connect("GS3.OUT", "SoundMixer.IN[2]");
    connect("GS4.OUT", "SoundMixer.IN[3]");
    initialize("6, 3, 2, 1", "SoundMixer.GAINS");

  }

  public static void main(final String[] argv) throws Exception {
    new TestTune2().go();
  }
}
