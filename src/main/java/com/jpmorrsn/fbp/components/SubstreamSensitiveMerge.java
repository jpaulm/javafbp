package com.jpmorrsn.fbp.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

@ComponentDescription("Merge multiple input streams, first-come, first-served, but sensitive to substreams")
@InPort(value = "IN", arrayPort = true, description = "Incoming packets")
@OutPort(value = "OUT", description = "Merged output")

public class SubstreamSensitiveMerge extends Component {
	/**
	 * First-come, first-served - sensitive to substreams
	 **/

	static final String copyright = "Copyright 2007, 2015, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

	private InputPort[] inportArray;
	private OutputPort outport;

	@Override
	protected void execute() {

		@SuppressWarnings("rawtypes")
		Packet p = null;
		int i = -1;
		int substream_level = 0;
		while (true) {
			if (substream_level != 0) {
				p = inportArray[i].receive();
				if (p == null)
					break;
			} else {
				while (true) {
					try {
						i = findInputPortElementWithData(inportArray);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} // will suspend if all elements empty but not drained
					if (i == -1) // all elements are drained
						return;
					p = inportArray[i].receive();
					if (p != null)
						break;
				}
				// p = inportArray[i].receive();
			}
			if (p.getType() == Packet.OPEN)
				substream_level++;
			else if (p.getType() == Packet.CLOSE)
				substream_level--;
			outport.send(p);
		}
	}

	@Override
	protected void openPorts() {

		inportArray = openInputArray("IN");
		outport = openOutput("OUT");

	}
}
