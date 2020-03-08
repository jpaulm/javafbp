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
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/**
 * Component to generate arrays of sound samples from note frequencies and times.
 * This component is the "front-end" of PlayTune
 */
@ComponentDescription("Generate arrays of sound samples")
@InPort("IN")
@OutPort("OUT")
// trace if connected
public class GenSamples extends Component {

 

  private InputPort inport;

  private OutputPort outport;

  private int curSlot = 0; // current slot in buffer

  private int x = 0;

  private int bufferSize = 0;

  private byte[] buf = new byte[bufferSize]; //  1 second of play

  @Override
  protected void execute() {

    //String s = getName();

    float sampleRate = 44100; // 44100 samples per sec. - with 16 bit, same as CD

    bufferSize = (int) sampleRate * 2; // 2 bytes per sample
    buf = new byte[bufferSize]; //  1 second of play
    Packet<?> p;

    while ((p = inport.receive()) != null) {

      int[] intArray = (int[]) p.getContent();

      buildNote(intArray[0], intArray[1], sampleRate); // note,  msecs

      drop(p);
    }

    if (curSlot > 0) {
      int k = buf.length;
      for (int i = curSlot; i < k; i++) {
        buf[i] = 0;
      }

      outport.send(create(buf));
    }

  }

  void buildNote(final int note, final int duration, final float sampleRate) { // note frequency, duration in msecs
    double RAD = 2.0 * Math.PI;
    int gap = 25; // gap in msecs
    int samples = (int) (duration * sampleRate / 1000);
    boolean note_ending = false, note_ended = false;
    int savex = 0;

    for (int i = 0; i < samples; i++) {
      if (note == 0) {
        x = 0;
      } else if (!note_ended) {
        x = (int) (32767.0 * Math.sin(RAD * note * i / sampleRate));
        if (note_ending) { // carry on until sign of x changes
          if (Math.signum(x) != Math.signum(savex)) {
            note_ended = true;
            x = savex = 0;
          }
        } else if (i >= samples - gap * sampleRate / 1000) {
          note_ending = true;
          savex = x;
        }
      }

      String s = new Integer(x).toString();
      //traceport.send(create(s));

      /*
      if (x > 32767) {
        x = 32767;
      }
      if (x < -32767) {
        x = -32767;
      }
      */

      buf[curSlot + 1] = (byte) (x % 256);

      buf[curSlot + 0] = (byte) (x / 256);

      curSlot += 2;
      if (curSlot >= buf.length) {

        outport.send(create(buf));
        buf = new byte[bufferSize]; //  1 second of play
        curSlot = 0;
      }
    }

  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");    

  }
}
