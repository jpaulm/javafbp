package com.jpmorrsn.fbp.engine;


/** Look after (synchronous) output/input from/to subnet.
 * This component sends a single packet out to the (external) output port, and then 
 * immediately does a receive from the corresponding (external) input port. This process
 * repeats until a null is received on the input port.
 */
@InPorts({ @InPort("NAME"), @InPort("IN") })
@OutPort("OUT")
public class SubOI extends Component {

  /************************************************************************
     * Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, 
     * distribute, or make derivative works under the terms of the Clarified Artistic License, 
     * based on the Everything Development Company's Artistic License.  A document describing 
     * this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. 
     * THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.
     * */
  private InputPort inport, nameport, extinport;

  private OutputPort outport, extoutport;

  @Override
  protected void execute()/* throws Throwable*/{
    Packet np = nameport.receive();
    if (np == null) {
      return;
    }
    nameport.close();
    String pname = (String) np.getContent();
    drop(np);

    int i = pname.indexOf(":");
    String oname = pname.substring(0, i);
    String iname = pname.substring(i + 1);
    extoutport = mother.getOutports().get(oname);
    mother.traceFuncs(getName() + ": Accessing output port: " + extoutport.getName());
    Component oldSender = extoutport.getSender();
    extoutport.setSender(this);

    extinport = mother.getInports().get(iname);
    mother.traceFuncs(getName() + ": Accessing input port: " + extinport.getName());
    Component oldReceiver = ((Connection) extinport).getReceiver();
    ((Connection) extinport).setReceiver(this);

    Packet p;
    while ((p = inport.receive()) != null) {
      extoutport.send(p);

      p = extinport.receive();
      p.setOwner(this);
      outport.send(p);

    }

    mother.traceFuncs(getName() + ": Releasing input port: " + extinport.getName());
    ((Connection) extinport).setReceiver(oldReceiver);
    extinport = null;

    mother.traceFuncs(getName() + ": Releasing output port: " + extoutport.getName());
    extoutport.setSender(oldSender);
    extoutport = null;
  }

  @Override
  protected void openPorts() {

    nameport = openInput("NAME");

    inport = openInput("IN");
    outport = openOutput("OUT");
  }
}
