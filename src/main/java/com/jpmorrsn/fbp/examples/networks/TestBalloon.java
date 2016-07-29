package com.jpmorrsn.fbp.examples.networks;


import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


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

public class TestBalloon extends Network {

 
  @Override
  protected void define() {

    connect(component("Gene\\ra\"te", GenerateTestData.class), port("OUT"),
        component("Balloon", com.jpmorrsn.fbp.engine.Balloon.class), port("IN"));

    connect(component("Balloon"), port("OUT"),
        component("Check", com.jpmorrsn.fbp.examples.components.CheckBallooning.class), port("IN"), 1);

    initialize("200", component("Gene\\ra\"te"), port("COUNT"));

  }

  public static void main(final String[] argv) throws Exception {
    new TestBalloon().go();
  }
}
