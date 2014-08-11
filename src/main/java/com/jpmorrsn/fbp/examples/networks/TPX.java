package com.jpmorrsn.fbp.examples.networks; // Change as required 


import java.io.File;

import com.jpmorrsn.fbp.components.ReadFile;
import com.jpmorrsn.fbp.components.RegExReplace;
import com.jpmorrsn.fbp.components.WriteFile;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.BuildBlob;


/* This test tests RegExReplace
 */
public class TPX extends Network {

  static final String copyright = "Copyright 2007, 2008, 2011, ....";

  @Override
  protected void define() {
    connect(component("Read", ReadFile.class), port("OUT"), component("BB", BuildBlob.class), port("IN"));
    initialize("C:\\Users\\Paul\\Documents\\Business\\FBP\\book_orig.xhtml,UTF8".replace("\\", File.separator),
        component("Read"), port("SOURCE"));
    component("Write", WriteFile.class);
    component("RER0", RegExReplace.class);
    component("RER1", RegExReplace.class);
    component("RER2", RegExReplace.class);
    component("RER3", RegExReplace.class);
    component("RER31", RegExReplace.class);
    component("RER32", RegExReplace.class);
    component("RER33", RegExReplace.class);
    //component("RER4", RegExReplace.class);
    //component("RER5", RegExReplace.class);
    //component("RER6", RegExReplace.class);
    connect("BB.OUT", "RER0.IN");
    connect("RER0.OUT", "RER1.IN");
    connect("RER1.OUT", "RER2.IN");
    connect("RER2.OUT", "RER3.IN");
    connect("RER3.OUT", "RER31.IN");
    connect("RER31.OUT", "RER32.IN");
    connect("RER32.OUT", "RER33.IN");
    //connect("RER33.OUT", "RER4.IN");
    //connect("RER4.OUT", "RER5.IN");
    //connect("RER5.OUT", "RER6.IN");
    connect("RER33.OUT", "Write.IN");
    initialize("C:\\Users\\Paul\\Documents\\Business\\FBP\\book.html,UTF8".replace("\\", File.separator),
        component("Write"), port("DESTINATION"));

    // non-breaking blanks -> blanks
    initialize("|\\u00a0| ", component("RER0"), port("MASKS"));

    // Chapter 17 – page <a href="#refpoml">1</a>   ->  <a href="#refpoml">Chapter 17</a>
    initialize("|(Chapter\\s+\\d+)\\s+\\–\\s+page\\s*\\<a\\s(href=\"#ref\\w+\")\\>1\\<\\/a\\>|\\<a $2\\>$1\\<\\/a\\>",
        component("RER1"), port("MASKS"));

    // Chapter 17 (page <a href="#refpoml">1</a>)   ->  <a href="#refpoml">Chapter 17</a>  ... Chapter 26  (page <a href="#refrel">1</a>)
    initialize("|(Chapter\\s+\\d+)\\s+\\(page\\s*\\<a\\s(href=\"#ref\\w+\")\\>1\\<\\/a\\>\\)|\\<a $2\\>$1\\<\\/a\\>",
        component("RER2"), port("MASKS"));

    // Chapter 17, page <a href="#refpoml">1</a>   ->  <a href="#refpoml">Chapter 17</a>
    initialize("|(Chapter\\s+\\d+)\\s*\\,\\s+page\\s*\\<a\\s(href=\"#ref\\w+\")\\>1\\<\\/a\\>|\\<a $2\\>$1\\<\\/a\\>",
        component("RER3"), port("MASKS"));

    //p. or pp. <a href="#reftablelookup">1<\\/a>   ->  <a href="#reftablelookup">click here<\\/a>
    initialize("|p+.\\s+\\<a\\s(href=\"#ref\\w+\")\\>1\\<\\/a\\>|\\<a $1\\>click here\\<\\/a\\>", component("RER31"),
        port("MASKS"));

    //xxxxx (page <a href="#refperform">1<\\/a>)  ->  <a href="#refperform">xxxxx<\\/a>
    initialize("|(\\w+)\\s+\\(page\\s*\\<a\\s(href=\"#ref\\w+\")\\>1\\<\\/a\\>\\)|\\<a $2\\>$1\\<\\/a\\>",
        component("RER32"), port("MASKS"));

    //xxxxx - page <a href="#refperform">1<\\/a>   ->  <a href="#refperform">xxxxx<\\/a>
    initialize("|(\\w+)\\s+\\–\\s+page\\s*\\<a\\s(href=\"#ref\\w+\")\\>1\\<\\/a\\>|\\<a $2\\>$1\\<\\/a\\>",
        component("RER33"), port("MASKS"));

    //<a href="#reftablelookup">1<\\/a>
    //initialize("|<a//s+(href=\"#ref\\w+\")>1<\\/a>|<a $1>click here<\\/a>", component("RER32"), port("MASKS"));

    // initialize("|“(.+?)”|&ldquo;$1&rdquo;", component("RER4"), port("MASKS"));
    // initialize("|’|&rsquo;", component("RER5"), port("MASKS"));
    // initialize("|–|&ndash;", component("RER6"), port("MASKS"));
  }

  public static void main(final String[] argv) throws Exception {
    new TPX().go();
  }
}
