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
import java.net.Socket;
import java.net.UnknownHostException;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InPorts;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.MustRun;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/**
 * Component to write data to a socket, using a stream of packets. The port number is specified as a 
 * String via an InitializationConnection.  It is specified as "must run" so that the socket will  
 * be closed even if no data packets are input.
 * 
 * This component writes a stream of packets to a socket, issuing a read every 20 packets - this 
 * is a complementary component to ReadFromSocket, which issues an Ack write after every 20 packets.
 * The idea here is to take advantage of the speed of writing the packets with minimal checking, but 
 * to check synchronization periodically (it made sense when I wrote it!). There are similar components
 * in the C#FBP implementation.
 *  
 * http://java.sun.com/developer/onlineTraining/Programming/BasicJava2/socket.html
 */
@ComponentDescription("Writes a stream of packets to a socket")
@InPorts({ @InPort(value = "IN", description = "Packets to be written", type = String.class),
    @InPort(value = "PORT", description = "Port name", type = String.class) })
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
@MustRun
public class WriteToSocket extends Component {

 
  private InputPort inport;

  InputPort pport;

  double _timeout = 20.0; // 20 secs

  private OutputPort outport;

  //Socket socket = null;

  PrintWriter out = null;

  BufferedReader in = null;

  @SuppressWarnings("unused")
  @Override
  protected void execute() {// Create socket connection

    Packet ptp = pport.receive();
    if (ptp == null) {
      return;
    }
    pport.close();

    String pti = (String) ptp.getContent();
    String parts[] = pti.split(",");
    String host = parts[0];
    pti = parts[1].trim();
    int pt = 0;
    try {
      pt = Integer.parseInt(pti);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    drop(ptp);

    Socket socket;
    try {
      socket = new Socket(host, pt);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (UnknownHostException e) {
      //InetAddress inetaddr = socket.getInetAddress(); 
      //String host = inetaddr.getHostName();
      System.out.println("Unknown host: " + host);
      System.exit(1);
    } catch (IOException e) {
      System.out.println("No I/O");
      System.exit(1);
    }

    Packet p;

    int cyclic_count = 0;

    while ((p = inport.receive()) != null) {

      String s = String.format("%1$04d", cyclic_count);

      longWaitStart(_timeout);

      out.println(s + ":" + p.getContent());
      ///* experimental
      if (cyclic_count % 20 == 0) {
        try {
          String line = in.readLine();
          // System.out.println("Text received :" + line);
        } catch (IOException e) {
          System.out.println("Read failed");
          System.exit(1);
        }
      }
      // */
      longWaitEnd();

      if (outport.isConnected()) {
        outport.send(p);
      } else {
        drop(p);
      }
      cyclic_count = (cyclic_count + 1) % 10000;
    }
    out.println("Closedown");
    out.close();
    try {
      in.close();
    } catch (IOException e) {
      System.out.println("No I/O");
      System.exit(1);
    }

  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    pport = openInput("PORT");

    outport = openOutput("OUT");

  }

}
