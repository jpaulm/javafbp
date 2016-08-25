/*
 * JavaFBP - A Java Implementation of Flow-Based Programming (FBP)
 * Copyright (C) 2009, 2016 J. Paul Morrison
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, see the GNU Library General Public License v3
 * at https://www.gnu.org/licenses/lgpl-3.0.en.html for more details.
 */
package com.jpaulmorrison.fbp.core.components.io;


import java.io.File;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutPorts;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


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

  
  private OutputPort dirsport, filesport;

  private InputPort source;
  

  @Override
  protected void execute() {
	int dirCount = 1;
	Packet rp = null;
	
    while (null != (rp = source.receive())) { 
    
    dirCount--;

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
    			  dirCount++;
    		  }
    		  else    			  
    			  filesport.send(create(s));    			   
    	  }
    	  
      }
    if (dirCount <= 0) {
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
