package com.jpmorrsn.fbp.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

/**
 * Component to assign incoming packets to the output port that has the smallest
 * backlog of packets waiting to be processed.
 * All the IPs in a substream must go to the same output port array element (logic added Aug. 2, 2015)
 */
@ComponentDescription("Sends incoming packets to output array element with smallest backlog")
@OutPort(value = "OUT", arrayPort = true, description = "Packets being output")
@InPort(value = "IN", description = "Incoming packets")
public class LoadBalance extends Component {

	static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	private InputPort inport;

	private OutputPort[] outportArray;

	@Override
	protected void execute() {

		int no = outportArray.length;
		int backlog;
		int sel = -1;
		int substream_level = 0;

		Packet p;
		while ((p = inport.receive()) != null) {
			if (substream_level == 0) {
				backlog = Integer.MAX_VALUE;
				for (int i = 0; i < no; i++) {
					int j = outportArray[i].downstreamCount();
					if (j <= backlog) {
						backlog = j;
						sel = i;
					}
				}
				if (getTracing())
				    System.out.println("Port " + sel + " selected; backlog: " + backlog);   // for debugging
			}
			if (p.getType() == Packet.OPEN)
				substream_level ++;
			else if (p.getType() == Packet.CLOSE)
				substream_level --;
			//else 
			//	System.out.println("Data: " + p.getContent());
			
			outportArray[sel].send(p);

		}
	}

	@Override
	protected void openPorts() {

		inport = openInput("IN");

		outportArray = openOutputArray("OUT");

	}
}
