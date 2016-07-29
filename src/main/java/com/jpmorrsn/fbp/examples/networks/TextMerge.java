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
import com.jpmorrsn.fbp.text.Affix;
import com.jpmorrsn.fbp.text.DedupeSuccessive;


/** This network is intended for timing runs */

public class TextMerge extends Network {


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
