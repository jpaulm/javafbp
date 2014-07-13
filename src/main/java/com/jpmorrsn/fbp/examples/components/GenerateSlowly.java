package com.jpmorrsn.fbp.examples.components;


import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;


@OutPort("OUT")
public class GenerateSlowly extends Component {

  private OutputPort outputPort;

  /**
   * @see com.jpmorrsn.fbp.engine.Component#execute()
   *
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws Exception {

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
    outputPort = openOutput("OUT");

  }

}
