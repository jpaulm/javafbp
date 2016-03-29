package com.jpmorrsn.fbp.examples.components;

import java.util.HashMap;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

/**
 * Component to generate word counts
 */
@ComponentDescription("Generate word counts")
@OutPort("OUT")
@InPort("IN")
public class GenerateWordCounts extends Component {

  static final String copyright = "Copyright 2007, 2016, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport;

  private OutputPort outport;

  @SuppressWarnings("unchecked")
@Override
  protected void execute() {
    
    HashMap<String, Integer> hm = new HashMap<String, Integer>();
    int j;
    Packet<String> p;
    while ((p = inport.receive()) != null) {
    	String s = (String) p.getContent();
    	Integer i = hm.get(s);    	
    	if (i == null)
    		j = 0;
    	else 
    		j = i.intValue();
    	j++;
    	hm.put(s, Integer.valueOf(j));   
    	drop(p);        
    }
    
    for (String key : hm.keySet()) {
    	p = create(key + ", " + hm.get(key));    	
    	outport.send(p);    	
    }

  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");
  }
}
