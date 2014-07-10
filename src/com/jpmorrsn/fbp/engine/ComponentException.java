/*
 * Copied from http://www.java-tips.org/java-se-tips/java.lang/creating-application-specific-exceptions.html 
 * 
 * Thanks!
 */
package com.jpmorrsn.fbp.engine;


public class ComponentException extends Exception {

  private int intError;

  public ComponentException(final int intErrNo) {
    intError = intErrNo;
  }

  public ComponentException(final String strMessage) {
    super(strMessage);
  }

  @Override
  public String toString() {
    return "Component exception - value: " + intError;
  }

  int getValue() {
    return intError;
  }
}
