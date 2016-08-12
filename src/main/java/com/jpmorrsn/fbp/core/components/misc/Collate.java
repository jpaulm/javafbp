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

package com.jpmorrsn.fbp.core.components.misc;


import com.jpmorrsn.fbp.core.engine.*;



/**
 * Component to collate two or more streams of packets, based on a list of
 * control field lengths held in the CTLFIELDS IIP
 * 
 * Control fields in incoming IPs are assumed to be contiguous, starting at byte 0
 * 
 * Input streams are assumed to be sorted on the same control fields, in ascending order
 */
@ComponentDescription("Collate two or more streams, based on a list of control field lengths ")
@OutPort("OUT")
@InPorts({ @InPort("CTLFIELDS"), @InPort(value = "IN", arrayPort = true) })
public class Collate extends Component {

  

  private InputPort[] inportArray;

  private OutputPort outport;

  private InputPort ctlfldport;

  private String prev = null;

  private String hold = null;

  private int low;

  private Packet pArray[];

  private int parmct;

  private int[] fldArray = null;

  @Override
  protected void execute() {

    Packet cfp = ctlfldport.receive();
    if (cfp == null) {
      return;
    }

    String cp = (String) cfp.getContent();
    fldArray = splitUp(cp);
    drop(cfp);
    ctlfldport.close();

    // System.out.println(cp.fldArray.length);

    parmct = fldArray.length;
    int totlen = 0;
    for (int i = 0; i < parmct; i++) {
      totlen += fldArray[i];
    }

    for (int i = 0; i < parmct; i++) {
      Packet p2 = create(Packet.OPEN, " ");
      outport.send(p2);
    }

    int no = inportArray.length;
    int count = no;
    pArray = new Packet[no];
    Packet p;
    for (int i = 0; i < no; i++) {
      p = inportArray[i].receive();
      if (p == null) {
        pArray[i] = null;
        --count;
      } else {
        pArray[i] = p;
      }

    }

    while (true) {
      hold = "\uffff";
      low = 0;

      for (int i = 0; i < no; i++) {
        if (pArray[i] != null) {

          String value = (String) pArray[i].getContent();
          value = value.substring(0, totlen);
          if (value.compareTo(hold) < 0) {
            hold = value;
            low = i;
          }
        }

      }
      sendOutput(low);
      pArray[low] = null;
      p = inportArray[low].receive();
      if (p == null) {
        count--;
      } else {
        pArray[low] = p;
      }
      if (count == 0) {
        break;
      }

    }
    for (int i = 0; i < parmct; i++) {
      p = create(Packet.CLOSE, " ");
      outport.send(p);
    }
  }

  void sendOutput(final int x) {
    if (prev != null) {
      // if (hold.compareTo(prev) != 0) {
      int level = findLevel();
      for (int i = 0; i < level; i++) {
        Packet p2 = create(Packet.CLOSE, " ");
        outport.send(p2);
      }
      for (int i = 0; i < level; i++) {
        Packet p2 = create(Packet.OPEN, " ");
        outport.send(p2);
      }

    }
    outport.send(pArray[x]);
    prev = hold;
  }

  int findLevel() {
    int j = 0;
    for (int i = 0; i < parmct; i++) {
      String h1 = hold.substring(j, j + fldArray[i]);
      String p1 = prev.substring(j, j + fldArray[i]);
      if (h1.compareTo(p1) != 0) {
        return parmct - i;
      }
      j += fldArray[i];
    }
    return 0;
  }

  int[] splitUp(final String s) {

    int i = 0;
    int count = 0;
    while ((i = s.indexOf(",", i + 1)) != -1) {
      ++count;
    }

    int[] res = new int[count + 1];
    i = 0;
    int j = -1;
    int k = 0;
    while ((j = s.indexOf(",", i + 1)) != -1) {
      String t = s.substring(i, j).trim();
      res[k] = Integer.parseInt(t);
      i = j + 1;
      k++;
    }
    String t = s.substring(i).trim();
    res[k] = Integer.parseInt(t);
    return res;

  }

  @Override
  protected void openPorts() {

    inportArray = openInputArray("IN");
    // inport.setType(Object.class);
    ctlfldport = openInput("CTLFIELDS");

    outport = openOutput("OUT");

  }

}