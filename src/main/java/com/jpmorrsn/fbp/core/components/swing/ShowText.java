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
package com.jpmorrsn.fbp.core.components.swing;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InPorts;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.MustRun;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


/**
 * Component to write data to a Swing pane. The title comes in at another port. It is specified as "must run". 
 * Incoming packets are sent to output port.
 */
@ComponentDescription("Displays packets on Swing EditorPane")
@MustRun
@InPorts({ @InPort(value = "IN", description = "Packets to be displayed", type = String.class),
    @InPort(value = "TITLE", description = "Title string", type = String.class) })
@OutPort(value = "OUT", optional = true, description = "Output port, if connected", type = String.class)
public class ShowText extends Component {

  
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
