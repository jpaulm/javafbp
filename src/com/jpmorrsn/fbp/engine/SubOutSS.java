package com.jpmorrsn.fbp.engine;


/** Look after output from subnet - added for subnet support 
 *  Differs from SubOut in that it adds an open bracket at the beginning, and a
 *  close bracket at the end 
 */
@InPorts({ @InPort("IN"), @InPort("NAME") })
public class SubOutSS extends Component {

  /************************************************************************** *
     * Copyright 2008, 2012, J. Paul Morrison.  At your option, you may copy, 
     * distribute, or make derivative works under the terms of the Clarified Artistic License, 
     * based on the Everything Development Company's Artistic License.  A document describing 
     * this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. 
     * THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.
     * */
  private InputPort inport, nameport;

  private OutputPort outport;

  @Override
  protected void execute()/* throws Throwable*/{
    Packet np = nameport.receive();
    if (np == null) {
      return;
    }
    nameport.close();
    String pname = (String) np.getContent();
    drop(np);

    outport = mother.getOutports().get(pname);
    mother.traceFuncs(getName() + ": Accessing output port: " + outport.getName());
    outport.setSender(this);
    Packet p;
    p = create(Packet.OPEN, "");
    outport.send(p);

    while ((p = inport.receive()) != null) {
      outport.send(p);

    }
    p = create(Packet.CLOSE, "");
    outport.send(p);

    mother.traceFuncs(getName() + ": Releasing output port: " + outport.getName());
    outport = null;
  }

  @Override
  protected void openPorts() {

    nameport = openInput("NAME");
    inport = openInput("IN");
  }
}
