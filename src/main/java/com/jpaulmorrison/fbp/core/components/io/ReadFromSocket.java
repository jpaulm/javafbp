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
package com.jpaulmorrison.fbp.core.components.io;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/**
 * Component to read data from socket, generating a stream of
 * packets. The port number is specified as a String via an
 * InitializationConnection.
 * 
 * This component reads a stream of packets from a socket, issuing a write of an Ack every 20 packets - this 
 * is a complementary component to WriteToSocket, which issues a read after every 20 packets.
 * 
 * The idea here is to take advantage of the speed of reading the packets with minimal checking, but 
 * to check synchronization periodically (it made sense when I wrote it!). There are 2 similar components
 * in the C#FBP implementation.
 * 
 * 
 * http://java.sun.com/developer/onlineTraining/Programming/BasicJava2/socket.html
 */
@ComponentDescription("Generate stream of packets from socket")
@OutPort(value = "OUT", description = "Read packets", type = String.class)
@InPort(value = "PORT", description = "Port name", type = String.class)
public class ReadFromSocket extends Component {

  
  private OutputPort outport;

  private InputPort pport;

  @Override
  protected void execute() {
    ServerSocket server = null;
    Socket client = null;
    BufferedReader in = null;
    PrintWriter out = null;
    String line = null;
    double _timeout = 10.0; // 10 secs
    int cyclic_count = 0;

    Packet ptp = pport.receive();
    if (ptp == null) {
      return;
    }
    pport.close();

    String pti = (String) ptp.getContent();
    pti = pti.trim();
    int pt = 0;
    try {
      pt = Integer.parseInt(pti);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    drop(ptp);

    try {
      server = new ServerSocket(pt);
    } catch (IOException e) {
      System.err.println("Could not listen on port 4444");
      System.exit(-1);
    }

    try {
      client = server.accept();
    } catch (IOException e) {
      System.err.println("Accept failed: 4444");
      System.exit(-1);
    }

    try {
      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      out = new PrintWriter(client.getOutputStream(), true);
    } catch (IOException e) {
      System.err.println("Accept failed: 4444");
      System.exit(-1);
    }

    while (true) {
      longWaitStart(_timeout);
      try {
        line = in.readLine();
        if (line.equals("Closedown")) {
          break;
        }
        ///* experimental
        if (cyclic_count % 20 == 0) {
          out.println(getName() + ": Ack - " + line);
        }
        //*/
      } catch (IOException e) {
        System.err.println("Read failed");
        System.exit(-1);
      }
      longWaitEnd();
      String s = line.substring(0, 4);
      int i = Integer.parseInt(s);
      if (i != cyclic_count) {
        System.err.println(this.getName() + ": Cycle count doesn't match");
        break;
      }
      line = line.substring(5);
      Packet p = create(line);
      outport.send(p);
      cyclic_count = (cyclic_count + 1) % 10000;
    }
  }

  @Override
  protected void openPorts() {

    outport = openOutput("OUT");
    pport = openInput("PORT");
  }
}
