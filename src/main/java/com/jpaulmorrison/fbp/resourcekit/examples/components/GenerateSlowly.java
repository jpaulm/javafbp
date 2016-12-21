package com.jpaulmorrison.fbp.resourcekit.examples.components;


import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;

@InPort(value = "CONFIG", optional = true)
@OutPort("OUT")
public class GenerateSlowly extends Component {

	private InputPort cfgPort;
	private OutputPort outputPort;

  /**
   * @see com.jpaulmorrison.fbp.core.engine.Component#execute()
   *
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws Exception {

	long intvl = 300; 
	Packet p = cfgPort.receive();  
	if (p != null) {
		intvl = Long.parseLong((String)p.getContent());
		drop(p);
	}
    for (int i = 0; i < 60; i++) {
      String s = Integer.toString(i);
      outputPort.send(create(s));
      Thread.sleep(intvl);
    }

  }

  /**
   * @see com.jpaulmorrison.fbp.core.engine.Component#openPorts()
   *
   * {@inheritDoc}
   */
  @Override
  protected void openPorts() {
	cfgPort = openInput("CONFIG");  
    outputPort = openOutput("OUT");

  }

}
