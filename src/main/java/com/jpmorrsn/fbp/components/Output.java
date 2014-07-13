package com.jpmorrsn.fbp.components;


/* Sven Steinseifer - 2010 */

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.Packet;


@InPort("IN")
public class Output extends Component {

  private InputPort inputPort;

  @Override
  protected void execute() {
    Packet p;
    int level = 1;
    while ((p = inputPort.receive()) != null) {
      switch (p.getType()) {
        case Packet.OPEN:
          System.out.println("OPEN(" + level + ")");
          level++;
          break;
        case Packet.CLOSE:
          level--;
          System.out.println("CLOSE(" + level + ")");
          break;
        default:
          System.out.println(p.getContent());
      }
      drop(p);
    }
  }

  @Override
  protected void openPorts() {
    inputPort = openInput("IN");
  }

}
