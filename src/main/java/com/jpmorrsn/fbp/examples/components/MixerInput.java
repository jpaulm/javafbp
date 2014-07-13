package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;


/** Miscellaneous Mixer Input
**/
@ComponentDescription("Miscellaneous Mixer Input")
@OutPort("OUT")
public class MixerInput extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

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
