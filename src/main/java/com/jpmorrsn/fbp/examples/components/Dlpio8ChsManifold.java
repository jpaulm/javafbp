package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/* FBP component annotations

*/

@ComponentDescription("Checks and initialises reading DLP-IO8 DAM channels")
@InPort(value = "IN", description = "Tree made of specific DLP-IO8 DAM " + "and jd2xxManager singleton", type = Object.class)
@OutPorts({

@OutPort(value = "OUT_CHINFO", arrayPort = true, optional = true, type = Object.class, description = "Channel info"),

@OutPort(value = "OUT_CHDATA", arrayPort = true, optional = true, type = Object.class, description = "Channel data"),

@OutPort(value = "OUT_LOG", description = "Specific DLP-IO8 DAM", type = Object.class, optional = true)

})
/**

* Component to acquire readings from a DLP-IO8 DAM

* @author EC

* @version 1.1

*/
public class Dlpio8ChsManifold extends Component {

  static final String copyright =

  "Copyright 2010, 2011 Technabling Ltd."

  + "Based on software developed by J. Paul Morrison (JavaFBP)";

  private InputPort inPort;

  OutputPort outLogPort, outChInfoPort[], outChDataPort[];

  @Override
  protected void openPorts() {

    inPort = openInput("IN");

    outChInfoPort = openOutputArray("OUT_CHINFO");

    outChDataPort = openOutputArray("OUT_CHDATA");

    outLogPort = openOutput("OUT_LOG");

  }

  @Override
  protected void execute() {
    Packet p = inPort.receive();
    Object o = p.getContent();
    outChInfoPort[0].send(create(o));
    outChInfoPort[1].send(create(o));
    outChDataPort[0].send(create(o));
    outChDataPort[1].send(create(o));
    outLogPort.send(create(null));
    drop(p);

  }
}
