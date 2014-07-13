package com.jpmorrsn.fbp.components;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to read data from an Inbox file, generating a stream of emails. The file
 * name is specified as a String via an InitializationConnection.
 */
@ComponentDescription("Generate stream of emails from I/O file")
@OutPort(value = "OUT", description = "Generated emails", type = String.class)
@InPort(value = "SOURCE", description = "File name", type = String.class)
public class ReadEmails extends Component {

  static final String copyright = "Copyright 2007, 2008, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private OutputPort outport;

  private InputPort source;

  @Override
  protected void execute() {
    Packet rp = source.receive();
    if (rp == null) {
      return;
    }
    source.close();

    String sf = (String) rp.getContent();
    try {
      drop(rp);
      FileInputStream in = new FileInputStream(new File(sf));
      BufferedReader b = new BufferedReader(new InputStreamReader(in));

      String s, t, u = "";
      int i, j, k;
      char[] bs = new char[1000000];
      while (true) {
        i = b.read(bs, 0, bs.length);
        if (i == -1) {
          break;
        }
        s = new String(bs, 0, i);
        j = 0;
        while (true) {
          k = s.indexOf("\nFrom - ", j);
          if (k == -1) {
            break;
          }
          t = s.substring(j, k + 1);
          Packet p = create(u + t);
          u = "";
          outport.send(p);
          j = k + 1;
        }
        //u = u + new String(bs, j, i - j);
        if (u.length() == 0) {
          u = new String(bs, j, i - j); //fudge to save space
        }
      }
      b.close();
    } catch (IOException e) {
      System.out.println("I/O Error on file: " + sf);
    }
  }

  @Override
  protected void openPorts() {

    outport = openOutput("OUT");

    source = openInput("SOURCE");

  }
}
