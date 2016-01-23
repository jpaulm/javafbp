package com.jpmorrsn.fbp.components;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


	
	@ComponentDescription("Merge multiple input streams, following Round Robin system")
	@OutPort(value = "IN", arrayPort = true, description = "Incoming packets")
	@InPort(value = "OUT", description = "Merged output")
	
	public class RRMerge extends Component {
		/** "Round Robin" Merge - merges an IP from element 0, then one from 1, then one from 2, and so on until
		 * it cycles back to 0, and so on until the first end of stream 
		 * This component will just terminate when first receive gets end of stream
		 * 
		 * The assumption is that all input streams have the same number of IPs
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

	    int no = inportArray.length;
	    Packet p;   
	    for (int i = 0; i < no; i++) {
	    	if (null == (p = inportArray[i].receive()))
	    		return;
	    	outport.send(p);
	    }

	  }
	  @Override
	  protected void openPorts() {

	    inportArray = openInputArray("IN");
	    outport = openOutput("OUT");

	  }
	}


