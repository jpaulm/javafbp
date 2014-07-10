package com.jpmorrsn.fbp.engine;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;


/** A Packet may either contain an Object, when <code> type</type> is <code> NORMAL </code>,
 * or a String, when <code>type</code> is not <code>NORMAL</code>.  The latter case
 * is used for things like open and close brackets (where the String will be the name
 * of a group. e.g. accounts)
*/

public class Packet<T> implements Serializable {

  /* *
     * Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, 
     * distribute, or make derivative works under the terms of the Clarified Artistic License, 
     * based on the Everything Development Company's Artistic License.  A document describing 
     * this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. 
     * THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.
     * */
  static public final int OPEN = 1;

  static public final int CLOSE = 2;

  static public final int NORMAL = 0;

  private final T content;

  private final int type;

  Object owner;

  HashMap<String, Chain> chains = null;

  private HashMap<String, Object> attrs = null;

  // An iteration that has nothing to iterate.               
  private static Iterator nullIter = new HashMap().values().iterator();

  @SuppressWarnings("unchecked")
  Packet(final int newType, final String newName, final Thread newOwner) {
    content = (T) newName;
    setOwner(newOwner);
    type = newType;
  }

  @SuppressWarnings("unchecked")
  Packet(final Object newContent, final Thread newOwner) {
    content = (T) newContent;
    setOwner(newOwner);
    type = NORMAL;
  }

  @Deprecated
  // this function has been moved to Component
  public void attach(final String name, final Packet subordinate) {
    if (subordinate == null) {
      FlowError.complain("Null packet reference in 'attach' method call: " + Thread.currentThread().getName());
    }
    Packet p = this;
    while (p.owner instanceof Packet) {
      if (p == subordinate) {
        FlowError.complain("Loop in tree structure");
      }
      p = (Packet) p.owner;
    }
    if (p == subordinate) {
      FlowError.complain("Loop in tree structure");
    }
    if (p.owner != Thread.currentThread()) {
      FlowError.complain("Packet not owned (directly or indirectly) by current component");
    }
    if (subordinate.owner != Thread.currentThread()) {
      FlowError.complain("Subordinate packet not owned by current component");
    }
    if (chains == null) {
      chains = new HashMap<String, Chain>();
    }
    Chain chain = chains.get(name);
    if (chain == null) {
      chain = new Chain(name);
      chains.put(name, chain);
    }

    subordinate.setOwner(this);
    chain.members.add(subordinate);
  }

  /** Clear the owner of a Packet, and reduce the number of Packets owned by the owner
  *  (if owner is a Component) - if Packet is chained, owner is Chain
  */

  void clearOwner() {
    if (owner instanceof Component) {
      Component c = (Component) owner;
      c.packetCount--;

    }
    owner = null;
  }

  /** Detach Packet from named chain
  */

  @Deprecated
  // this function has been moved to Component
  public void detach(final String name, final Packet subordinate) {
    if (subordinate == null) {
      FlowError.complain("Null packet reference in 'detach' method call: " + Thread.currentThread().getName());
    }
    Packet root = getRoot();
    if (root.owner != Thread.currentThread()) {
      FlowError.complain("Packet not owned (directly or indirectly) by current component");
    }
    if (chains == null || null == chains.get(name)) {
      FlowError.complain("Named chain does not exist: " + name + " (" + Thread.currentThread().getName() + ")");
    }
    Chain chain = chains.get(name);
    if (!chain.members.remove(subordinate)) {
      FlowError.complain("Object not found on " + name + ": " + Thread.currentThread().getName());
    }
    subordinate.setOwner(root.owner);
    return;
  }

  /** Get named attribute of Packet - may be any Object
  */
  Object getAttribute(final String key) {
    if (attrs != null) {
      return attrs.get(key);
    }

    return null;
  }

  /** Get all attributes of this Packet (as Iterator)
  */
  protected Iterator getAttributes() {
    if (attrs != null) {
      return attrs.keySet().iterator();
    }

    return nullIter;
  }

  /** Get named chain (as Iterator)
  */

  protected Iterator getChain(final String name) {
    if (chains == null) {
      return nullIter;
    }
    Chain chain = chains.get(name);
    if (chain != null) {
      return chain.members.iterator();
    }

    return nullIter;
  }

  /** Get all chains for this Packet (as Iterator)
  */

  protected Iterator getChains() {
    if (chains != null) {
      return chains.keySet().iterator();
    }

    return nullIter;
  }

  /** Get contents of this Packet - may be any Object
  */

  public T getContent() {
    //if (type == NORMAL)
    return content;
    // else
    // return null;
  }

  private String getName() {
    if (type == NORMAL) {
      return null;
    }

    return (String) content;
  }

  /** Get root of this Packet - it follows the Packet owner chain up until
  * it finds a Packet that is owned by a Component rather than by a Packet
  * bug fixed Mar. 18, 2012
  */

  public Packet getRoot() {
    Packet p = this;
    while (p.owner instanceof Packet) {
      p = (Packet) p.owner;
    }
    return p;
  }

  /** This method returns the type of a Packet
  */

  public int getType() {
    return type;
  }

  /** Make an Object a named attribute of a Packet
  */

  protected void putAttribute(final String key, final Object value) {

    if (attrs == null) {
      attrs = new HashMap<String, Object>();
    }
    attrs.put(key, value);
  }

  /** Remove a named attribute from a Packet (does not return the attribute)
  */

  protected void removeAttribute(final String key) {
    if (attrs != null) {
      attrs.remove(key);
    }
  }

  /** Change the owner of a Packet - if the owner is a Component,
  * increment the number of Packets owned by that Component
  * (when the Component is deactivated, it must no longer own any Packets)
  */

  void setOwner(final Object newOwner) {
    clearOwner();
    owner = newOwner;
    if (owner instanceof Component) {
      Component c = (Component) owner;
      c.packetCount++; // count of owned packets

    }
  }

  @Override
  public String toString() {
    String value = "null";
    final String names[] = { "NORMAL", "OPEN", "CLOSE" };
    if (getType() == NORMAL) {
      Object obj = getContent();
      if (obj != null) {
        value = obj.toString();
      }
    } else {
      value = names[getType()];
      value += "; " + getName();
    }
    return String.format("%1$s", value);
  }
}
