package com.jpmorrsn.fbp.components;


import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to drive a browser.
 */
@ComponentDescription("Component to drive a browser")
@OutPort("OUT")
@InPort("IN")
public class ShowURL extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

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
      url = new URL("file://C:/Users/Paul/Documents/Business/tryform.htm"); // works
    } catch (MalformedURLException e) {
      //do nothing
    }
    //File file = new File("/Documents and Settings/HP_Administrator/My Documents/Business/fbp/tryform.htm");
    //URI uri = new URI("com/jpmorrsn/javaFBP/resources/tryform.htm");
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
