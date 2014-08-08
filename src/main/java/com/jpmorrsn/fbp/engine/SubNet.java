package com.jpmorrsn.fbp.engine;


//import java.util.Iterator;

public abstract class SubNet extends Network {

  /* *
     * Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, 
     * distribute, or make derivative works under the terms of the Clarified Artistic License, 
     * based on the Everything Development Company's Artistic License.  A document describing 
     * this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. 
     * THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.
     * */
  // subclasses should override this method
  //  @Override
  //  protected void define() throws Throwable {
  //  }
  @Override
  protected final void execute() throws Exception {
    OutputPort subEndPort = null;
    InputPort subInPort = null;

    if (status != StatusValues.ERROR) {

      mother.traceFuncs(this.getName() + " started");
      getComponents().clear();
      subEndPort = outputPorts.get("*SUBEND");
      subInPort = inputPorts.get("*CONTROL");
      if (subInPort != null) {
        Packet p = subInPort.receive();
        if (p != null) {
          drop(p);
        }
      }

      // use static fields instead!
      // tracing = mother.tracing;
      // traceFileList = mother.traceFileList;

      try {
        callDefine();
        boolean res = true;
        for (Component comp : getComponents().values()) {
          res &= comp.checkPorts();
        }
        if (!res)
      	  FlowError.complain("One or more mandatory connections have been left unconnected: " + getName());
        initiate();
        // activateAll();
        // don't do deadlock testing in subnets - you need to consider the whole net!
        deadlockTest = false;
        waitForAll();

        for (InputPort ip : inputPorts.values()) {
          if (ip instanceof InitializationConnection) {
            InitializationConnection ic = (InitializationConnection) ip;
            ic.close();
          }
        }

        /*
         * Iterator allout = (outputPorts.values()).iterator();
         * 
         * while (allout.hasNext()) {
         * 
         * 
         * OutputPort op = (OutputPort) allout.next(); op.close();
         * 
         *  }
         */
        // status = Component.StatusValues.TERMINATED; //will not be set if
        // never activated
        // mother.indicateTerminated(this);
        mother.traceFuncs(this.getName() + " closed down");
        if (subEndPort != null) {
          subEndPort.send(new Packet(null, this));
        }
      } catch (FlowError e) {
        String s = "Flow Error :" + e;
        System.out.println("Network: " + s);
        throw e;
      }
    }
  }

  /* subclasses should override this */
  //  @Override
  //  protected void openPorts() {
  //  }

  /* (non-Javadoc)
   * @see com.jpmorrsn.fbp.engine.Network#signalError(com.jpmorrsn.fbp.engine.Component, java.lang.Exception)
   */
  @Override
  void signalError(final Exception e) {
    if (status != StatusValues.ERROR) {
      mother.signalError(e);
      terminate(StatusValues.ERROR);
    }
  }

  @Override
  protected/* (non-Javadoc)
           * @see com.jpmorrsn.fbp.engine.Network#terminate(com.jpmorrsn.fbp.engine.Component.StatusValues)
           */
  void terminate(final Component.StatusValues newStatus) {
    for (Component comp : getComponents().values()) {
      comp.terminate(newStatus);
    }
    status = newStatus;
    interrupt();
  }

  /**
   * Declares input ports not specified in annotations.
   * @param portName the name of the input port
   
  protected void declareInputPort(final String portName) {
    inputPortAttrs.put(portName, new InPort() {

       boolean arrayPort() {
        return false;
      }

       String description() {
        return "";
      }

       Class type() {
        return Object.class;
      }

       String value() {
        return portName;
      }

       boolean fixedSize() {
        return false;
      }

       Class<? extends Annotation> annotationType() {
        return this.getClass();
      }
    });
  }
  */
  /**
   * Declares output ports not specified in annotations.
   * @param portName the name of the output port
   
  protected void declareOutputPort(final String portName) {
    outputPortAttrs.put(portName, new OutPort() {

       boolean arrayPort() {
        return false;
      }

       String description() {
        return "";
      }

       Class type() {
        return Object.class;
      }

       String value() {
        return portName;
      }

       Class<? extends Annotation> annotationType() {
        return this.getClass();
      }

       boolean optional() {
        return false;
      }

       boolean fixedSize() {
        return false;
      }
    });
    */
}
