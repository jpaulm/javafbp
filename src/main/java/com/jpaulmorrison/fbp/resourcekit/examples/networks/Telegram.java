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

package com.jpaulmorrison.fbp.resourcekit.examples.networks;



import com.jpaulmorrison.fbp.core.engine.Network;
import com.jpaulmorrison.fbp.core.components.io.ReadFile;
import com.jpaulmorrison.fbp.core.components.misc.WriteToConsole;
import com.jpaulmorrison.fbp.core.components.text.LineToWords;
import com.jpaulmorrison.fbp.core.components.text.WordsToLine;
import com.jpaulmorrison.fbp.core.components.misc.Counter;
import com.jpaulmorrison.fbp.core.components.routing.ConcatStreams;

/** 
 * Solution to Telegram problem, described in several FBP documents, inc. the Wikipedia article
 *
 */
public class Telegram extends Network {

  String description = "http://www.jpaulmorrison.com/cgi-bin/wiki.pl?TelegramProblem";

  @Override
  protected void define() {
    component("Read file", ReadFile.class);
    component("Decompose_to words", LineToWords.class);
    component("Recompose_to lines", WordsToLine.class);
    component("Write lines", WriteToConsole.class);
    component("Count_words", Counter.class);
    component("Append_word count", ConcatStreams.class);
    connect(component("Read file"), port("OUT"), component("Decompose_to words"), port("IN"));
    initialize("src/main/resources/testdata/readme.txt", component("Read file"), port("SOURCE"));
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
