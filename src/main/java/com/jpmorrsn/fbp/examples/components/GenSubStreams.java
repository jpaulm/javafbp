package com.jpmorrsn.fbp.examples.components;


/* Sven Steinseifer - 2010 */

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


@OutPort("OUT")
public class GenSubStreams extends Component {

  OutputPort outputPort;

  @Override
  protected void execute() {
    outputPort.send(create(Packet.OPEN, "OPEN(1)"));
    outputPort.send(create("A"));
    outputPort.send(create(Packet.OPEN, "OPEN(2)"));
    outputPort.send(create("B"));
    outputPort.send(create(Packet.CLOSE, "CLOSE(2)"));
    outputPort.send(create(Packet.CLOSE, "CLOSE(1)"));
    outputPort.close();
  }

  @Override
  protected void openPorts() {
    outputPort = openOutput("OUT");
  }

}
