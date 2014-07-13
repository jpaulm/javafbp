/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2008, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.FlowError;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.MustRun;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to write data to the console, using a stream of packets. It is
 * specified as "must run" so that the output file will be cleared even if no
 * data packets are input.  It also checks that upstream Balloon is working.
 */
@ComponentDescription("Check ballooning logic")
@InPort(value = "IN", description = "Packets to be checked", type = String.class)
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
@MustRun
public class CheckBallooning extends Component {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport;

  double _timeout = 5.0; // 5 secs

  private OutputPort outport;

  @Override
  protected void execute() {
    Packet p;
    int num = 200;

    while ((p = inport.receive()) != null) {
      longWaitStart(_timeout);
      if (num > 100 && num < 150) {
        try {
          sleep(120);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } // force ballooning
      }
      String s = (String) p.getContent();
      String t = String.format("%1$06d", num);
      if (!s.startsWith(t)) {
        FlowError.complain("Out of sequence");
        break;
      }

      if (p.getType() == Packet.OPEN) {
        System.out.println("===> Open Bracket");
      } else if (p.getType() == Packet.CLOSE) {
        System.out.println("===> Close Bracket");
      } else {
        System.out.println((String) p.getContent());
      }
      longWaitEnd();
      if (outport.isConnected()) {
        outport.send(p);
      } else {
        drop(p);
      }
      num--;
    }

  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");

  }
}
