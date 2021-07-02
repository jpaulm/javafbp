package com.jpaulmorrison.fbp.core.components.httpurl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.jpaulmorrison.fbp.core.engine.Component;
import com.jpaulmorrison.fbp.core.engine.InPort;
import com.jpaulmorrison.fbp.core.engine.InputPort;
import com.jpaulmorrison.fbp.core.engine.OutPort;
import com.jpaulmorrison.fbp.core.engine.OutputPort;
import com.jpaulmorrison.fbp.core.engine.Packet;

//https://www.codejava.net/java-se/networking/use-httpurlconnection-to-download-file-from-an-http-url

@InPort(value = "SOURCE", description = "HTMLName")
@OutPort(value = "OUT", description = "HTML")

public class LoadURL extends Component {

	private InputPort inPort;
	private OutputPort outPort;

	protected void execute() throws Exception {

		Packet<?> pp = inPort.receive();

		String fileURL = (String) pp.getContent();
		drop(pp);
		inPort.close();

			
		// https://stackoverflow.com/questions/2793168/reading-httpurlconnection-inputstream-manual-buffer-or-bufferedinputstream

		URL url = new URL(fileURL);
		
		//HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
				
		InputStream in = url.openStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder result = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null) {
		    result.append(line);
		}
		outPort.send(create(result.toString()));
		reader.close();
	}

	@Override
	protected void openPorts() {
		inPort = openInput("SOURCE");
		outPort = openOutput("OUT");

	}
}
