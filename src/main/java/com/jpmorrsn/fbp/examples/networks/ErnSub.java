package com.jpmorrsn.fbp.examples.networks; // Change as required 


import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.SubNet;
import com.jpmorrsn.fbp.engine.SubOut;
import com.jpmorrsn.fbp.examples.components.Ern2;


//Test for Ernesto's Subnet array ports

@OutPort(valueList = { "OUTCHINFO_*", "OUTCHDATA_*" }, setDimension = 2, description = "Subnet info exit point", type = Object.class)
public class ErnSub extends SubNet {

  @Override
  protected void define() throws Exception {

    //component("DLPIO8_Ch_Manifold_0", Ern1.class); 
    component("DLPIO8_Ch_Manifold_0", Ern2.class);
    for (int i = 0; i < 2; i++) {
      component("SUBOUT_CHINFO_" + i, SubOut.class);
      component("SUBOUT_CHDATA_" + i, SubOut.class);

      connect("DLPIO8_Ch_Manifold_0.OUTCHINFO_" + i, "SUBOUT_CHINFO_0.IN");
      connect("DLPIO8_Ch_Manifold_0.OUTCHDATA[" + i + "]", "SUBOUT_CHDATA_" + i + ".IN");

      initialize("OUTCHINFO_" + i, "SUBOUT_CHINFO_" + i + ".NAME");
      initialize("OUTCHDATA_" + i, "SUBOUT_CHDATA_" + i + ".NAME");
    }
    //connect("DLPIO8_Ch_Manifold_0.OUTCHDATA[5]", "SUBOUT_CHDATA_1.IN");
  }

}
