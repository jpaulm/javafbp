package com.jpmorrsn.fbp.examples.components;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InPorts;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.Packet;

/**
 * Component to execute a loaded component.  Currently it has to have the same metadata as the component being executed...
 * 
 */
@ComponentDescription("Execute a loaded component")
@OutPort("OUT")
@InPorts({@InPort("IN"), @InPort("COMP")})

@InPort("COMP")

public class TabulaRasa extends Component {

	static final String copyright = "Copyright 2007, ..., 2014, J. Paul Morrison.  At your option, you may copy, "
			+ "distribute, or make derivative works under the terms of the Clarified Artistic License, "
			+ "based on the Everything Development Company's Artistic License.  A document describing "
			+ "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
			+ "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

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
		
		Packet<String> p = compPort.receive();
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
