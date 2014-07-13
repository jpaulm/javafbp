/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


@OutPorts({ @OutPort(value = "OUT1", optional = true), @OutPort(value = "OUT2", optional = true), @OutPort("OUT3") })
@InPort("IN")
public class TBBWF extends Component {

  private InputPort inport;

  OutputPort outport1, outport2, outport3;

  @Override
  protected void execute() {
    int i, j = 0;
    Packet ip1 = inport.receive();
    if (ip1 == null) {
      i = 1;
    } else {
      String s = (String) ip1.getContent();
      i = Integer.parseInt(s);
      drop(ip1);
      Packet ip2 = inport.receive();
      if (ip2 == null) {
        j = 0;
      } else {
        s = (String) ip2.getContent();
        j = Integer.parseInt(s);
        drop(ip2);
      }
    }
    if (i == j) {
      i = i * 2;
    } else {
      i = Math.max(i, j);
    }
    outport1.send(create(i + ""));
    outport2.send(create(i + ""));
    outport3.send(create(getName() + "," + i));
    //System.out.println(i);
    inport.close();
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    outport1 = openOutput("OUT1");
    outport2 = openOutput("OUT2");
    outport3 = openOutput("OUT3");
  }
}
