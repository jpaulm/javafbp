package com.jpmorrsn.fbp.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to generate arrays of sound samples from note frequencies and times.
 * This component is the "front-end" of PlayTune
 */
@ComponentDescription("Generate arrays of sound samples")
@InPort("IN")
// amplitude range will be between +RANGE and -RANGE
@OutPorts({ @OutPort("OUT"), @OutPort(value = "TRACE", optional = true) })
// trace if connected
public class GenSamples extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport /*, rangeport */;

  private OutputPort traceport, outport;

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
    Packet p;

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
      traceport.send(create(s));

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
    traceport = openOutput("TRACE");

  }
}
