/*
 * Copyright (C) J.P. Morrison, Enterprises, Ltd. 2009, 2012 All Rights Reserved. 
 */
package com.jpmorrsn.fbp.engine;


public final class VersionAndTimestamp {

  private static String version = "JavaFBP - version 2.7.1";

  private static String date = "12 Jan., 2014";

  static String getVersion() {
    return version;
  }

  static String getDate() {
    return date;
  }
}
