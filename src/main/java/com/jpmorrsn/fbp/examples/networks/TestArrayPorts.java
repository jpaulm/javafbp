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
 

*  Sven Steinseifer - 2010 */

package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.ReplString;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


public class TestArrayPorts {

  public static void main(final String[] args) {
    try {
      new Network() {

        @Override
        protected void define() {
          component("generate", GenerateTestData.class);
          component("replicate", ReplString.class);
          component("discard", Discard.class);

          connect("generate.OUT", "replicate.IN");
          connect("replicate.OUT[1]", "discard.IN");
          initialize("100", "generate.COUNT");
        }
      }.go();
    } catch (Exception e) {
      System.err.println("Exception trapped here");
      e.printStackTrace();
    }
  }
}
