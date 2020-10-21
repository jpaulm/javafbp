package com.jpaulmorrison.fbp.resourcekit.experimental.components;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/*
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
*/

import java.util.HashMap;
 
import com.google.gson.Gson;
import com.jpaulmorrison.fbp.core.engine.Component; // Using 'Connection', 'Statement' and 'ResultSet' classes in java.sql package
import com.jpaulmorrison.fbp.core.engine.ComponentDescription;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InPorts;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;
import com.jpaulmorrison.fbp.core.engine.SubIn;
import com.jpaulmorrison.fbp.core.engine.SubNet;
import com.jpaulmorrison.fbp.core.engine.SubOut;
 
@ComponentDescription("Reads table from MySQL and outputs result")
@InPorts({ @InPort(value = "DATABASE", description = "Database name", type = String.class),
		@InPort(value = "USER", description = "User name", type = String.class),
		@InPort(value = "CLASS", description = "Object class", type = String.class),
		@InPort(value = "FIELDS", description = "Field correspondences", type = String.class),
		@InPort(value = "PSWD", description = "Password obtained from file", type = String.class) })
@OutPort(value = "OUT", description = "Table rows")
 


public class ReadJDBC extends SubNet {
String description = "ReadJDBC   subnet";
protected void define() { 
  component("ReadJDBC",com.jpaulmorrison.fbp.core.components.jdbc.ReadJDBC.class); 
  component("SUBIN",SubIn.class); 
  initialize("DATABASE", component("SUBIN"), port("NAME")); 
  component("SUBIN_2_",SubIn.class); 
  initialize("USER", component("SUBIN_2_"), port("NAME")); 
  component("SUBIN_3_",SubIn.class); 
  initialize("CLASS", component("SUBIN_3_"), port("NAME")); 
  component("SUBOUT",SubOut.class); 
  initialize("OUT", component("SUBOUT"), port("NAME")); 
  component("SUBIN_4_",SubIn.class); 
  initialize("FIELDS", component("SUBIN_4_"), port("NAME")); 
  component("SUBIN_5_",SubIn.class); 
  initialize("PSWD", component("SUBIN_5_"), port("NAME")); 
  connect(component("SUBIN_3_"), port("OUT"), component("ReadJDBC"), port("CLASS")); 
  connect(component("ReadJDBC"), port("OUT"), component("SUBOUT"), port("IN" )); 
  connect(component("SUBIN_4_"), port("OUT"), component("ReadJDBC"), port("FIELDS")); 
  connect(component("SUBIN_5_"), port("OUT"), component("ReadJDBC"), port("PSWD")); 
  connect(component("SUBIN"), port("OUT"), component("ReadJDBC"), port("DATABASE")); 
  connect(component("SUBIN_2_"), port("OUT"), component("ReadJDBC"), port("USER")); 
} 
	
}
