package com.jpmorrsn.fbp.engine;


/**
 * Look after input to subnet - added for subnet support. This version of SubIn
 * supports Substream Sensitivity
 */
@InPort("NAME")
@OutPort("OUT")
public class SubInSS extends Component {

  /***************************************************************************
   * Copyright 2008, 2012, J. Paul Morrison. At your option, you may copy,
   * distribute, or make derivative works under the terms of the Clarified
   * Artistic License, based on the Everything Development Company's Artistic
   * License. A document describing this License may be found at
   * http://www.jpaulmorrison.com/fbp/artistic2.htm. THERE IS NO WARRANTY; USE
   * THIS PRODUCT AT YOUR OWN RISK.
   */

  /*  Changes thanks to Sven Steinseifer */

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

    Component oldReceiver = ((Connection) inport).getReceiver();
    if (inport instanceof InitializationConnection) {
      FlowError.complain("SubinSS cannot support IIP - use Subin");
    }

    ((Connection) inport).setReceiver(this);
    int level = 0;
    while ((p = inport.receive()) != null) {
      p.setOwner(this);
      if (p.getType() == Packet.OPEN) {
        if (level > 0) {
          outport.send(p);
        } else {
          drop(p);
          mother.traceFuncs(this.getName() + " open bracket detected");
        }
        level++;
      } else if (p.getType() == Packet.CLOSE) {
        if (level > 1) {
          // pass on nested brackets
          outport.send(p);
          level--;
        } else {
          drop(p);
          mother.traceFuncs(this.getName() + " close bracket detected");
          break;
        }
      } else {
        outport.send(p);
      }
    }

    mother.traceFuncs(getName() + ": Releasing input port: " + inport.getName());
    ((Connection) inport).setReceiver(oldReceiver);

    inport = null;
    // outport.close();
  }

  @Override
  protected void openPorts() {

    nameport = openInput("NAME");
    outport = openOutput("OUT");
  }
}
