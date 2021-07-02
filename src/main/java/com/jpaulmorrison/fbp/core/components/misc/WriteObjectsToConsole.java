
package com.jpaulmorrison.fbp.core.components.misc;

	import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jpaulmorrison.fbp.core.engine.Component;
	import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
	import com.jpaulmorrison.fbp.core.engine.InPort;
	import com.jpaulmorrison.fbp.core.engine.InputPort;
	import com.jpaulmorrison.fbp.core.engine.MustRun;
	import com.jpaulmorrison.fbp.core.engine.OutPort;
	import com.jpaulmorrison.fbp.core.engine.OutputPort;
	import com.jpaulmorrison.fbp.core.engine.Packet;


	/**
	 * Component to write data to the console, using a stream of packets. It is
	 * specified as "must run" so that the output file will be cleared even if no
	 * data packets are input.
	 */
	@ComponentDescription("Write stream of packets to console")
	@InPort(value = "IN", description = "Packets to be displayed")
	@OutPort(value = "OUT", optional = true, description = "Output port, if connected")
	@MustRun
	public class WriteObjectsToConsole extends Component {

	  
	  private InputPort inport;

	  private OutputPort outport;

	  @Override
	  protected void execute() {
	    Packet<?> p;

	    while ((p = inport.receive()) != null) {
	      if (p.getType() == Packet.OPEN) {
	        System.out.println("===> Open Bracket");
	      } else if (p.getType() == Packet.CLOSE) {
	        System.out.println("===> Close Bracket");
	      } else {
	    	  printRegularIP(p.getContent()); 
	        //System.out.println((String) p.getContent());
	      }
	     
	      if (outport.isConnected()) {
	        outport.send(p);
	      } else {
	        drop(p);
	      }
	    }

	  }

	  void printRegularIP(Object o) {
		  // Thanks to https://stackoverflow.com/questions/2989560/how-to-get-the-fields-in-an-object-via-reflection
		  String str = "";
		  String delim = "";
		  Object value = null;
		  Class cls = o.getClass();
		  str = cls.getName() + ": {";
		  //for (Field field : cls.getDeclaredFields()) {
		  for (Field field : getAllModelFields(cls)) {
			    field.setAccessible(true); // ???
			    if (field.getName().equals("this$0"))
			    	continue;
				try {
					value = field.get(o);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			    //if (value != null) {
			        //System.out.println(field.getName() + "=" + value);
			    	str += delim + field.getName() + ": " + value;
			    	delim = "; ";
			    //}
			}
		  str += "}";
		  System.out.println(str);
	  }
	  
	  // Thanks, https://stackoverflow.com/users/755804/18446744073709551615 !
	  
	  public static List<Field> getAllModelFields(Class aClass) {
		    List<Field> fields = new ArrayList<>();
		    do {
		        Collections.addAll(fields, aClass.getDeclaredFields());
		        aClass = aClass.getSuperclass();
		    } while (aClass != null);
		    return fields;
		}
	  
	  @Override
	  protected void openPorts() {
	    inport = openInput("IN");

	    outport = openOutput("OUT");

	  }
	}

 
