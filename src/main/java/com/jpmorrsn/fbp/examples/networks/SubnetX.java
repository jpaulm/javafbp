package com.jpmorrsn.fbp.examples.networks; // Change as required 


/* Subnet used in one of the test cases
 */

import com.jpmorrsn.fbp.components.Passthru;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.SubInSS;
import com.jpmorrsn.fbp.engine.SubNet;
import com.jpmorrsn.fbp.engine.SubOutSS;


@OutPort("OUT")
@InPort("IN")
public class SubnetX extends SubNet {

  static final String copyright = "Copyright 1999, 2000, 2001, 2002, ....";

  @Override
  protected void define() {

    component("SUBIN", SubInSS.class);
    component("SUBOUT", SubOutSS.class);
    component("Pass", Passthru.class);

    initialize("IN", component("SUBIN"), port("NAME"));
    connect(component("SUBIN"), port("OUT"), component("Pass"), port("IN"));
    connect(component("Pass"), port("OUT"), component("SUBOUT"), port("IN"));
    initialize("OUT", component("SUBOUT"), port("NAME"));

  }
}