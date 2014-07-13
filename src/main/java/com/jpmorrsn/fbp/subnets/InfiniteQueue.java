package com.jpmorrsn.fbp.subnets;


/*
 * Copyright (C) J.P. Morrison Enterprises, Ltd. 2009, 2014 All Rights Reserved. 
 */

import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.SubIn;
import com.jpmorrsn.fbp.engine.SubNet;
import com.jpmorrsn.fbp.engine.SubOut;


@ComponentDescription("Component to store large number of packets temporarily on disk")
@InPorts({ @InPort("IN"), @InPort("TEMPFILENAME") })
@OutPort("OUT")
public class InfiniteQueue extends SubNet {

  String description = " Infinite Queue";

  @Override
  protected void define() {
    component("__  Write", com.jpmorrsn.fbp.components.WriteFile.class);
    component("__  Read_", com.jpmorrsn.fbp.components.ReadFile.class);
    component("SUBOUT", SubOut.class);
    initialize("OUT", component("SUBOUT"), port("NAME"));
    component("SUBIN", SubIn.class);
    initialize("IN", component("SUBIN"), port("NAME"));
    component("SUBIN_2", SubIn.class);
    initialize("TEMPFILENAME", component("SUBIN_2"), port("NAME"));
    component("_ Replicate", com.jpmorrsn.fbp.components.ReplString.class);
    connect(component("SUBIN"), port("OUT"), component("__  Write"), port("IN"));
    connect(component("SUBIN_2"), port("OUT"), component("_ Replicate"), port("IN"));
    connect(component("_ Replicate"), port("OUT[0]"), component("__  Write"), port("DESTINATION"));
    connect(component("_ Replicate"), port("OUT[1]"), component("__  Read_"), port("SOURCE"));
    connect(component("__  Read_"), port("OUT"), component("SUBOUT"), port("IN"));
    connect(component("__  Write"), port("*"), component("__  Read_"), port("*"));
  }

}
