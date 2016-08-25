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

package com.jpaulmorrison.fbp.core.engine;


/**
 * Instances of this class are thrown whenever a programming error
 * in a flow network is detected.  Nobody is expected to catch these,
 * because they are considered indications of design errors, even though
 * detected only at run-time.
 **/

public class FlowError extends RuntimeException {

  /**
   * Constructs a new FlowError with a useful (but non-localized)
   * message as its text.  FlowErrors without texts are not allowed, as
   * these are considered bad practice.
   * @param text a description of the error
   **/

  static final long serialVersionUID = 362498820763181265L;

  

  FlowError(final String text) {
    super(text);
  }

  /**
   * A convenience method which constructs a new FlowError and
   * throws it at once, typically never returning.
   * @param text a description of the error
   **/

  public static void complain(final String text) {
    throw new FlowError(text);
  }
}
