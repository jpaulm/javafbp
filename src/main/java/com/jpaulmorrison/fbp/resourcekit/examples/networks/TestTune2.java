/*
 * JavaFBP - A Java Implementation of Flow-Based Programming (FBP)
 * Copyright (C) 2009, 2016 J. Paul Morrison
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, see the GNU Library General Public License v3
 * at https://www.gnu.org/licenses/lgpl-3.0.en.html for more details.
 */

package com.jpaulmorrison.fbp.resourcekit.examples.networks;


import com.jpaulmorrison.fbp.core.engine.Network;
import com.jpaulmorrison.fbp.core.components.audio.GenSamples;

/**
 * This is similar to TestTune, but we have added 3 more signals into the SoundMixer component; 
 * also, the tune file has been hard-wired into a component (JingleBells), and 3 copies for other voices
 * 
 *  There is a problem with this: adding the signals together results in higher notes!  Needs to be looked at!
 *  
 */


public class TestTune2 extends Network {

  String description = "This is similar to TestTune, but we have added 3 more signals into the SoundMixer component; "
      + "also, the tune file has been hard-wired into a component (JingleBells), and 3 copies for other voices";

  @Override
  protected void define() {
    //component("_Read_Tune_File", com.jpaulmorrison.fbp.core.components.ReadFile.class);
    //component("_Text_to_Int_Array", com.jpaulmorrison.fbp.core.components.Text2IntArray.class);
    component("JB", com.jpaulmorrison.fbp.core.components.audio.JingleBells.class);
    component("JB2", com.jpaulmorrison.fbp.core.components.audio.JingleBells2.class);
    component("JB3", com.jpaulmorrison.fbp.core.components.audio.JingleBells3.class);
    component("JB4", com.jpaulmorrison.fbp.core.components.audio.JingleBells4.class);
    component("GS", GenSamples.class);
    component("GS2", GenSamples.class);
    component("GS3", GenSamples.class);
    component("GS4", GenSamples.class);
    component("SoundMixer", com.jpaulmorrison.fbp.core.components.audio.SoundMixer.class);
    //component("Display", com.jpaulmorrison.fbp.core.components.misc.WriteToConsole.class);

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
