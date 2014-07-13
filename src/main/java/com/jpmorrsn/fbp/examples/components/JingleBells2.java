package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;


/** Generate note/duration pairs for Jingle Bells
**/
@ComponentDescription("Generate note/duration pairs for Jingle Bells")
@OutPort("OUT")
public class JingleBells2 extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

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
