/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2016 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.engine;


public final class VersionAndTimestamp {

  private static String version = "JavaFBP - version 3.0.4";

  private static String date = "19 Apr., 2016";

  static String getVersion() {
    return version;
  }

  static String getDate() {
    return date;
  }
}
