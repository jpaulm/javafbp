package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

@InPort(value = "CONFIG", optional = true)
@OutPort("OUT")
public class GenerateSlowly extends Component {

	private InputPort cfgPort;
	private OutputPort outputPort;

  /**
   * @see com.jpmorrsn.fbp.engine.Component#execute()
   *
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws Exception {

	long intvl = 1000; 
	Packet p = cfgPort.receive();  
	if (p != null) {
		intvl = Long.parseLong((String)p.getContent());
	}
    for (int i = 0; i < 100000; i++) {
      String s = Integer.toString(i);
      outputPort.send(create(s));
      Thread.sleep(1000);
    }

  }

  /**
   * @see com.jpmorrsn.fbp.engine.Component#openPorts()
   *
   * {@inheritDoc}
   */
  @Override
  protected void openPorts() {
	cfgPort = openInput("CONFIG");  
    outputPort = openOutput("OUT");

  }

}
