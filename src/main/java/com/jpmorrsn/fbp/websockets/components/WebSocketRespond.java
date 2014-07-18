/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.websockets.components;


import org.java_websocket.WebSocket;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.Packet;


@InPort("IN")
public class WebSocketRespond extends Component {

  private InputPort inport;

  /**
   * General component to receive sequence of substreams, and use them to return data to the client
   * 
   * Expected input is a series of substreams, each consisting of
   *  - open bracket
   *  - packet containing socket reference - Java Class WebSocket
   *  - 0 or more packets containing data string references
   *  - close bracket
   *  
   */
  
  @Override
  protected void execute() throws Exception {

    while (true) {
      Packet lbr = inport.receive();
      if (lbr == null) {
        break;
      }
      drop(lbr);
      Packet p1 = inport.receive();
      WebSocket conn = (WebSocket) p1.getContent();
      drop(p1);
      
      Packet p2 = inport.receive();
      while (p2.getType() != Packet.CLOSE) {

          String message = (String) p2.getContent();
          conn.send(message);          
          drop(p2);
      
          p2 = inport.receive();
      }
      
      drop(p2);
      

    }
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.Component#openPorts()
   */
  @Override
  protected void openPorts() {
    inport = openInput("IN");

  }

}
