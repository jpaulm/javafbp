package com.jpaulmorrison.fbp.core.components.jdbc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
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
		
		FieldInfo[] fiArray = gson.fromJson(fldsStr, FieldInfo[].class);

		try (

				// Step 1: Allocate a database 'Connection' object
				Connection conn = DriverManager.getConnection(
						// "jdbc:mysql://localhost:3306/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
						// "root", pswd); // For MySQL only

						iipContents[0] + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC", user, pswd);
				
				Statement stmt = conn.createStatement();) {
		
			String strSelect = "select * from " + iipContents[1];
			System.out.println("The SQL statement is: \"" + strSelect + "\"\n"); // Echo
																					// For
																					// debugging
			
			ResultSet rset = stmt.executeQuery(strSelect);

			ResultSetMetaData rsmd;
			int numberOfColumns = 0;
			HashMap<String, String> hmColumns = new HashMap<String, String>();
			try {
				rsmd = rset.getMetaData();
				numberOfColumns = rsmd.getColumnCount();
				for (int i = 1; i <= numberOfColumns; i++) {
					hmColumns.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// number of columns should match number of fields in curClass - so
			// let's test...
			
			int n = curClass.getFields().length;
			if (n != numberOfColumns) {
				System.out.println(
						"Number of class fields - " + n + " does not match number of columns - " + numberOfColumns);
				return;
			}
			
			HashMap<String, Class<?>> hmFields = new HashMap<String, Class<?>>();
			
			for (Field fd: curClass.getFields()) {
				hmFields.put(fd.getName(), fd.getType());
			}

			// iterate through rows

			int rowCount = 0;
			while (rset.next()) { // Move the cursor to the next row, return
									// false if no more row
				Object obj = null;
				try {
					obj = curClass.getDeclaredConstructor().newInstance();
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
									
					//System.out.println("JDBC: " + colName + " " + hmColumns.get(colName));
					
					//System.out.println("Obj: " + objField + " " + hmFields.get(objField));
					
					String objFType = hmFields.get(objField).toString(); 
					
					Class<?>[] cArg = new Class[1];
					cArg[0] = String.class;
								
					if (objFType.startsWith("class ")) {
						int k = objFType.lastIndexOf(".");
						String s = objFType.substring(k + 1);
						if (s.equals("BigDecimal")) 
								s = "Decimal";
						getMethodName = "get" + s;
						try {
							ResultSet.class.getMethod(getMethodName, cArg);
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
						meth = ResultSet.class.getMethod(getMethodName, cArg);
					} catch (NoSuchMethodException e) {
						System.out.println("Missing method - trying method: '" + getMethodName + "()' on '" + colName
								+ "' " + hmColumns.get(colName) + " (target: '" + objField + "' "
								+ hmFields.get(objField) + ")");
					}
					o = meth.invoke(rset, colName);

					Field fd = curClass.getField(objField);
					
					try {
						fd.set(obj, o);
					} catch (/*InvocationTargetException*/ Exception e) {
						//System.out.println("Format mismatch - trying method: '" + getMethodName + "()' on '" + colName
						//		+ "' " + hmColumns.get(colName) + " (target: '" + objField + "' "
						//		+ hmFields.get(objField) + ")");
						//MPrice mp = new MPrice("CAD" +  o);  //fudge
						String s = hmFields.get(objField).toString();
						s = s.substring(6);
						//Object oo = Class.forName(s).newInstance();
						Constructor<?> cons = Class.forName(s).getConstructor(cArg);
						Object oo = cons.newInstance((String) o);
						fd.set(obj, oo);
					
				}
				}
				outPort.send(create(obj));
				++rowCount;
			}

			System.out.println("Total number of records = " + rowCount);
			// outPort.send(create("Total number of records = " + rowCount));
			 
		} catch (SQLException ex) {
			//System.out.println("SQL Exception");
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			//System.out.println("Class Not Found Exception");
			ex.printStackTrace();
		}
		catch (InvocationTargetException ex) {
			//System.out.println("Class Not Found Exception");
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