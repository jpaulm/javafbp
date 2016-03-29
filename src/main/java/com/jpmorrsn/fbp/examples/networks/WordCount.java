package com.jpmorrsn.fbp.examples.networks;


import java.io.File;

import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.Sort;
import com.jpmorrsn.fbp.components.WriteToConsole;
//import com.jpmorrsn.fbp.components.WriteFile;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.DeCompose;
import com.jpmorrsn.fbp.examples.components.GenerateWordCounts;


public class WordCount extends Network {

  static final String copyright = "Copyright 1999, 2000, 2001, 2016, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {
	component("Read", ReadFile.class);
	component("DeCompose", DeCompose.class);
	component("GenerateWordCounts", GenerateWordCounts.class);
	component("Sort", Sort.class);
	component("Display", WriteToConsole.class);
	
    connect("Read.OUT", "DeCompose.IN");
    initialize("testdata/readme.txt".replace("/", File.separator), "Read.SOURCE");
    connect("DeCompose.OUT","GenerateWordCounts.IN");
    connect("GenerateWordCounts.OUT","Sort.IN");
    connect("Sort.OUT","Display.IN");
    
  }

  public static void main(final String[] argv) throws Throwable {
    new WordCount().go();
  }
}
