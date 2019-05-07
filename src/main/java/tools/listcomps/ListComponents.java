package tools.listcomps;


import java.io.File;

import java.io.IOException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class ListComponents {

	Enumeration<?> entries;
	ZipFile zipFile;

	// zip file must have "src", "test" and "diagrams" directories // and
	// optionally components...

	String zipName = "C:\\Users\\Paul\\Documents\\GitHub\\javafbp\\build\\libs\\javafbp-4.1.0.jar";
	//String shortName = zipName.substring(zipName.indexOf("build"));
	//String shortName = "C:\\Users\\Paul\\" + zipName;
	
	//FileOutputStream f = null;	
	
	//LinkedList<String> compList = new LinkedList<String>();
	URLClassLoader classLoader = null;
	URL[] urls = new URL[1];
	
			
	//classLoader = new URLClassLoader(urls);
	HashMap<String, AInPort> inputPortAttrs = new HashMap<String, AInPort>();
	HashMap<String, AOutPort> outputPortAttrs = new HashMap<String, AOutPort>();
	
	Class<?> compdescCls = null;
	Class<?> inportCls = null;
	Class<?> outportCls = null;
	Class<?> inportsCls = null;
	Class<?> outportsCls = null;
	
	boolean inports_started  = false;
	boolean outports_started  = false;
	
	
	ListComponents(String str) throws MalformedURLException {
		//try {
			//f = new FileOutputStream(new File("C:\\Temp\\JavaFBP_Comps.txt"));}
			//		catch (Exception e){} 
		
		if (str != null)
			zipName = str;
		System.out.println(zipName);
		String s = zipName; 
		int j = s.lastIndexOf("javafbp") + 8;
		String seg = "engine.";
		if (0 <= s.substring(j, j + 1).compareTo("4"))  // if javafbp jar file version not less than 4.0.0
		    seg = "core.engine.";
		String owner = "jpmorrsn";
		if (0 <= s.substring(j, j + 3).compareTo("4.1"))  // if javafbp jar file version not less than 4.0.0
		    owner = "jpaulmorrison";
		urls[0] = (new File(zipName)).toURI().toURL();
		classLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader().getParent());

		try {
			zipFile = new ZipFile(zipName);		
			
		compdescCls = classLoader
				.loadClass("com." + owner + ".fbp." + seg + "ComponentDescription");
		inportCls = classLoader
				.loadClass("com." + owner + ".fbp." + seg + "InPort");
		outportCls = classLoader
				.loadClass("com." + owner + ".fbp." + seg + "OutPort");
		inportsCls = classLoader
				.loadClass("com." + owner + ".fbp." + seg + "InPorts");
		outportsCls = classLoader
				.loadClass("com." + owner + ".fbp." + seg + "OutPorts");

		} catch (ClassNotFoundException e){
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
			

			entries = zipFile.entries();
			int ul_cnt = 0;
			System.out.println(
					"<h1>JavaFBP Component List</h1>"); 
			int hdg_level = 0;

			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				
				if (-1 == (entry.getName().indexOf("/core/components/")))
					continue;

				if (entry.isDirectory()) { // Assume directories are stored
											// parents first, then children 
					hdg_level = count_slashes(entry.getName());
					if (hdg_level > 1) {
						if (ul_cnt > 0)
							System.out.println("</ul>");
						System.out.println(
							"<h" + hdg_level + ">" + entry.getName() + "</h" + hdg_level + ">"); 
						
 						System.out.println("<ul>");
 						ul_cnt ++;
					}
					
					
					
				} else {
					System.out.println(
							"<li>" + entry.getName() + "</li>"); 
					
				 
				//compList.add(entry.getName());
				
				String t = entry.getName().substring(0, entry.getName().length() - 6);
				t = t.replace("/", ".");
				Class <?> javaClass = null;
						
				try {
				javaClass = classLoader.loadClass(t);
				
				} catch (ClassNotFoundException e) {
					System.out.println("Missing class name in " + zipName);
					
					// e.printStackTrace();
					javaClass = null;
				} catch (NoClassDefFoundError e) {
					System.out.println("Missing internal class name in "
							+ zipName);
					
					// e.printStackTrace();
					javaClass = null;
				} 

				Annotation[] annos = javaClass.getAnnotations();
				buildMetadata(annos);     // do for each non-directory entry
				}
				
			}

			try {
				zipFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		
		
		
		
	 

	int count_slashes(String s) {  // return reduces count by 4
		int count = 0;
		int n = s.indexOf("/");
		while (-1 != n) {
			count ++;
			s = s.substring(n + 1);
			n = s.indexOf("/");
		}
		return count - 4;			 
	}
	
	void buildMetadata(Annotation[] annos) {   // do for each class
		inputPortAttrs.clear();
		outputPortAttrs.clear();
		
		//String s = driver.javaFBPJarFile; 
		
		try {

			LinkedList<Annotation> output_annos = new LinkedList<Annotation>();
			//Annotation[] annos = javaClass.getAnnotations();
			for (Annotation a : annos) {
				if (compdescCls.isInstance(a)) {
					Method meth = compdescCls.getMethod("value");
					String compDescr = (String) meth.invoke(a);
					System.out.println(
							"<p>- " + compDescr + "</p>"); 
				}
				if (inportCls.isInstance(a)) {
					
					
					getInPortAnnotation(a, inportCls);
					
				}
				if (inportsCls.isInstance(a)) {
					
					Method meth = inportsCls.getMethod("value");
					Object[] oa = (Object[]) meth.invoke(a);
					for (Object o : oa) {
						getInPortAnnotation((Annotation) o, inportCls);
					}
				}
				if (outportCls.isInstance(a)) {
					output_annos.add(a);
					
				}
				if (outportsCls.isInstance(a)) {
					
					Method meth = outportsCls.getMethod("value");
					Object[] oa = (Object[]) meth.invoke(a);
					for (Object o : oa) {
						getOutPortAnnotation((Annotation) o, outportCls);
					}
				}
			}
			for (Annotation a: output_annos) {
				getOutPortAnnotation(a, outportCls);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void getInPortAnnotation(Annotation a, Class<?> inport) {
		AInPort ipt = new AInPort();
		if (!inports_started) {
			System.out.println(
					"<p>-- Input Ports</p>"); 
			inports_started  = true;						
		}
		try {
			Method meth = inport.getMethod("value");
			ipt.value = (String) meth.invoke(a);

			meth = inport.getMethod("arrayPort");
			Boolean b = (Boolean) meth.invoke(a);
			ipt.arrayPort = b.booleanValue();

			meth = inport.getMethod("fixedSize");
			b = (Boolean) meth.invoke(a);
			ipt.fixedSize = b.booleanValue();
			
			meth = inport.getMethod("optional");
			b = (Boolean) meth.invoke(a);
			ipt.optional = b.booleanValue();

			meth = inport.getMethod("description");
			ipt.description = (String) meth.invoke(a);

			meth = inport.getMethod("type");
			ipt.type = (Class<?>) meth.invoke(a);

			meth = inport.getMethod("setDimension");
			Integer ic = (Integer) meth.invoke(a);
			int i = ic.intValue();

			meth = inport.getMethod("valueList");
			String[] sa = (String[]) meth.invoke(a);
			for (String s : sa) {
				if (s.toLowerCase().endsWith("*")) {
					for (int j = 0; j < i; j++) {
						AInPort ipt2 = new AInPort();
						ipt2.value = s.substring(0, s.length() - 1) + j;
						ipt2.arrayPort = ipt2.arrayPort;
						ipt2.fixedSize = ipt.fixedSize;
						ipt2.description = ipt.description;
						ipt2.optional = ipt.optional;
						ipt2.type = ipt.type;
						inputPortAttrs.put(ipt2.value, ipt2);						
					}
				} else {
					AInPort ipt2 = new AInPort();
					ipt2.value = s;
					ipt2.arrayPort = ipt2.arrayPort;
					ipt2.fixedSize = ipt.fixedSize;
					ipt2.description = ipt.description;
					ipt2.optional = ipt.optional;
					ipt2.type = ipt.type;
					inputPortAttrs.put(ipt2.value, ipt2);
					}
			}
			if (sa.length == 0)
				inputPortAttrs.put(ipt.value, ipt);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	AOutPort getOutPortAnnotation(Annotation a, Class<?> outport) {
		if (!outports_started) {
			System.out.println(
					"<p>-- Output Ports</p>"); 
			outports_started  = true;						
		}
		AOutPort opt = new AOutPort();
		try {
			Method meth = outport.getMethod("value");
			opt.value = (String) meth.invoke(a);

			meth = outport.getMethod("arrayPort");
			Boolean b = (Boolean) meth.invoke(a);
			opt.arrayPort = b.booleanValue();

			meth = outport.getMethod("fixedSize");
			b = (Boolean) meth.invoke(a);
			opt.fixedSize = b.booleanValue();

			meth = outport.getMethod("optional");
			b = (Boolean) meth.invoke(a);
			opt.optional = b.booleanValue();
			
			meth = outport.getMethod("description");
			opt.description = (String) meth.invoke(a);

			meth = outport.getMethod("type");
			opt.type = (Class<?>) meth.invoke(a);

			meth = outport.getMethod("setDimension");
			Integer ic = (Integer) meth.invoke(a);
			int i = ic.intValue();

			meth = outport.getMethod("valueList");
			String[] sa = (String[]) meth.invoke(a);
			for (String s : sa) {
				if (s.toLowerCase().endsWith("*")) {
					for (int j = 0; j < i; j++) {
						AOutPort opt2 = new AOutPort();
						opt2.value = s.substring(0, s.length() - 1) + j;
						opt2.arrayPort = opt2.arrayPort;
						opt2.fixedSize = opt.fixedSize;
						opt2.optional = opt.optional;
						opt2.description = opt.description;
						opt2.type = opt.type;
						outputPortAttrs.put(opt2.value, opt2);
					}
				} else {
					AOutPort opt2 = new AOutPort();
					opt2.value = s;
					opt2.arrayPort = opt2.arrayPort;
					opt2.fixedSize = opt.fixedSize;
					opt2.optional = opt.optional;
					opt2.description = opt.description;
					opt2.type = opt.type;
					outputPortAttrs.put(opt2.value, opt2);
				}
			}
			if (sa.length == 0)
				outputPortAttrs.put(opt.value, opt);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return opt;
	}

	
	


	public static void main(String[] args) {

	//	System.out.println(System.getProperty("simple.message") + args[0] + " from Simple.");

		try {
			new ListComponents(System.getProperty("jarfile"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}
}