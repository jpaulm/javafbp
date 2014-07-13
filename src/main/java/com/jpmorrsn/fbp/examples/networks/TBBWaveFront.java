/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.TBBWF;


public class TBBWaveFront extends Network {

  static final String copyright = "Copyright 1999, 2000, 2001, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
    /* Letters are columns and numbers are rows */
    String[] cols = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
    int dim = 5;
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        component(cols[i] + j, TBBWF.class);
      }
    }
    component("LM", WriteToConsole.class);
    for (int i = 0; i < dim; i++) {
      for (int j = 0; j < dim; j++) {
        if (i < dim - 1) {
          connect(cols[i] + j + ".OUT1", cols[i + 1] + j + ".IN"); // OUT1 -> right neighbour
        }
        if (j < dim - 1) {
          connect(cols[i] + j + ".OUT2", cols[i] + (j + 1) + ".IN"); // OUT2 -> below neighbour
        }
        connect(cols[i] + j + ".OUT3", "LM.IN"); // OUT3 -> build matrix
      }
    }
  }

  public static void main(final String[] argv) throws Exception {
    new TBBWaveFront().go();
  }

}
