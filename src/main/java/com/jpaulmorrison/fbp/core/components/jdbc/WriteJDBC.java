package com.jpaulmorrison.fbp.core.components.jdbc;

	
	import java.io.File;
import java.io.InputStream;
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

	@ComponentDescription("Write incoming IPs to MySQL table")
	@InPorts({ @InPort(value = "DATABASE", description = "Database name", type = String.class),
			@InPort(value = "USER", description = "User name", type = String.class),
			@InPort(value = "CLASS", description = "Object class", type = String.class),
			@InPort(value = "FIELDS", description = "Field correspondences", type = String.class),
			@InPort(value = "PSWD", description = "Password obtained from file", type = String.class),
			 @InPort("IN") })
	@OutPort(value = "OUT", description = "Table rows", optional=true)

	public class WriteJDBC extends Component {

		// adapted from
		// https://www.ntu.edu.sg/home/ehchua/programming/java/JDBC_Basic.html

		//private OutputPort outPort;

		private InputPort pswdPort;
		private InputPort dBNPort;
		private InputPort userPort;
		private InputPort classPort;
		private InputPort fldsPort;
		private InputPort inPort;
		private OutputPort outPort;


		
		@SuppressWarnings("resource")
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
			Class<?> curClass = Class.forName(objClass);
			
			String[] iipContents = dbTable.split("!", 2);

			pp = fldsPort.receive();

			String fldsStr = (String) pp.getContent();
			drop(pp);
			fldsPort.close();

			Gson gson = new Gson();
			
			FieldInfo[] fiArray = null;
			try {
				fiArray = gson.fromJson(fldsStr, FieldInfo[].class);

			} catch (Exception e) {
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

			try {
					Method gc = dm_cls.getMethod("getConnection", carr);
					Object conn = gc.invoke(null, iipContents[0] + 
							"?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", 
							user, pswd);                         
					
					Method cs = conn_cls.getMethod("createStatement");
					Object stmt = cs.invoke(conn);                   					
				
				String strSelect = "select * from " + iipContents[1];
				//ResultSet rs = stmt.executeQuery(strSelect);
				System.out.println("The SQL statement is: \"" + strSelect + "\"\n");  
				
				
				
				Method eq = stmt_cls.getMethod("executeQuery", carr2);	
				
				Object rset2 = eq.invoke(stmt, strSelect);  
				
			    //ResultSetMetaData rsmd = rset2.getMetaData();
			    Method gmd = rs_cls.getMethod("getMetaData");
		    	Object rsmd2 = gmd.invoke(rset2);
		    	
				//int numberOfColumns = rsmd2.getColumnCount();
		    	 Method gcc = rsmd_cls.getMethod("getColumnCount");
			     int numberOfColumns = (int) gcc.invoke(rsmd2);
		    	
			
				//String strSelect = "select * from " + iipContents[1];
				//System.out.println("The SQL statement is: \"" + strSelect + "\"\n"); // Echo
				String strDelete = "delete from " + iipContents[1];  // no qualifier
				System.out.println("The SQL statement is: \"" + strDelete + "\"\n"); // Echo
						
				//int countDeleted = stmt.executeUpdate(strDelete);
				Method del = stmt_cls.getMethod("executeUpdate", carr2);	
				
				int countDeleted = (int) del.invoke(stmt, strDelete);  
				
		        System.out.println(countDeleted + " records deleted.\n");	     
				
				HashMap<String, String> hmColumns = new HashMap<String, String>();
				
				Method gcn = rsmd_cls.getMethod("getColumnName", int.class);
		    	Method gct = rsmd_cls.getMethod("getColumnTypeName", int.class);
				 
				try {
					
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
				
				int x = curClass.getFields().length;
				if (x != numberOfColumns) {
					System.out.println(
							"Number of class fields - " + n + " does not match number of columns - " + numberOfColumns);
					return;
				}
				
				HashMap<String, Class<?>> hmFields = new HashMap<String, Class<?>>();
				
				for (Field fd: curClass.getFields()) {
					hmFields.put(fd.getName(), fd.getType());
				}
				
				

				// receive and process IPs

				 
				int rowCount = 0;
				Packet<?> pIn;
			    while ((pIn = inPort.receive()) != null) {
			    	
			    	Object o = pIn.getContent();
			    	if (o.getClass() != curClass) {
			    		System.out.println("Unexpected class in incoming IP: " + o.getClass());
			    		continue;
			    	}
			    	String sqlInsert = "insert into " + iipContents[1] + "(";
			    	String sqlValues = " values(";
			    	// iterate through hmColumns
			    	String cma = ""; 
					for (String col : hmColumns.keySet()) {
						Field field = null;
						for (int i = 0; i < fiArray.length; i++) {
							// String colName = fiArray[i].colName;
							String objField = fiArray[i].objField;
							if (fiArray[i].colName.equals(col)) {
								field = curClass.getDeclaredField(objField);
								break;								
							}
						}

							if (field == null) {
								System.out.println("Table column \"" + col + 
										"\" not found in Field Info:" +
										fldsStr);
								return;										
							}
														
							sqlInsert += cma + col;
							sqlValues += cma + "\"" + field.get(o).toString() + "\"";
							cma = ",";						 
					}
					sqlInsert += ")";
			    	sqlValues += ")";
			    	 //String sqlInsert = "insert into sales values (3001, 'Gone Fishing', 'Kumar', 'CAD11.11', 11)";
			        System.out.println("The SQL statement is: " + sqlInsert + " " +
			    	 sqlValues + "\n");  // Echo for debugging
			        
			        //int countInserted = stmt.executeUpdate(sqlInsert + " " + sqlValues);
			        Method eu = stmt_cls.getMethod("executeUpdate", carr2);
			        int countInserted = (int) eu.invoke(stmt, sqlInsert + " " + sqlValues);
			        
			        if (countInserted != 1) {
			        	System.out.println("Couldn't insert record:\n");
			        	System.out.println("... " + sqlInsert);
			        }
			        
			        if (outPort.isConnected()) {
				        outPort.send(pIn);
				      } else {
				        drop(pIn);
				      }		 
			    }
		
				System.out.println("Total number of records = " + rowCount);
				// outPort.send(create("Total number of records = " + rowCount));
				 
			}   catch (InvocationTargetException ex) {
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
			inPort = openInput("IN");
			outPort = openOutput("OUT");
		}

		public class FieldInfo {
			String colName;
			String objField;
		}
	}
