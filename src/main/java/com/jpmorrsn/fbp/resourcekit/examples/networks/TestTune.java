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

package com.jpmorrsn.fbp.resourcekit.examples.networks;


import java.io.File;

import com.jpmorrsn.fbp.core.engine.Network;
import com.jpmorrsn.fbp.core.components.audio.Text2IntArray;


public class TestTune extends Network {

  String description = " ";

  @Override
  protected void define() {
    component("_Read_Tune_File", com.jpmorrsn.fbp.core.components.io.ReadFile.class);
    component("_Text_to_Int_Array", Text2IntArray.class);
    //component("_Play_Tune", com.jpmorrsn.fbp.core.components.PlayTune.class);
    //component("Display", com.jpmorrsn.fbp.core.components.misc.WriteToConsole.class);
    component("GenSamples", com.jpmorrsn.fbp.core.components.audio.GenSamples.class);
    component("SoundMixer", com.jpmorrsn.fbp.core.components.audio.SoundMixer.class);

    connect(component("_Read_Tune_File"), port("OUT"), component("_Text_to_Int_Array"), port("IN"));
    initialize("src/main/resources/testdata/tune.txt".replace("/", File.separator), component("_Read_Tune_File"), port("SOURCE"));
    connect(component("_Text_to_Int_Array"), port("OUT"), component("GenSamples"), port("IN"));
    connect("GenSamples.OUT", "SoundMixer.IN");
    initialize("1", "SoundMixer.GAINS");

  }

  public static void main(final String[] argv) throws Exception {
    new TestTune().go();
  }
}
