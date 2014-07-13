/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.networks;

import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.Sort;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.LDIFScan;

public class ProcessLDIFs extends Network {

    String description = "";

    // FIXME: files in this network need replacing by existing files: Paul?
    @Override
    protected void define() /* throws Throwable */{
	component("Read LDIF file_", ReadFile.class);
	component("Scan LDIF file", LDIFScan.class);
	component("Display", WriteToConsole.class);
	component("Another LDIF file", ReadFile.class);
	component("Scan LDIF file_", LDIFScan.class);
	component("Sort", Sort.class);
	connect(component("Read LDIF file_"), port("OUT"),
		component("Scan LDIF file"), port("IN"));
	initialize("C:\\Temp\\FBP_people.ldif", component("Read LDIF file_"),
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
	initialize("C:\\Temp\\FBP_list.ldif", component("Another LDIF file"),
		port("SOURCE"));
	initialize("FBP_list", component("Scan LDIF file_"), port("LABEL"));

    }

    public static void main(final String[] argv) throws Exception {
	new ProcessLDIFs().go();
    }

}
