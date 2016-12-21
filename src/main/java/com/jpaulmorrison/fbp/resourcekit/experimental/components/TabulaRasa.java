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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InPorts;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.Packet;

/**
 * Component to execute a loaded component.  Currently it has to have the same metadata as the 
 * component being executed, except for COMP... We need to make this more flexible...
 * 
 */
@ComponentDescription("Execute a loaded component")
@OutPort("OUT")
@InPorts({@InPort("IN"), @InPort("COMP")})

@InPort("COMP")

public class TabulaRasa extends Component {

	
	private InputPort compPort;
	
	@Override
	protected void openPorts() {
		compPort = openInput("COMP");	
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void execute() {
		
		Class<Component> cls = null;
		Class cls2 = null;
		Constructor<Component> con = null;
		Object proc = null;
		Field inPorts = null;
		Field outPorts = null;
		
		Packet<String> p =  compPort.receive();
		String compName = p.getContent();
		drop(p);
		compPort.close();
		
		try {
			cls = (Class<Component>) Class.forName(compName);
			con = cls.getConstructor();
			proc = con.newInstance();
			cls2 = cls.getSuperclass(); // Component class
			inPorts = cls2.getDeclaredField("inputPorts");
			inPorts.setAccessible(true);
			outPorts = cls2.getDeclaredField("outputPorts");
			outPorts.setAccessible(true);
			
			// Sets the field to the new value
			inPorts.set(proc, inputPorts);

			// Sets the field to the new value
			outPorts.set(proc, outputPorts);

			Method m = cls2.getDeclaredMethod("openPorts");
			m.setAccessible(true);
			m.invoke(proc);

			m = cls2.getDeclaredMethod("execute");
			m.setAccessible(true);
			m.invoke(proc);

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
