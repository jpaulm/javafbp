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


package com.jpaulmorrison.fbp.core.components.audio;


import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;


/** Generate note/duration pairs for Jingle Bells - 2nd voice
**/
@ComponentDescription("Generate note/duration pairs for Jingle Bells  - 2nd voice")
@OutPort("OUT")
public class JingleBells2 extends Component {

  OutputPort _outport;

  int C4 = 262;

  int D4 = 294;

  int E4 = 330;

  int F4 = 349;

  int G4 = 392;

  int A4 = 440;

  int B4 = 494;

  int notes[] = { G4, G4, G4, G4, G4, G4, G4, G4, D4, D4, G4 };

  int note = 1500;

  int half = note / 2;

  int quarter = note / 4;

  int eighth = note / 8;

  int durations[] = { quarter, quarter, half, quarter, quarter, half, quarter, quarter, quarter + eighth, eighth, half };

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
