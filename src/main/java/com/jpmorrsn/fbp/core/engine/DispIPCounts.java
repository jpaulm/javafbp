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

package com.jpmorrsn.fbp.core.engine;


import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.jpmorrsn.fbp.core.engine.*;


/** Generate a stream of IP counts under control of a timer
*/
@ComponentDescription("Generates stream of counts under control of a timer")
@OutPort(value = "OUT", description = "Generated stream of counts", type = String.class)
@InPorts({ @InPort(value = "CLSDN", description = "Closedown signal"),
    @InPort(value = "INTVL", description = "Interval in seconds", type = String.class) })
//@MustRun
@SelfStarting
public class DispIPCounts extends Component {

  
  private OutputPort outport;

  private InputPort intvl, clsdn;

  @Override
  protected void execute() {
    Packet itp = intvl.receive();
    if (itp == null) {
      FlowError.complain("No interval specified for DispIPCounts component");
      return;
    }
    intvl.close();

    String iti = (String) itp.getContent();
    iti = iti.trim();
    long it = 0;
    try {
      it = Long.parseLong(iti);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    drop(itp);

    while (true) {
      try {
        sleep(it); //  sleep 'it' msecs
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      for (Map.Entry<String, BigInteger> kvp : network.getIPCounts().entrySet()) {
        String s = kvp.getKey() + "                                                  ";
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss:SSS");
        String t = kvp.getValue().toString();
        t = "                       " + t;
        int i = t.length();
        t = t.substring(i - 12, i);
        Packet p = create(df.format(date) + " " + s.substring(0, 40) + t);
        outport.send(p);
      }

      if (clsdn.isClosed()) {
        break;
      }
    }

  }

  @Override
  protected void openPorts() {
    outport = openOutput("OUT");
    intvl = openInput("INTVL"); // interval in secs
    clsdn = openInput("CLSDN"); // closedown signal
  }
}
