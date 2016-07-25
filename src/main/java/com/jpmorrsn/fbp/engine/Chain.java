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

package com.jpmorrsn.fbp.engine;


import java.util.LinkedList;


/** This is a package-private class which is just used to hold
* chains attached to Packets.  There are no methods here,
* since all the work is being done in class Packet.
* This could have been an inner class of Packet.
*/

final class Chain {

 

  final LinkedList<Packet> members;

  protected final String name;

  Chain(final String n) {
    name = n;
    members = new LinkedList<Packet>();
  }
}
