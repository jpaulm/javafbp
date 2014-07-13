package com.jpmorrsn.fbp.examples.networks; // Change as required 


import java.io.File;

import com.jpmorrsn.fbp.components.Collate;
import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;


/** This is really the front end of an Update app - instead of routing the merged stream 
 * to a processing component, we just display it.
 * 
 * @author HP_Administrator
 *
 */
public class Update extends Network {

  @Override
  protected void define() {
    //tracing = true;
    component("Read Master", ReadFile.class);
    component("Read Details", ReadFile.class);
    component("Collate", Collate.class);
    component("Display", WriteToConsole.class);
    connect(component("Read Master"), port("OUT"), component("Collate"), port("IN[0]"));
    connect(component("Read Details"), port("OUT"), component("Collate"), port("IN[1]"));
    connect(component("Collate"), port("OUT"), component("Display"), port("IN"));
    initialize("testdata/mfile".replace("/", File.separator), component("Read Master"), port("SOURCE"));
    initialize("testdata/dfile".replace("/", File.separator), component("Read Details"), port("SOURCE"));

    initialize("3, 2, 5", component("Collate"), port("CTLFIELDS"));

  }

  public static void main(final String[] argv) throws Exception {
    new Update().go();
  }

}