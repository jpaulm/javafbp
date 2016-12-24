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


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InPorts;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.MustRun;
import com.jpaulmorrison.fbp.core.engine.Packet;
import com.jpaulmorrison.fbp.core.engine.Priority;


/**
 * Component to play sequence of 1-second sound buffers - this component is the "front-end" of PlayTune
 */
@ComponentDescription("Play sequence of 1-second sound buffers")
@InPorts({ @InPort(value = "IN", arrayPort = true), @InPort("GAINS") })
@MustRun
@Priority(Thread.MAX_PRIORITY)
// to ensure component is not held up by other processing
public class SoundMixer extends Component {

  
  private InputPort[] inport;

  private InputPort gainsport;

  private SourceDataLine source = null;

  @Override
  protected void execute() {
    float[] gains = new float[inport.length];
    Packet gtp = gainsport.receive();
    gainsport.close();
    if (gtp == null) {
      for (int i = 0; i < gains.length; i++) {
        gains[i] = (int) (1.0 / gains.length);
      }
    } else {
      String gs = (String) gtp.getContent();
      buildGains(gs, gains);
      drop(gtp);
    }
    AudioFormat af = null;
    float sampleRate = 44100; // 44100 samples per sec. - with 16 bit, same as CD
    try {
      af = new AudioFormat(sampleRate, 16, 1, true, true); // linear PCM encoding, 1 channel, signed, bigEndian
      DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
      source = (SourceDataLine) AudioSystem.getLine(info);
      source.open(af);
      source.start();
    } catch (Exception e) {
      System.out.println(e);
    }

    Packet p = null;

    while (true) {
      byte[] mainbuf = null;
      int j = 0;
      
      // receive from each element then get next set
      
      for (InputPort element : inport) {
        p = element.receive();
        if (p == null) {
          break;
        }
        byte[] pbuf = (byte[]) p.getContent();
        if (mainbuf == null) {
          mainbuf = new byte[pbuf.length];
          for (int i = 0; i < mainbuf.length; i += 2) {
            mainbuf[i] = 0;
            mainbuf[i + 1] = 0;
          }
        }
        for (int i = 0; i < pbuf.length; i += 2) {
          int x = mainbuf[i] * 256 + mainbuf[i + 1];
          float y = pbuf[i] * 256 + pbuf[i + 1];
          y = y * gains[j]; // adjust by gain
          x += y;
          if (x > 32767) { // safeguard
            x = 32767;
          }

          if (x < -32767) {
            x = -32767;
          }
          mainbuf[i] = (byte) (x / 256);
          mainbuf[i + 1] = (byte) (x % 256);

        }

        drop(p);
        j++;
      }

      if (p == null) {
        break;
      }

      try {
        source.write(mainbuf, 0, mainbuf.length);
        mainbuf = null;
      } catch (Exception e) {
        System.out.println(e);
      }
    }

    try {
      source.drain();
      source.stop();
      source.close();
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  void buildGains(final String s, final float[] fa) {
    int fromIndex = 0;
    int g = 0;
    float tf = 0;
    while (true) {
      int i = s.indexOf(",", fromIndex);
      String is = null;
      if (i == -1) {
        is = s.substring(fromIndex);
      } else {
        is = s.substring(fromIndex, i);
        fromIndex = i + 1;
      }

      try {
        fa[g] = Float.parseFloat(is);
      } catch (NumberFormatException e) {
        e.printStackTrace();
      }
      tf += fa[g];
      if (i == -1) {
        break;
      }
      g++;
    }

    for (int i = 0; i < fa.length; i++) {
      fa[i] /= tf;
    }

  }

  @Override
  protected void openPorts() {
    inport = openInputArray("IN");
    gainsport = openInput("GAINS");

  }
}
