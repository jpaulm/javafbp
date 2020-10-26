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
package com.jpaulmorrison.fbp.core.components.routing;


import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


@ComponentDescription("Force crash")
@InPort("IN")
public class Crash extends Component {

 /** 
  * Force crash
  * 
  */
  private InputPort inport;

  @SuppressWarnings("null")
  @Override
  protected void execute() {
    Packet<?> p = inport.receive();
    Object foo = new Object();
    System.err.println("Foo");
    foo = null;
    System.err.println(foo.toString());
    drop(p);

  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");
    //		inport.setType(Object.class);

  }
}
