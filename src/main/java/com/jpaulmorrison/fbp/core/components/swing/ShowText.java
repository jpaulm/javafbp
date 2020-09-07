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
package com.jpaulmorrison.fbp.core.components.swing;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InPorts;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.MustRun;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;

/**
 * Component to write data to a Swing pane. The title comes in at another port.
 * It is specified as "must run". Incoming packets are sent to output port.
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

	JTextPane pane = null;
	DefaultStyledDocument doc = new DefaultStyledDocument();
	StyleContext sc = new StyleContext();

	Style style = sc.getStyle(StyleContext.DEFAULT_STYLE);

	@Override
	protected void execute() {

		Packet<?> tp = titleport.receive();
		if (tp != null) {
			title = (String) tp.getContent();
			drop(tp);
			titleport.close();
		}

		jframe = new JFrame(title);
		// pane = new JTextPane("text/plain", "");

		pane = new JTextPane(doc);

		pane.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(pane);
		jframe.add(scrollPane);
		jframe.setSize(600, 400);
		jframe.setVisible(true);
		jframe.setLocation(100, 50);
		jframe.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent ev) {
				jframe.dispose();
			}
		});

		Packet<?> p;

		// DefaultStyledDocument doc = new DefaultStyledDocument();

		StyleConstants.setForeground(style, Color.BLUE);
		StyleConstants.setFontSize(style, 14);

		while ((p = (Packet<?>) inport.receive()) != null) {
			String s = (String) p.getContent() + '\n';
			try {
				doc.insertString(doc.getLength(), s, style);
				doc.setCharacterAttributes(0, doc.getLength(), style, false);
			} catch (BadLocationException exc) {
				exc.printStackTrace();
			}

			if (outport.isConnected()) {
				outport.send(p);
			} else {
				drop(p);
			}

		}

	}

	@Override
	protected void openPorts() {
		inport = openInput("IN");

		outport = openOutput("OUT");
		titleport = openInput("TITLE");

	}
}
