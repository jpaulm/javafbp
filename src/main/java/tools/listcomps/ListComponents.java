package tools.listcomps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ListComponents {

	Enumeration<?> entries;
	ZipFile zipFile;

	// zip file must have "src", "test" and "diagrams" directories // and
	// optionally components...

	String zipName = "Documents\\GitHub\\javafbp\\build\\libs\\javafbp-4.1.0.jar";
	//String shortName = zipName.substring(zipName.indexOf("build"));
	String shortName = "C:\\Users\\Paul\\" + zipName;
	
	FileOutputStream f = null;	
	
	LinkedList<String> compList = new LinkedList<String>();

	ListComponents(String str) throws MalformedURLException {
		try {
			f = new FileOutputStream(new File("C:\\Temp\\JavaFBP_Comps.txt"));}
					catch (Exception e){} 
		
		if (str != null)
			zipName = str;
		
		String zfn = System.getProperty("user.home") + File.separator + zipName;
		 
		

		try {

			zipFile = new ZipFile(zfn);

			entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				
				if (-1 == (entry.getName().indexOf("/components/")))
					continue;

				if (entry.isDirectory()) { // Assume directories are stored
											// parents first
					// then children.
					System.out.println(
							"Extracting directory: " + entry.getName()); // This
																			// is
																			// not
																			// robust,
																			// just
																			// for
																			// demonstration
																			// purposes.
					//(new File(entry.getName())).mkdirs();
				} else {
					System.out.println("Extracting file: " + entry.getName());
					
				}
				compList.add(entry.getName());
			}

			zipFile.close();
		} catch (IOException ioe) {
			System.err.println("Unhandled exception:");
			ioe.printStackTrace();
			return;
		}
		URL[] urls = new URL[1];
		urls[0] = (new File(shortName)).toURI().toURL();
		URLClassLoader classLoader = null;
		
		//classLoader = new URLClassLoader(urls);
		classLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader().getParent());
		
		Class javaClass = null;
		try {
		javaClass = classLoader.loadClass("com.jpaulmorrison.fbp.core.components.text.StartsWith");
		
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

		Annotation[] anns = javaClass.getAnnotations();
		
	}

	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0) {
			// System.out.println(new String(buffer));
			out.write(buffer, 0, len);
		}
		//out.flush();
		in.close();
		//out.close();
	}

	public static void main(String[] args) {

	//	System.out.println(System.getProperty("simple.message") + args[0] + " from Simple.");

		try {
			new ListComponents(System.getProperty("simple.message"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}
}