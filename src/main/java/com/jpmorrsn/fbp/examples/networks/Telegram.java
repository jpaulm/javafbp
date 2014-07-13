/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.engine.Network;


public class Telegram extends Network {

  String description = "http://www.jpaulmorrison.com/cgi-bin/wiki.pl?TelegramProblem";

  @Override
  protected void define() {
    component("Read file", com.jpmorrsn.fbp.components.ReadFile.class);
    component("Decompose_to words", com.jpmorrsn.fbp.text.LineToWords.class);
    component("Recompose_to lines", com.jpmorrsn.fbp.text.WordsToLine.class);
    component("Write lines", com.jpmorrsn.fbp.components.WriteToConsole.class);
    component("Count_words", com.jpmorrsn.fbp.components.Counter.class);
    component("Append_word count", com.jpmorrsn.fbp.components.Concatenate.class);
    connect(component("Read file"), port("OUT"), component("Decompose_to words"), port("IN"));
    initialize("JavaFBPProperties.xml", component("Read file"), port("SOURCE"));
    initialize("20", component("Recompose_to lines"), port("MEASURE"));
    connect(component("Decompose_to words"), port("OUT"), component("Count_words"), port("IN"));
    connect(component("Count_words"), port("OUT"), component("Recompose_to lines"), port("IN"));
    connect(component("Recompose_to lines"), port("OUT"), component("Append_word count"), port("IN[0]"));
    connect(component("Append_word count"), port("OUT"), component("Write lines"), port("IN"));
    connect(component("Count_words"), port("COUNT"), component("Append_word count"), port("IN[1]"));

  }

  public static void main(final String[] argv) throws Exception {
    new Telegram().go();
  }

}
