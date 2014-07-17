/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2014 All Rights Reserved. 
 */
package components;

/**
 * General component to receive sequence of data chunks from a web socket and convert them
 *  into a series of substreams suitable for processing by custom components  
 * 
 * Generated output is a series of substreams, each consisting of
 *  - open bracket
 *  - packet containing socket reference - Java Class WebSocket
 *  - packet containing data string reference
 *  - close bracket
 *  
 *  The port number is passed in via an IIP associated with input port PORT
 *  
 *  This component is long-running; at startup, it starts a WebSocketServer thread
 *  
 *  Every 1/2 second the WebSocketServer checks to see if a 'kill' message has been received 
 *   by WebSocketreceive - if it has, the WebSocketServer is closed down. (This may need tweaking!)
 *   
 *  'Kill' is signalled by the client sending the character string '@kill' 
 *  
 */

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collections;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
//import org.java_websocket.drafts.Draft_17;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidHandshakeException;
//import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.FrameBuilder;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.*;
import org.java_websocket.server.WebSocketServer;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

@InPort("PORT")
@OutPort("OUT")
public class WebSocketReceive extends Component {

  private InputPort portPort;
  private OutputPort outport;

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.Component#execute()
   */
  @Override
  protected void execute() throws Exception {

    putGlobal("killsw", new Boolean(false));
    WebSocketImpl.DEBUG = true; /// tracing for socket stuff
    
    Packet p = portPort.receive();
    Integer i = (Integer) p.getContent();
    int port = i.intValue();
    drop (p);

    WebSocketServer wss = new MyWebSocketServer(port, new Draft_10());
    // Draft 17 - Hybi 17/RFC 6455 and is currently supported by Chrome16+ and IE10.
    // Draft 10 -  Hybi 10. This draft is supported by Chrome15 and Firefox6-9.

    //wss.stop();
    System.out.println("WebSocketServer starting");
    wss.start();

    while (true) {

      try {
        Thread.sleep(500); // sleep for 1/2 sec
      } catch (InterruptedException e) {
        e.printStackTrace();
        // handle the exception...        
        // For example consider calling Thread.currentThread().interrupt(); here.
      }
      Boolean killsw = (Boolean) getGlobal("killsw");
      if (killsw.booleanValue()) {
    	// see also http://stackoverflow.com/questions/4812686/closing-websocket-correctly-html5-javascript
    	
        wss.stop();
        return;
      }
    }
  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.Component#openPorts()
   */
  @Override
  protected void openPorts() {

	portPort = openInput("PORT");
    setOutport(openOutput("OUT"));

  }

  /**
   * @return the outport
   */
  public OutputPort getOutport() {
    return outport;
  }

  /**
   * @param outport the outport to set
   */
  public void setOutport(final OutputPort outport) {
    this.outport = outport;
  }

  private class MyWebSocketServer extends WebSocketServer {

    //private static int counter = 0;

    Component comp = null;

    public MyWebSocketServer(final int port, final Draft d) throws UnknownHostException {
      super(new InetSocketAddress(port), Collections.singletonList(d));

    }

    @SuppressWarnings("unused")
	public MyWebSocketServer(final InetSocketAddress address, final Draft d) {
      super(address, Collections.singletonList(d));
    }

    @Override
    public void onOpen(final WebSocket conn, final ClientHandshake handshake) {
    	System.out.println("open");
		}

    
	public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(
				WebSocket conn, Draft draft, ClientHandshake request)
				throws InvalidDataException {
			ServerHandshakeBuilder resp = new HandshakeImpl1Server();
			resp.setHttpStatusMessage("HTTP/1.1 101 Switching Protocols\r\n");

			String val = request.getFieldValue("sec-websocket-protocol");
			if (!(val.equals(""))) {
				resp.put("sec-websocket-protocol", val);
			}  // experimental
				
				try {
					resp = (ServerHandshakeBuilder) draft
							.postProcessHandshakeResponseAsServer(request, resp);
				} catch (InvalidHandshakeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//}
			return resp;
		}
    
    @Override
    public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
      //System.out.println("closed " + code + " " + reason + " " + remote);
      System.out.println( "Connection closed (" + code + ") by " + ( remote ? "remote peer" : "us" ) );
    }

    @Override
    public void onError(final WebSocket conn, final Exception ex) {
      System.out.println("Error:");
      ex.printStackTrace();
    }

    /*
	 * Make sure that the substream comes out of a single port of a single process, all together...
	 */
    
    @Override
    public void onMessage(final WebSocket conn, final String message) {

    System.out.println(message);
       if (message.equals("@kill")){       	  
          //conn.close(CloseFrame.NORMAL, "Close message"); (caused 1005 errors)
          putGlobal("killsw", new Boolean(true));
          }
          else{

      Packet p1 = comp.create(conn);
      Packet p2 = comp.create(message);
      Packet lbr = comp.create(Packet.OPEN, "pdata");
      Packet rbr = comp.create(Packet.CLOSE, "pdata");

      WebSocketReceive wsr = (WebSocketReceive) comp;

      wsr.getOutport().send(lbr);
      wsr.getOutport().send(p1);  // conn
      wsr.getOutport().send(p2);  // data
      wsr.getOutport().send(rbr);
      }

    }

    @Override
    public void onMessage(final WebSocket conn, final ByteBuffer blob) {
    	System.out.println(blob);
      conn.send(blob);
    }

    @SuppressWarnings({"unused"})
    public void onWebsocketMessageFragment(final WebSocket conn, final Framedata frame) {
    	System.out.println(frame);
      FrameBuilder builder = (FrameBuilder) frame;
      builder.setTransferemasked(false);
      conn.sendFrame(frame);
    }

    @Override
    public void start() {
      //Object selectorthread = null;
      //if (selectorthread != null) {
      //  throw new IllegalStateException(getClass().getName() + " can only be started once.");
      //}
      comp = (Component) Thread.currentThread();

      new Thread(this).start();
    }

    

  }

}
