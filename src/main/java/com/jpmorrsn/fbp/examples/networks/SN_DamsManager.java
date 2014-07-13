package com.jpmorrsn.fbp.examples.networks; // Change as required 


import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.SubIn;
import com.jpmorrsn.fbp.engine.SubNet;
import com.jpmorrsn.fbp.engine.SubOut;
import com.jpmorrsn.fbp.examples.components.Dlpio8ChsManifold;


//Test for Ernesto's Subnet "weird" problem!

/* FBP component annotations

 */

@InPort(value = "IN_DBDAMINFO", description = "Subnet entry point", type = Object.class)
//@OutPort(valueList = {"OUT_CHINFO_0","OUT_CHINFO_1","OUT_CHDATA_0","OUT_CHDATA_1"},
//description = "Subnet exit points", type = Object.class, optional = true)
@OutPort(valueList = { "OUT_CHINFO*", "OUT_CHDATA*" }, setDimension = 2, description = "Subnet Manifold - exit points", type = Object.class, optional = true)
/**

* Subnet to initialise and run the various DAMs

*/
public class SN_DamsManager extends SubNet {

  //  static final String copyright =
  //         "Copyright 2010, 2011 Technabling Ltd."
  //         + "Based on software developed by J. Paul Morrison (JavaFBP)";

  @Override
  protected void define() {

    component("SubIn", SubIn.class);

    for (int i = 0; i < 2; i++) {
      component("SubOutInfo" + i, SubOut.class);
      component("SubOutData" + i, SubOut.class);
    }

    component("DLPIO8_ChManifold0", Dlpio8ChsManifold.class); //  Dlpio8ChsManifold.class); 

    /* Initialise the subnet */

    initialize("IN_DBDAMINFO", "SubIn.NAME");

    for (int i = 0; i < 2; i++) {
      initialize("OUT_CHINFO" + i, "SubOutInfo" + i + ".NAME");
      initialize("OUT_CHDATA" + i, "SubOutData" + i + ".NAME");
    }

    /* Main connection path. */

    connect("SubIn.OUT", "DLPIO8_ChManifold0.IN");

    for (int i = 0; i < 2; i++) {

      connect("DLPIO8_ChManifold0.OUT_CHINFO[" + i + "]", "SubOutInfo" + i + ".IN");

      connect("DLPIO8_ChManifold0.OUT_CHDATA[" + i + "]", "SubOutData" + i + ".IN");

    }

  }

}