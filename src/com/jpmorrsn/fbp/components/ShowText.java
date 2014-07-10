package com.jpmorrsn.fbp.components;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.MustRun;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;


/**
 * Component to write data to a Swing pane. The title comes in at another port. It is specified as "must run". Incoming packets are
 * sent to output port.
 */
@ComponentDescription("Displays packets on Swing EditorPane")
@MustRun
@InPorts({ @InPort(value = "IN", description = "Packets to be displayed", type = String.class),
    @InPort(value = "TITLE", description = "Title string", type = String.class) })
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
public class ShowText extends Component {

  static final String copyright = "Copyright 2007, 2012, J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic License.  A document describing "
      + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  private InputPort inport, titleport;

  private JFrame jframe;

  private OutputPort outport;

  private String title = "Data Pane";

  private JEditorPane jEditorPane;

  @Override
  protected void execute() {
    Packet tp = titleport.receive();
    if (tp != null) {
      title = (String) tp.getContent();
      drop(tp);
      titleport.close();
    }

    // start up the swing ui in a separate thread ....
    try {
      SwingUtilities.invokeAndWait(new Runnable() {

        public void run() {
          jframe = new JFrame(title);
          jEditorPane = new JEditorPane("text/plain", " ");
          jEditorPane.setEditable(false);
          JScrollPane scrollPane = new JScrollPane(jEditorPane);
          jframe.add(scrollPane);
          jframe.setSize(600, 400);
          jframe.setVisible(true);
          jframe.setLocation(100, 50);
          jframe.addWindowListener(new WindowAdapter() {

            @SuppressWarnings("unused")
            @Override
            public void windowClosing(final WindowEvent ev) {
              jframe.dispose();
            }
          });
        }
      });
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    String contents = "";
    Packet p;
    while ((p = inport.receive()) != null) {
      String s = "" + p.getContent();
      contents += s + "\n";
      if (outport.isConnected()) {
        outport.send(p);
      } else {
        drop(p);
      }
      longWaitStart(5.0); // timeout if over 5 secs
      jEditorPane.setText(contents);
      jframe.update(jframe.getGraphics());
      longWaitEnd();
    }
  }

  @Override
  protected void openPorts() {
    inport = openInput("IN");

    outport = openOutput("OUT");
    titleport = openInput("TITLE");

  }
}
