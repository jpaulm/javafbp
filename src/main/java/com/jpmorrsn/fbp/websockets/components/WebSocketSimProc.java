/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.websockets.components;

import java.util.Random;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

/**
 * Sample web socket request processor
 * 
 * This is a non-looper processing one substream per activation
 * 
 * Expected input is a substream, consisting of 
 *  - open bracket
 *  - packet containing socket reference - Java Class WebSocket
 *  - packet containing data reference - should be a string, colon, blank, 'namelist'
 *  - close bracket
 * 
 * Generated output is a substream, conforming to requirements of WebSocketRespond -
 *  in the case of this component, this is
 *  - open bracket
 *  - packet containing socket reference - Java Class WebSocket
 *  - 0 or more packets containing data string references
 *  - close bracket
 *  
 *  This component does a random delay - from 0 to 20 seconds - before
 *   sending its response - this is to allow testing of multiple clients
 */
@ComponentDescription("Simple request processing")
@OutPort("OUT")
@InPort("IN")
public class WebSocketSimProc extends Component {

	static final String copyright = "Copyright 2007, 2014, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	private InputPort inport;

	private OutputPort outport;
	
	/*
	 * Make sure that the substream comes out of a single port of a single process, all together...
	 */

	@Override
	protected void execute() {

		Packet lbr = inport.receive();
		Packet p1 = inport.receive();
		Packet p2 = inport.receive();
		Packet rbr = inport.receive();

		String s = (String) p2.getContent();
		drop(p2);
		int i = s.indexOf(":");
		String t = s.substring(0, i);
		
		Random rand = new Random();
		int j = rand.nextInt(20);

		if (s.endsWith("namelist")) {
			
			try {
				sleep(j * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		outport.send(lbr);

		outport.send(p1);  // contains Connection
		outport.send(create(t + " Joe Fresh"));
		outport.send(create(t + " Aunt Jemima"));
		outport.send(create(t + " Frankie Tomatto's"));		
		
		outport.send(rbr);
		
		}
		else {
			outport.send(lbr);

			outport.send(p1);
			outport.send(create("unknown keyword"));
			
			outport.send(rbr);
		}
			

	}

	@Override
	protected void openPorts() {

		inport = openInput("IN");

		outport = openOutput("OUT");

	}
}
