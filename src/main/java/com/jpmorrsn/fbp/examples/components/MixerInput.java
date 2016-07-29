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

package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;


/** Miscellaneous Audio Mixer Input
**/
@ComponentDescription("Miscellaneous Audio Mixer Input")
@OutPort("OUT")
public class MixerInput extends Component {

 

  OutputPort _outport;

  int notes[] = { 262 };

  //               C 

  int durations[] = { 3500 };

  @Override
  protected void execute() {

    for (int i = 0; i < notes.length; i++) {

      int[] intArray = { notes[i], durations[i] };
      _outport.send(create(intArray));

    }

  }

  @Override
  protected void openPorts() {
    _outport = openOutput("OUT");
  }
}
