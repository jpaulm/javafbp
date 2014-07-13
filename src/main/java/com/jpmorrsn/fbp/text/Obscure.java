/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.text;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Obscure single characters in each packet IN with the given char OBS (unless they match the char EXC) and copy to OUT.
 *
 */
@ComponentDescription("Replace characters apart from EXC in each packet IN with the given OBS and copy to OUT")
@OutPort("OUT")
@InPorts({ @InPort("IN"), @InPort("OBS"), @InPort("EXC") })
public class Obscure extends Component {

  static final String copyright = "Copyright 2007, 2010, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  InputPort inport, obsport, excport;

  private OutputPort outport;

  @Override
  protected void execute() {
    char obs = ' '; // Default obscure with space
    Packet pObs = obsport.receive();
    if (pObs != null) {
      obs = ((String) pObs.getContent()).charAt(0);
      drop(pObs);
    }
    obsport.close();

    String exc = " "; // Default do not obscure spaces
    Packet pExc = excport.receive();
    if (pExc != null) {
      exc = (String) pExc.getContent();
      drop(pExc);
    }
    excport.close();

    Packet pin;
    while ((pin = inport.receive()) != null) {
      String out = "";
      String in = (String) pin.getContent();
      if (in != null) {
        in += exc; // add one EXC token as a marker
        int e = in.indexOf(exc);
        int s = 0;
        while (in.length() > out.length() && e > -1) {
          out += getStringFilledWith((e - s), obs) + exc;
          //System.out.println("in:  |" + in + "|\nout: |" + out + "|");
          s = e + exc.length();
          e = in.indexOf(exc, s);
        }
        // drop the marker
        out = out.substring(0, out.length() - exc.length());
        //System.out.println("out: |" + out + "|");
      }
      drop(pin); // did you hear that?

      Packet pout = create(out);
      outport.send(pout);
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    obsport = openInput("OBS");
    excport = openInput("EXC");
    outport = openOutput("OUT");
  }

  protected String getStringFilledWith(final int number, final char filler) {
    String filled = "";
    if (number > 0) {
      char[] fillers = new char[number];
      for (int n = 0; n < fillers.length; n++) {
        fillers[n] = filler;
      }
      filled = new String(fillers);
    }
    return filled;
  }
}
