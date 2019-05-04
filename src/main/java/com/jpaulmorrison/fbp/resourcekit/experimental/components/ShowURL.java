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

package com.jpaulmorrison.fbp.resourcekit.experimental.components;


import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;


/**
 * Component to drive a browser given the URL.
 */
@ComponentDescription("Component to drive a browser")
@OutPort("OUT")
@InPort("IN")
public class ShowURL extends Component {
  

  private InputPort inport;

  private OutputPort outport;

  private static final Object basicServiceObject = getBasicServiceObject();

  private static final Class<?> basicServiceClass = getBasicServiceClass();

  @Override
  protected void execute() {
    Packet p;
    URL url = null;
    try {
      // http://forums.sun.com/thread.jspa?threadID=5287628&tstart=195
      //URL url = new URL("http://www.jpaulmorrison.com/fbp/tryform.htm");
      //url = new URL("file://C:/Users/Paul/Documents/Business/tryform.htm"); // works
      url = new URL("https://api.github.com/users/defunkt");
    } catch (MalformedURLException e) {
      //do nothing
    }
    //File file = new File("/Documents and Settings/HP_Administrator/My Documents/Business/fbp/tryform.htm");
    //URI uri = new URI("com/jpaulmorrison/javaFBP/resources/tryform.htm");
    //URL url = file.toURI().toURL();  
    //URL url = uri.toURL();  // try to access resource 
    while ((p = inport.receive()) != null) {
      showDocument(url);
      outport.send(p);
    }

  }

  public static boolean showDocument(final URL url)

  {
    if (basicServiceObject == null) {
      return false;
    }

    try {
      Method method = basicServiceClass.getMethod("showDocument", new Class<?>[] { URL.class });

      Boolean resultBoolean = (Boolean) method.invoke(basicServiceObject, new Object[] { url });

      return resultBoolean.booleanValue();
    } catch (Exception ex) {
      ex.printStackTrace();

      throw new RuntimeException(ex.getMessage());
    }
  }

  private static Object getBasicServiceObject()

  {
    try {
      Class<?> serviceManagerClass = Class.forName("javax.jnlp.ServiceManager");

      Method lookupMethod = serviceManagerClass.getMethod("lookup", new Class<?>[] { String.class });

      return lookupMethod.invoke(null, new Object[] { "javax.jnlp.BasicService" });
    } catch (Exception ex) {
      return null;
    }
  }

  private static Class<?> getBasicServiceClass()

  {
    try {
      return Class.forName("javax.jnlp.BasicService");
    } catch (Exception ex) {
      return null;
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");
  }
}
