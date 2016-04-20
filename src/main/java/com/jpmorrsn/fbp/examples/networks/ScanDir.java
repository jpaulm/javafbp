package com.jpmorrsn.fbp.examples.networks;


import java.io.File;

import com.jpmorrsn.fbp.components.KickWD;
import com.jpmorrsn.fbp.components.ListFiles;
import com.jpmorrsn.fbp.components.Passthru;
import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.Sort;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.DeCompose;
import com.jpmorrsn.fbp.examples.components.GenerateWordCounts;
import com.jpmorrsn.fbp.examples.components.JFilter;


public class ScanDir extends Network {

  static final String copyright = "Copyright 1999, 2000, 2001, 2016, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

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
    connect("ListFiles.DIRS", "Passthru.IN");
    connect("Passthru.OUT", "ListFiles.SOURCE");
    connect("ReadFile.OUT", "DeCompose.IN");
    initialize("C:\\Users\\Paul\\Documents\\GitHub\\drawfbp\\src\\main".replace("/", File.separator), "KickWData.SOURCE");    
    connect("DeCompose.OUT","GenerateWordCounts.IN");
    connect("GenerateWordCounts.OUT","Sort.IN");
    connect("Sort.OUT","Display.IN");
    
  }

  public static void main(final String[] argv) throws Throwable {	
    new ScanDir().go();
  }
}
