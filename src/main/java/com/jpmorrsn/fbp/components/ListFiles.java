package com.jpmorrsn.fbp.components;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutPorts;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to step through a directory, generating two streams of packets:
 *   - directories go to port DIRS
 *   - files go to port FILES 
 * The file name is specified as a String via an InitializationConnection.
 */
@ComponentDescription("Generate two streams of packets from directory: directories and files")
@OutPorts({  
  @OutPort(value = "DIRS", description = "Directories", type = String.class),
  @OutPort(value = "FILES", description = "Files", type = String.class)
  })
@InPort(value = "SOURCE", description = "Directory name", type = String.class)

public class ListFiles extends Component {

  static final String copyright = "Copyright 2007, 2008, 2016, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private OutputPort dirsport, filesport;

  private InputPort source;
  

  @Override
  protected void execute() {
	int unProcessedCount = 0;
	Packet rp = null;
	
    while (null != (rp = source.receive())) { 
    
    unProcessedCount--;

    String sf = (String) rp.getContent();
      
      File f = new File(sf);
      if (!(f.isDirectory())) 
    	  filesport.send(rp);
      else {
    	  drop(rp);
    	  String[] list = f.list();
    	  for (int i = 0; i < list.length; i++){    		  
    		  String s = sf + File.separator + list[i];
    		  File f2 = new File(s);
    		  if (f2.isDirectory()) {
    			  dirsport.send(create(s));
    			  unProcessedCount++;
    		  }
    		  else    			  
    			  filesport.send(create(s));    			   
    	  }
    	  
      }
    if (unProcessedCount <= 0) {
    	source.close();
    	return;
    }
    //System.out.println(unProcessedCount + ", " + sf);
  }
  }

  @Override
  protected void openPorts() {

    dirsport = openOutput("DIRS");
    filesport = openOutput("FILES");

    source = openInput("SOURCE");

  }
}
