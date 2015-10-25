/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.engine;


public final class VersionAndTimestamp {

  private static String version = "JavaFBP - version 3.0.2";

  private static String date = "11 Oct., 2015";

  static String getVersion() {
    return version;
  }

  static String getDate() {
    return date;
  }
}
