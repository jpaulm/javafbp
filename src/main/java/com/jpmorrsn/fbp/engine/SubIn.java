package com.jpmorrsn.fbp.engine;


/**
 * Look after input to subnet - added for subnet support
 */
@InPort("NAME")
@OutPort("OUT")
public class SubIn extends Component {

  /******************************************************************************
     * Copyright 2008, 2012, J. Paul Morrison.  At your option, you may copy, 
     * distribute, or make derivative works under the terms of the Clarified Artistic License, 
     * based on the Everything Development Company's Artistic License.  A document describing 
     * this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. 
     * THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.
     * */

  private InputPort inport, nameport;

  private OutputPort outport;

  @Override
  protected void execute() {

    Packet np = nameport.receive();
    if (np == null) {
      return;
    }
    nameport.close();
    String pname = (String) np.getContent();
    drop(np);

    if (outport.isClosed) {
      return;
    }

    inport = mother.getInports().get(pname);
    mother.traceFuncs(getName() + ": Accessing input port: " + inport.getName());
    Packet p;
    // I think this works!
    Component oldReceiver;
    if (inport instanceof InitializationConnection) {
      InitializationConnection iico = (InitializationConnection) inport;
      oldReceiver = iico.getReceiver();
      InitializationConnection iic = new InitializationConnection(iico.content, this);
      iic.name = iico.name;
      //iic.network = iico.network;

      p = iic.receive();
      p.setOwner(this);
      outport.send(p);
      iic.close();
    } else {
      oldReceiver = ((Connection) inport).getReceiver();
      ((Connection) inport).setReceiver(this);
      //Connection c = (Connection) inport;

      while ((p = inport.receive()) != null) {
        p.setOwner(this);
        outport.send(p);
      }
    }

    // inport.close();
    mother.traceFuncs(getName() + ": Releasing input port: " + inport.getName());

    inport.setReceiver(oldReceiver);

  }

  @Override
  protected void openPorts() {

    nameport = openInput("NAME");
    outport = openOutput("OUT");
  }
}
