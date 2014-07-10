package com.jpmorrsn.fbp.components;


import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.FlowError;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;
import com.jpmorrsn.fbp.engine.SelfStarting;


/** Generate a stream of IP counts under control of a timer
*/
@ComponentDescription("Generates stream of counts under control of a timer")
@OutPort(value = "OUT", description = "Generated stream of counts", type = String.class)
@InPorts({ @InPort(value = "CLSDN", description = "Closedown signal"),
    @InPort(value = "INTVL", description = "Interval in seconds", type = String.class) })
//@MustRun
@SelfStarting
public class DispIPCounts extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private OutputPort outport;

  private InputPort intvl, clsdn;

  @Override
  protected void execute() {
    Packet itp = intvl.receive();
    if (itp == null) {
      FlowError.complain("No interval specified for DispIPCounts component");
      return;
    }
    intvl.close();

    String iti = (String) itp.getContent();
    iti = iti.trim();
    long it = 0;
    try {
      it = Long.parseLong(iti);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    drop(itp);

    while (true) {
      try {
        sleep(it); //  sleep 'it' msecs
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      for (Map.Entry<String, BigInteger> kvp : network.getIPCounts().entrySet()) {
        String s = kvp.getKey() + "                                                  ";
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd'T'HH:mm:ss:SSS");
        String t = kvp.getValue().toString();
        t = "                       " + t;
        int i = t.length();
        t = t.substring(i - 12, i);
        Packet p = create(df.format(date) + " " + s.substring(0, 40) + t);
        outport.send(p);
      }

      if (clsdn.isClosed()) {
        break;
      }
    }

  }

  @Override
  protected void openPorts() {
    outport = openOutput("OUT");
    intvl = openInput("INTVL"); // interval in secs
    clsdn = openInput("CLSDN"); // closedown signal
  }
}
