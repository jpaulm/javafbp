package com.jpaulmorrison.fbp.core.components.parsexml;


import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;
import com.jpaulmorrison.utils.BabelParser2;

@InPort(value = "IN", description = "Data to be parsed")
@OutPort(value = "OUT", description = "IPs")

public class ParseXML extends Component {

	private InputPort inPort;

	private OutputPort outPort;
	 
	  
	@Override
	protected void execute() throws Exception {
		  Packet<?> p;
		  String xml = "";
		    while ((p = inPort.receive()) != null) {
		    	xml += p.getContent();		
		    	drop(p);
		    }
		    String q = null;
		    String strType = null;
		    Integer errNo = Integer.valueOf(0);
			BabelParser2 bp = new BabelParser2(xml, errNo);		  
		    while (true) {
		    	strType = " ";
		    	q = scanTag(xml, bp, errNo, strType);
		    	if (q == null)
		    		break;
		    	strType = q.substring(0, 1);
		    	q = q.substring(1);
		    	
		    	Packet<?> op = null;
		    	
		    	if (strType.equals("?"))
		    		op = create(new Control(q)); 	
		    	else if (strType.equals("!"))
		    		op = create(new Control(q)); 			    	
		    	else if (strType.equals("/"))
		    		op = create(new End(q)); 
		    	else if (strType.equals("C"))
		    		op = create(new Comment(q)); 
		    	else {
		    		if (q.endsWith("/")) {
		    			q = q.substring(0, q.length() - 1);
		    			op = create (new Solo(q));
		    		}
		    		else
		    			op = create(new Start(q)); 
		    	}
		    	
		    	outPort.send(op);
		    	
		    	bp.eraseOutput();
		    	if (bp.finished())
    				return;
		    	while (true) { // scan off text after begin tag, to endtag
		    		if (bp.finished())
		    			break;
					if (bp.tc('<', 'n'))
						break;		
					bp.tu();
				}
		    	
		    	String s = bp.getOutStr().trim();
		    	if (!s.equals("")) {
		    		op = create(new Text(s));
		    		outPort.send(op);
		    	}
		    	 
		    }
		    
	    	
	}
	
	String scanTag(String input, BabelParser2 bp, Integer errNo, String strType) {
		
		while (true) { // skip blanks, CRs or tabs
			if (!(bp.tb('o')))
				break;
		}

		if (bp.finished())
			return null;
		
		if (!(bp.tc('<', 'o'))) {
			errNo = Integer.valueOf(8);
			return null;
		}
		
		// we should be just after a <
	
		bp.eraseOutput();
		while (true) {
			if (bp.finished())
				return null;
			
			if (bp.tc('/', 'o')) {
				strType = "/";
				//break;
			}
			

			else if (bp.tc('!', 'o')) {
				if (!skipComment(bp))
					strType = "!";
				else {
					strType = "C";
					return strType + bp.getOutStr();
				}
			}
			
			else if (bp.tc('?', 'o')) {
				strType = "?";
				//break;
			}
			
			while (true) { // scan off a symbol within <>
				if (bp.tc('>', 'o'))
					break;		
				bp.tu();
			}
			break;
		}
		return strType + bp.getOutStr();
	}

	boolean skipComment(BabelParser2 bp) {
		if (!bp.tc('-', 'o'))
			return false;
		if (!bp.tc('-', 'o'))
			return false;
		while (true) { // find -->
			if (bp.finished())
				return false;
			if (bp.tc('-', 'o') && bp.tc('-', 'o') && bp.tc('>', 'o'))
				break;			 
			bp.tu();
		}
		return true;
	}
	@Override
	protected void openPorts() {
		inPort = openInput("IN");
		outPort = openOutput("OUT");

	}


}
