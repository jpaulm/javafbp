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

package com.jpaulmorrison.fbp.core.components.text;


import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InPorts;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/**
 * Obscure single characters in each packet IN with the given char OBS (unless they match the char EXC) and copy to OUT.
 *
 */
@ComponentDescription("Replace characters apart from EXC in each packet IN with the given OBS and copy to OUT")
@OutPort("OUT")
@InPorts({ @InPort("IN"), @InPort("OBS"), @InPort("EXC") })
public class Obscure extends Component {

 
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
