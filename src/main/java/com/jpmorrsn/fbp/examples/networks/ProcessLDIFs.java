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

package com.jpmorrsn.fbp.examples.networks;

import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.Sort;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.LDIFScan;

/** 
 * Network to process LDIF files - two files are converted to fixed format and then sorted together
 *   
 */

public class ProcessLDIFs extends Network {

    String description = "";

    
    @Override
    protected void define() /* throws Throwable */{
	component("Read LDIF file_", ReadFile.class);
	component("Scan LDIF file", LDIFScan.class);
	component("Display", WriteToConsole.class);
	component("Read another LDIF file", ReadFile.class);
	component("Scan LDIF file_", LDIFScan.class);
	component("Sort", Sort.class);
	connect(component("Read LDIF file_"), port("OUT"),
		component("Scan LDIF file"), port("IN"));
	initialize("src/main/resources/testdata/contacts.ldif", component("Read LDIF file_"),
		port("SOURCE"));
	initialize("FBP_people", component("Scan LDIF file"), port("LABEL"));
	connect(component("Sort"), port("OUT"), component("Display"),
		port("IN"));
	connect(component("Scan LDIF file"), port("OUT"), component("Sort"),
		port("IN"));
	connect(component("Another LDIF file"), port("OUT"),
		component("Scan LDIF file_"), port("IN"));
	connect(component("Scan LDIF file_"), port("OUT"), component("Sort"),
		port("IN"));
	initialize("src/main/resources/testdata/contacts.ldif", component("Read another LDIF file"),
		port("SOURCE"));
	initialize("FBP_list", component("Scan LDIF file_"), port("LABEL"));

    }

    public static void main(final String[] argv) throws Exception {
	new ProcessLDIFs().go();
    }

}
