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

package com.jpmorrsn.fbp.resourcekit.examples.networks;


import java.io.File;


import com.jpmorrsn.fbp.core.components.io.*;
import com.jpmorrsn.fbp.core.components.routing.KickWD;
import com.jpmorrsn.fbp.core.components.routing.Passthru;
import com.jpmorrsn.fbp.core.components.routing.WriteToConsole;
import com.jpmorrsn.fbp.core.engine.Network;

import com.jpmorrsn.fbp.resourcekit.examples.components.*;
/**
 * Network shown in 3rd DrawFBP Youtube video - https://www.youtube.com/watch?v=-AmzfhV2hIU
 * 
 * Currently (Aug. 7, 2016) - seems to have bug!  Needs fixing!
 * Fixed (Aug. 10, 2016) - insufficient slots in loop connections :-)
 * 
 */


public class ScanDir extends Network {

  
  @Override
  protected void define() {
	component("ListFiles", ListFiles.class);
	component("ReadFile", ReadFile.class);
	component("DeCompose", DeCompose.class);
	component("GenerateWordCounts", GenerateWordCounts.class);
	component("Sort", Sort.class);
	component("Display", WriteToConsole.class);	
	component("KickWData", KickWD.class);	
	component("Passthru", Passthru.class);
	component("JFilter", JFilter.class);
	
    connect("ListFiles.FILES", "JFilter.IN");
    connect("JFilter.OUT", "ReadFile.SOURCE");
    connect("KickWData.OUT", "ListFiles.SOURCE");
    connect("ListFiles.DIRS", "Passthru.IN", 1000);
    connect("Passthru.OUT", "ListFiles.SOURCE", 1000);
    connect("ReadFile.OUT", "DeCompose.IN");
    initialize("src/main".replace("/", File.separator), "KickWData.SOURCE");    
    connect("DeCompose.OUT","GenerateWordCounts.IN");
    connect("GenerateWordCounts.OUT","Sort.IN");
    connect("Sort.OUT","Display.IN");
    
  }

  public static void main(final String[] argv) throws Throwable {	
    new ScanDir().go();
  }
}
