package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;


/**
 * Component to generate arrays of sound samples from note frequencies and times.
 * This component is the "front-end" of PlayTune
 */
@ComponentDescription("Generate arrays of sound samples")
@InPort("RANGE")
// amplitude range will be between +RANGE and -RANGE
@OutPorts({ @OutPort("OUT"), @OutPort(value = "TRACE", optional = true) })
// trace if connected
public class Rhythm2 extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  InputPort inport, rangeport;

  private OutputPort traceport, outport;

  int curSlot = 0; // current slot in buffer

  int x = 0;

  int bufferSize = 0;

  byte[] buf = new byte[bufferSize]; //  1 second of play

  @SuppressWarnings("unused")
  @Override
  protected void execute() {

    float sampleRate = 44100; // 44100 samples per sec. - with 16 bit, same as CD

    bufferSize = (int) sampleRate * 2; // 2 bytes per sample
    buf = new byte[bufferSize]; //  1 second of play

    int C4 = 262;
    int D4 = 294;

    int E4 = 330;
    int F4 = 349;

    int G4 = 392;
    int C5 = 524;
    int G5 = 784;
    for (int i = 0; i < 6; i++) { // 6 bars 0f 4 beats      
      buildNote(E4, 250, sampleRate); // note,  msecs
      buildNote(F4, 250, sampleRate); // note,  msecs
      buildNote(C4, 500, sampleRate); // note,  msecs     
      //buildNote(0, 250, sampleRate, range); // note,  msecs
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
    int samples = (int) (duration * sampleRate / 1000);
    int gap = 25; // gap in msecs

    boolean note_ending = false, note_ended = false;
    int savex = 0;

    for (int i = 0; i < samples; i++) {
      if (note == 0) {
        x = 0;
      } else if (!note_ended) {
        x = (int) (32767.0 * Math.sin(RAD * note * i / sampleRate));
        if (note_ending) { // carry on until sign of x changes - this prevents click!
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
      traceport.send(create(s));

      if (x > 32767) {
        x = 32767;
      }
      if (x < -32767) {
        x = -32767;
      }

      buf[curSlot + 1] = (byte) (x % 256);
      buf[curSlot + 0] = (byte) (x / 256 % 256);

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

    outport = openOutput("OUT");
    traceport = openOutput("TRACE");

  }
}
