package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.Sort;
import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.text.Affix;
import com.jpmorrsn.fbp.text.DedupeSuccessive;


/** This network is intended for timing runs */

public class TextMerge extends Network {

  static final String copyright = "Copyright 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {

    /* This network takes a list of names and generates greetings
    */

    component("Read mixed names", ReadFile.class);
    component("Sort names", Sort.class); // can only handle up to 9999 names
    component("Remove duplicate names", DedupeSuccessive.class);
    component("Merge names into greetings", Affix.class);
    component("Send messages", WriteToConsole.class);
    connect("Read mixed names.OUT", "Sort names.IN");
    connect("Sort names.OUT", "Remove duplicate names.IN");
    connect("Remove duplicate names.OUT", "Merge names into greetings.IN");
    connect("Merge names into greetings.OUT", "Send messages.IN");

    initialize("C:\\Users\\Public\\TextMergeNames.txt", "Read mixed names.SOURCE");
    initialize("Hello, ", "Merge names into greetings.PRE");
    initialize(", how are you today?", "Merge names into greetings.POST");

  }

  public static void main(final String[] argv) throws Exception {
    new TextMerge().go();
  }
}
