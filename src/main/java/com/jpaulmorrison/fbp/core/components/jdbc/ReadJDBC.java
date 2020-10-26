package com.jpaulmorrison.fbp.core.components.jdbc;



import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

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

@ComponentDescription("Reads table from MySQL and outputs result")
@InPorts({ @InPort(value = "DATABASE", description = "Database name", type = String.class),
		@InPort(value = "USER", description = "User name", type = String.class),
		@InPort(value = "CLASS", description = "Object class", type = String.class),
		@InPort(value = "FIELDS", description = "Field correspondences", type = String.class),
		@InPort(value = "PSWD", description = "Password obtained from file", type = String.class) })
@OutPort(value = "OUT", description = "Table rows")

public class ReadJDBC extends Component {

	// adapted from
	// https://www.ntu.edu.sg/home/ehchua/programming/java/JDBC_Basic.html

	private OutputPort outPort;

	private InputPort pswdPort;
	private InputPort dBNPort;
	private InputPort userPort;
	private InputPort classPort;
	private InputPort fldsPort;

	
	
	@Override
	protected void execute() throws Exception {
		
		boolean debug = false;

		Packet<?> pp = pswdPort.receive();

		String pswd = (String) pp.getContent();
		drop(pp);
		pswdPort.close();

		pp = dBNPort.receive();

		String dbTable = (String) pp.getContent();
		drop(pp);
		dBNPort.close();

		pp = userPort.receive();

		String user = (String) pp.getContent();
		drop(pp);
		userPort.close();

		pp = classPort.receive();

		String objClass = (String) pp.getContent();
		drop(pp);
		classPort.close();
		
		
		//Class<?> curClass = cls;
		Class<?> dataClass = Class.forName(objClass);
		
		String[] iipContents = dbTable.split("!", 2);

		pp = fldsPort.receive();

		String fldsStr = (String) pp.getContent();
		drop(pp);
		fldsPort.close();

		Gson gson = new Gson();
		FieldInfo[] fiArray = null;
		if (debug)
			System.out.println(fldsStr);
		try {
		fiArray = gson.fromJson(fldsStr, FieldInfo[].class);
		} catch (Exception e){
			System.out.println("Error parsing JSON string");
			return;
		}
		if (debug)
			for (int i = 0; i < fiArray.length; i++) {
				String colName = fiArray[i].colName;
				String objField = fiArray[i].objField;
				System.out.println(colName + ", " + objField);
			}
		
		
		String userName = System.getProperty("user.name");
		
		// Determine latest version of mysql-connector-java
		
		String command =
				  "curl -X GET https://search.maven.org/solrsearch/select?q=a:\"mysql-connector-java\"&rows=20&wt=json";
		ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
		processBuilder.directory(new File("C:/Users/" + userName));
		Process process = processBuilder.start();
		InputStream instr = process.getInputStream();
		String data = "";
		int i2 = -1;
		while (-1 != (i2 = instr.read())) {	      
	            char c = (char) i2;
	            data += c;
	         }
		
		String s = "\"latestVersion\":\"";
		int m = data.indexOf(s);
		data = data.substring(m + s.length());
		int n = data.indexOf("\"");
		String v = data.substring(0, n);
		
		// Download latest version of mysql-connector-java jar file
		
		command = "curl -O http://search.maven.org/remotecontent?filepath=mysql/mysql-connector-java/" + v + "/mysql-connector-java-" + v + ".jar";
		processBuilder = processBuilder.command(command.split(" "));
		//processBuilder.directory(new File("C:/Users/" + userName + "/workspace/"));
		process = processBuilder.start();

		File f = new File("mysql-connector-java-" + v + ".jar");
						
		URL[] urls = {f.toURI().toURL()};
		URLClassLoader ucl = new URLClassLoader(urls);
		Class<?> conn_cls = ucl.loadClass("java.sql.Connection");
		Class<?> dm_cls = ucl.loadClass("java.sql.DriverManager");
		Class<?> stmt_cls = ucl.loadClass("java.sql.Statement");
		Class<?> rs_cls = ucl.loadClass("java.sql.ResultSet");
		Class<?> rsmd_cls = ucl.loadClass("java.sql.ResultSetMetaData");
		//Class<?> se_cls = ucl.loadClass("java.sql.SQLException");
		
		Class<?>[] carr = {String.class, String.class, String.class};
		//Object conn = conn_cls.newInstance();
		
		Class<?>[] carr2 = {String.class};
		
		try  
		{
				Method gc = dm_cls.getMethod("getConnection", carr);
				String connStr = iipContents[0] + 
						"?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
				Object conn = gc.invoke(null, connStr, 
						user, pswd);                         
				
				Method cs = conn_cls.getMethod("createStatement");
				Object stmt = cs.invoke(conn);                   
				
 
		
			String strSelect = "select * from " + iipContents[1];
			System.out.println("The SQL statement is: \"" + strSelect + "\"\n");  
			
			
			
			Method eq = stmt_cls.getMethod("executeQuery", carr2);	
			
			Object rset2 = eq.invoke(stmt, strSelect);  // o3 is ResultSet
			
			//ResultSet rset = stmt.executeQuery(strSelect);

			Object rsmd2;
			int numberOfColumns = 0;
			HashMap<String, String> hmColumns = new HashMap<String, String>();
			try {
				Method gmd = rs_cls.getMethod("getMetaData");
		    	rsmd2 = gmd.invoke(rset2);
		    	//rsmd = rset.getMetaData();
		    	Method gcc = rsmd_cls.getMethod("getColumnCount");
		    	numberOfColumns  = (int) gcc.invoke(rsmd2);
				//numberOfColumns = rsmd.getColumnCount();
		    	Method gcn = rsmd_cls.getMethod("getColumnName", int.class);
		    	Method gct = rsmd_cls.getMethod("getColumnTypeName", int.class);
		    	
				for (int i = 1; i <= numberOfColumns; i++) {
					String colName  = (String) gcn.invoke(rsmd2, i);
					String colTypeName  = (String) gct.invoke(rsmd2, i);
					hmColumns.put(colName, colTypeName);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// number of columns should match number of fields in curClass - so
			// let's test...
			
			int n2= dataClass.getFields().length;
			if (n2 != numberOfColumns) {
				System.out.println(
						"Number of class fields - " + n2 + " does not match number of columns - " + numberOfColumns);
				return;
			}
			
			HashMap<String, Class<?>> hmFields = new HashMap<String, Class<?>>();
			
			for (Field fd: dataClass.getFields()) {
				hmFields.put(fd.getName(), fd.getType());
			}

			// iterate through rows

			int rowCount = 0;
			Method next = rs_cls.getMethod("next");
	    	boolean nxt = (boolean) next.invoke(rset2);
			while (nxt) { // Move the cursor to the next row, return
									// false if no more row
				Object obj = null;
				try {
					Constructor<?> cons = dataClass.getConstructor();					
					obj = cons.newInstance(); 
				} catch (InstantiationException e) {
					// handle 1
				} catch (IllegalAccessException e) {
					// handle 2
				}
				 

				// iterate through fiArray

				//for (Map.Entry<String, String> entry : hmColumns.entrySet()) {
				for (int i = 0; i < fiArray.length; i++) {
					// System.out.println(entry.getKey() + " = " +
					// entry.getValue());
					String getMethodName = null;

					String colName = fiArray[i].colName;
					String objField = fiArray[i].objField;
									
					if (debug) {
						System.out.println("JDBC: " + colName + " " + hmColumns.get(colName));					
						System.out.println("Obj: " + objField + " " + hmFields.get(objField));
					}
					
					String objFType = hmFields.get(objField).toString(); 
					
					Class<?>[] cArg = new Class[1];
					cArg[0] = String.class;
								
					if (objFType.startsWith("class ")) {
						int k = objFType.lastIndexOf(".");
						String s2 = objFType.substring(k + 1);
						if (s2.equals("BigDecimal")) 
								s2 = "Decimal";
						getMethodName = "get" + s2;
						try {
							rs_cls.getMethod(getMethodName, cArg);
						} catch (NoSuchMethodException e)
						{
							getMethodName = "getString";
						}
					}
					else {
						getMethodName = "get" + objFType.substring(0, 1).toUpperCase();
						getMethodName += objFType.substring(1).toLowerCase();
					}
					
										
					//Class<?>[] cArg = new Class[1];
					//cArg[0] = String.class;

					Method meth = null;
					Object o = null;
					
					try {
						meth = rs_cls.getMethod(getMethodName, cArg);
					} catch (NoSuchMethodException e) {
						System.out.println("Missing method - trying method: '" + getMethodName + "()' on '" + colName
								+ "' " + hmColumns.get(colName) + " (target: '" + objField + "' "
								+ hmFields.get(objField) + ")");
					}
					
					try {
					o = meth.invoke(rset2, colName);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}

					Field fd = dataClass.getField(objField);
					
					try {
						fd.set(obj, o);
					} catch (Exception e) {						
						String s2 = hmFields.get(objField).toString();
						//System.out.println("class is " + s); 
						s2 = s2.substring(6);
						//Object oo = Class.forName(s).newInstance();
						Constructor<?> cons = Class.forName(s2).getConstructor(cArg);
						Object oo = cons.newInstance((String) o);
						try {
						fd.set(obj, oo);
						} catch (/*InvocationTargetException*/ Exception e2) {	
							e2.printStackTrace();
						}
					
				}
				}
				outPort.send(create(obj));
				++rowCount;
				nxt = (boolean) next.invoke(rset2);
			}

			System.out.println("Total number of records = " + rowCount);
			// outPort.send(create("Total number of records = " + rowCount));
			 
		} catch (ClassNotFoundException ex) {
			//System.out.println("Class Not Found Exception");
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			//System.out.println("Class Not Found Exception");
			ex.printStackTrace();
		} catch (Exception ex) {
			//System.out.println("SQL Exception");
			ex.printStackTrace();
		} 

		// Step 5: Close conn and stmt - Done automatically by
		// try-with-resources (JDK 7)
	}
	
	

	@Override
	protected void openPorts() {
		pswdPort = openInput("PSWD");
		userPort = openInput("USER");
		dBNPort = openInput("DATABASE");
		classPort = openInput("CLASS");
		fldsPort = openInput("FIELDS");
		outPort = openOutput("OUT");
	}

	public class FieldInfo {
		String colName;
		String objField;
	}
}
