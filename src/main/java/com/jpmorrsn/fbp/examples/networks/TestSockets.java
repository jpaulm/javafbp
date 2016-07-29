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


/**
 * This network uses the ReadFromSocket and WriteToSocket streaming components   
 *
 * Not to be confused with WebSockets! 
 */
import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.components.Passthru;
import com.jpmorrsn.fbp.components.ReadFromSocket;
import com.jpmorrsn.fbp.components.WriteToSocket;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;


public class TestSockets extends Network {

  @Override
  protected void define() {
    component("Gen", GenerateTestData.class);
    component("WS", WriteToSocket.class);
    component("RS", ReadFromSocket.class);
    component("WS2", WriteToSocket.class);
    component("RS2", ReadFromSocket.class);
    //component("Disp", WriteToConsole.class);
    component("Disp", Discard.class);
    component("Passthru", Passthru.class);

    connect("Gen.OUT", "WS.IN");
    //connect WS to RS via socket!
    connect("RS.OUT", "Passthru.IN");
    initialize("10000", component("Gen"), port("COUNT"));
    initialize("localhost, 4444", "WS.PORT");
    initialize("4444", "RS.PORT");

    connect("Passthru.OUT", "WS2.IN");
    //connect WS2 to RS2 via socket!
    connect("RS2.OUT", "Disp.IN");

    initialize("localhost, 4445", "WS2.PORT");
    initialize("4445", "RS2.PORT");
  }

  public static void main(final String[] argv) throws Exception {
    new TestSockets().go();
  }

}
