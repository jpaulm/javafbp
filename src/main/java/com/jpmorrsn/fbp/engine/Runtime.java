package com.jpmorrsn.fbp.engine;

import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.Boolean;
import java.lang.Exception;
import java.lang.Iterable;
import java.lang.Override;
import java.util.*;
import java.net.*;
import org.java_websocket.*;
import org.java_websocket.handshake.*;
import org.java_websocket.server.*;
import org.java_websocket.util.Base64;
import org.json.*;

final public class Runtime {

    public static class Util {

        public static class JSONObjectKeysIterable implements Iterable {
            JSONObject mObject;
            JSONObjectKeysIterable(JSONObject o) {
                mObject = o;
            }

            public java.util.Iterator<String> iterator() {
                return mObject.keys();
            }
        }

        public static abstract class Predicate<Item> {
            protected abstract boolean apply(Item i);
        }

        public static <Item> List<Item> filter(List<Item> in, Predicate<Item> f) {
            List<Item> out = new ArrayList<Item>(in.size());
            for (Item inObj : in) {
                if (f.apply(inObj)) {
                    out.add(inObj);
                }
            }
            return out;
        }

        public static String stringFromStream(InputStream stream) throws UnsupportedEncodingException, IOException {
            BufferedReader in= new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder buf=new StringBuilder();
            String str;
            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();
        }
    }

    public static class ComponentLibrary {

        private HashMap<String, Class> mComponents = new HashMap<String, Class>();

        public ComponentLibrary() {

        }
        public ComponentLibrary(String fbpPath) throws Exception {
            InputStream in = new FileInputStream(fbpPath);
            loadFromJson(Util.stringFromStream(in));
        }

        public void loadFromJson(String fbpFileContent) throws JSONException {
            JSONObject libs = null;
            try {
                JSONTokener tokener = new JSONTokener(fbpFileContent);
                JSONObject root = new JSONObject(tokener);
                JSONObject rt = root.getJSONObject("javafbp");
                libs = rt.getJSONObject("libraries");
            } catch (Exception e) {
                System.err.println("Unable to parse fbp.json: " + e.toString());
            }

            Iterable<String> libNames = new Util.JSONObjectKeysIterable(libs);
            for (String libName : libNames) {
                JSONObject lib = libs.getJSONObject(libName);
                String classPath = lib.getString("_classpath");
                JSONObject components = lib.optJSONObject("components");
                Iterable<String> names = new Runtime.Util.JSONObjectKeysIterable(components);
                buildComponentMap(libName, names, classPath);
            }
        }

        private void buildComponentMap(String libName, Iterable<String> componentNames, String baseLib) {
            for (String name : componentNames) {
                final String fullName = libName+"/"+name;
                final String className = baseLib + "." + name;
                try {
                    Class c = Class.forName(className);
                    mComponents.put(fullName, c);
                } catch (Exception e) {
                    System.err.println("Cannot load component " + fullName + ": " + e.toString());
                    e.printStackTrace();
                }
            }
        }


        public Map<String, Class> getComponents() { return mComponents; }

        public Class getComponent(String componentName) { return mComponents.get(componentName); }

        private static List<InPort> getInports(Class comp) {
            ArrayList<InPort> ret = new ArrayList<InPort>();
            InPort p = (InPort)comp.getAnnotation(InPort.class);
            if (p != null) {
                ret.add(p);
            }
            InPorts ports = (InPorts)comp.getAnnotation(InPorts.class);
            if (ports != null) {
                for (InPort ip : ports.value()) {
                    ret.add(ip);
                }
            }
            return ret;
        }
        private static List<OutPort> getOutports(Class comp) {
            ArrayList<OutPort> ret = new ArrayList<OutPort>();
            OutPort p = (OutPort)comp.getAnnotation(OutPort.class);
            if (p != null) {
                ret.add(p);
            }
            OutPorts ports = (OutPorts)comp.getAnnotation(OutPorts.class);
            if (ports != null) {
                for (OutPort op : ports.value()) {
                    ret.add(op);
                }
            }
            return ret;
        }
        private static String getDescription(Class comp) {
            String description = "";
            ComponentDescription a = (ComponentDescription)comp.getAnnotation(ComponentDescription.class);
            if (a != null) {
                description = a.value();
            }
            return description;
        }

        public JSONObject getComponentInfoJson(String componentName) throws JSONException {
            // Have to instantiate the component to introspect :(
            Class componentClass = mComponents.get(componentName);

            // Top-level
            JSONObject def = new JSONObject();
            def.put("name", componentName);
            def.put("description", getDescription(componentClass));
            def.put("subgraph", false); // TODO: support subgraphs
            def.put("icon", "coffee"); // TODO: allow components to specify icon

            // InPorts
            JSONArray inPorts = new JSONArray();
            for (InPort port : ComponentLibrary.getInports(componentClass)) {
                JSONObject portInfo = new JSONObject();
                portInfo.put("id", port.value());
                portInfo.put("type", ComponentLibrary.mapPortType(port.type()));
                portInfo.put("description", port.description());
                portInfo.put("addressable", port.arrayPort());
                portInfo.put("required", !port.optional());
                inPorts.put(portInfo);
            }
            def.put("inPorts", inPorts);

            // OutPorts
            JSONArray outPorts = new JSONArray();
            for (OutPort port : ComponentLibrary.getOutports(componentClass)) {
                JSONObject portInfo = new JSONObject();
                portInfo.put("id", port.value());
                portInfo.put("type", ComponentLibrary.mapPortType(port.type()));
                portInfo.put("description", port.description());
                portInfo.put("addressable", port.arrayPort());
                portInfo.put("required", !port.optional());
                outPorts.put(portInfo);
            }
            def.put("outPorts", outPorts);

            return def;
        }

        // Return a FBP type string for a
        static String mapPortType(Class javaType) {
            if (javaType == String.class) {
                return "string";
            } else if (javaType == Boolean.class) {
                return "boolean";
            } else if (javaType == java.util.Hashtable.class) {
                return "object";
            } else {
                // Default
                return "any";
            }
        }

    }

    public static class Definition {

        public static class Connection {
            public Connection() {}

            public String srcNode;
            public String srcPort;
            public String tgtNode;
            public String tgtPort;
        }

        public static class IIP {
            public String tgtNode;
            public String tgtPort;
            public Object data;
        }

        public Map<String, String> nodes; // id -> className
        public List<Connection> connections;
        public List<IIP> iips;

        public Definition() {
            nodes = new HashMap();
            connections = new ArrayList<Connection>();
            iips = new ArrayList<IIP>();
        }

        public void loadFromJson(String json) throws JSONException {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject root = new JSONObject(tokener);

            // Nodes
            JSONObject processes = root.getJSONObject("processes");
            Iterable<String> nodeNames = new Util.JSONObjectKeysIterable(processes);
            for (String name : nodeNames) {
                JSONObject node = processes.getJSONObject(name);
                addNode(name, node.getString("component"));
            }

            // Connections
            JSONArray connections = root.getJSONArray("connections");
            for (int i=0; i<connections.length(); i++) {
                JSONObject conn = connections.getJSONObject(i);
                JSONObject src = conn.optJSONObject("src");
                JSONObject tgt = conn.getJSONObject("tgt");
                if (src == null) {
                    addInitial(tgt.getString("process"), tgt.getString("port"),
                               conn.getString("data")
                    );
                } else {
                    addEdge(src.getString("process"), src.getString("port"),
                            tgt.getString("process"), tgt.getString("port")
                    );
                }
            }

        }

        public void addNode(String id, String component) {
            this.nodes.put(id, component);
        }
        public void removeNode(String id) {
            this.nodes.remove(id);
        }
        public void addEdge(final String src, final String _srcPort,
                            final String tgt, final String _tgtPort) {
            this.connections.add(new Definition.Connection() {{
                srcNode=src; srcPort=_srcPort;
                tgtNode=tgt; tgtPort=_tgtPort;
            }});
        }
        public void removeEdge(final String src, final String srcPort,
                               final String tgt, final String tgtPort) {
            // PERFORMANCE: linear complexity
            this.connections = Util.filter(this.connections, new Util.Predicate<Connection>() {
                @Override
                public boolean apply(Connection in) {
                    boolean equals = in.srcNode == src && in.srcPort == srcPort
                            && in.tgtNode == tgt && in.tgtPort == tgtPort;
                    return !equals;
                }
            });
        }
        public void addInitial(final String tgt, final String _tgtPort,
                               final String _data) {
            this.iips.add(new Definition.IIP() {{
                tgtNode=tgt; tgtPort=_tgtPort;
                data = _data;
            }});
        }
        public void removeInitial(final String tgt, final String tgtPort) {
            // PERFORMANCE: linear complexity
            this.iips = Util.filter(this.iips, new Util.Predicate<IIP>() {
                @Override
                public boolean apply(IIP in) {
                    boolean equals = in.tgtNode == tgt && in.tgtPort == tgtPort;
                    return !equals;
                }
            });
        }

    }

    public static class RuntimeNetwork extends Network {

        static final String copyright = "";
        private Definition mDefinition;
        private ComponentLibrary mLibrary;

        public RuntimeNetwork(ComponentLibrary lib, Definition def) {
            mLibrary = lib;
            mDefinition = def;
        }

        @Override
        protected void define() {

            // Add nodes
            for (Map.Entry<String, String> entry : mDefinition.nodes.entrySet()) {
                System.out.println("addNode: " + entry.getKey() + " " + entry.getValue() + " "); // cls.toString()
                Class cls = mLibrary.getComponent(entry.getValue());
                component(entry.getKey(), cls);
            }

            // Connect
            for (Definition.Connection conn : mDefinition.connections) {
                connect(component(conn.srcNode), port(conn.srcPort.toUpperCase()),
                        component(conn.tgtNode), port(conn.tgtPort.toUpperCase()));
            }

            // Add IIPs
            for (Definition.IIP iip : mDefinition.iips) {
                System.out.println("addInitial: " + iip.tgtNode + " " + iip.tgtPort + " " + iip.data);
                initialize(iip.data, component(iip.tgtNode), port(iip.tgtPort.toUpperCase()));
            }

        }

        static public void startNetwork(ComponentLibrary lib, Definition def) throws Exception {
            Runtime.RuntimeNetwork net = new Runtime.RuntimeNetwork(lib, def);
            net.go();
        }

    }

    public static class FlowhubApi {
        private String endpoint;

        public static FlowhubApi create() {
            return new FlowhubApi("http://api.flowhub.io"); // TODO: support HTTPS
        }

        FlowhubApi(String e) {
            endpoint = e;
        }

        // Returns null on failure
        public String registerRuntime(final String runtimeId, final String userId, final String label, final String address) throws Exception {
            JSONObject payload = new JSONObject() {{
                put("id", runtimeId);
                put("user", userId);
                put("label", label);
                put("address", address);
                put("protocol", "websocket");
                put("type", "javafbp");
                put("secret", "9129923"); // TEMP: currently not used
            }};

            int response = makeRequestSync("PUT", endpoint+"/runtimes/"+runtimeId, payload);
            if (response == 201 || response == 200) {
                return runtimeId;
            } else {
                return null;
            }
        }

        public String pingRuntime(final String runtimeId) throws Exception {
            int response = makeRequestSync("POST", endpoint+"/runtimes/"+runtimeId, null);
            if (response == 201 || response == 200) {
                return runtimeId;
            } else {
                return null;
            }
        }

        private int makeRequestSync(String method, String url, JSONObject payload) throws Exception {
            URL obj = new URL(url);

            java.net.HttpURLConnection con = (java.net.HttpURLConnection)obj.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod(method);
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();
            java.io.DataOutputStream wr = new java.io.DataOutputStream(con.getOutputStream());

            if (payload != null) {
                wr.writeBytes(payload.toString());
            }
            wr.flush();
            wr.close();

            final int responseCode = con.getResponseCode();

            System.out.println(method + " " + url);
            if (payload != null) {
                System.out.println(payload.toString());
            }
            System.out.println("Response Code : " + responseCode);

            java.io.InputStream s = (responseCode > 400) ? con.getErrorStream() : con.getInputStream();
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(s));
            StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());

            return responseCode;
        }

    }

    public static class Server extends WebSocketServer {

        private Definition mNetworkDefinition = null; // TEMP: move out, object lifetime should be that of Runtime
        // FIXME: support multiple networks
        private ComponentLibrary mLibrary = null;

        public Server(int port, ComponentLibrary lib) throws UnknownHostException {
            super(new InetSocketAddress(port));
            mLibrary = lib;
        }

        public void onFbpCommand(String protocol, String command, JSONObject payload,
                                 WebSocket socket) throws JSONException {

            // Runtime info
            if (protocol.equals("runtime") && command.equals("getruntime")) {
                JSONObject p = new JSONObject();
                p.put("type", "javafbp");
                p.put("version", "0.4");
                p.put("capabilities", new JSONArray() {{
                    put("protocol:component");
                    put("protocol:graph");
                    put("protocol:network");
                }});
                sendFbpResponse("runtime", "runtime", p, socket);

            // Component listing
            } else if (protocol.equals("component") && command.equals("list")) {
                for (String name : mLibrary.getComponents().keySet()) {
                    JSONObject def = mLibrary.getComponentInfoJson(name);
                    sendFbpResponse("component", "component", def, socket);
                }

            // Graph manipulation
            // FIXME: respect 'graph'
            // FIXME: support subgraphs
            } else if (protocol.equals("graph") && command.equals("clear")) {
                mNetworkDefinition = new Definition();
            } else if (protocol.equals("graph") && command.equals("addnode")) {
                String id = payload.getString("id");
                String component = payload.getString("component");
                mNetworkDefinition.addNode(id, component);
            } else if (protocol.equals("graph") && command.equals("removenode")) {
                String id = payload.getString("id");
                mNetworkDefinition.removeNode(id);
            } else if (protocol.equals("graph") && command.equals("changenode")) {
                // Ignoring metadata changes
            } else if (protocol.equals("graph") && command.equals("addedge")) {
                // FIXME: handle addressable ports
                JSONObject src = payload.getJSONObject("src");
                JSONObject tgt = payload.getJSONObject("tgt");
                mNetworkDefinition.addEdge(src.getString("node"), src.getString("port"),
                                           tgt.getString("node"), tgt.getString("port")
                );
            } else if (protocol.equals("graph") && command.equals("removeedge")) {
                JSONObject src = payload.getJSONObject("src");
                JSONObject tgt = payload.getJSONObject("tgt");
                mNetworkDefinition.removeEdge(src.getString("node"), src.getString("port"),
                        tgt.getString("node"), tgt.getString("port")
                );
            } else if (protocol.equals("graph") && command.equals("changeedge")) {
                // Ignoring metadata changes
            } else if (protocol.equals("graph") && command.equals("addinitial")) {
                JSONObject tgt = payload.getJSONObject("tgt");
                JSONObject src = payload.getJSONObject("src");
                // FIXME: handle addressable ports
                mNetworkDefinition.addInitial(tgt.getString("node"), tgt.getString("port"),
                                              src.getString("data")
                );
            } else if (protocol.equals("graph") && command.equals("removeinitial")) {
                JSONObject tgt = payload.getJSONObject("tgt");
                mNetworkDefinition.removeInitial(tgt.getString("node"), tgt.getString("port"));

            // Network management
            // TODO: redirect System.out and System.error to the client
            // TODO: implement edge data introspection support
            // FIXME: execute network in separate thread, not blocking
            } else if (protocol.equals("network") && command.equals("start")) {
                try {
                    RuntimeNetwork.startNetwork(mLibrary, mNetworkDefinition);
                } catch (Exception e) {
                    System.err.println("Unable to start network");
                    e.printStackTrace();
                }

            } else {
                System.err.println("Unknown FBP protocol message: " + protocol + ":" + command);
            }
        }

        public void sendFbpResponse(String protocol, String command, JSONObject payload,
                                    WebSocket socket) throws JSONException {
            JSONObject msg = new JSONObject();
            msg.put("protocol", protocol);
            msg.put("command", command);
            msg.put("payload", payload);
            socket.send(msg.toString());
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            System.out.println("Client connected");
        }
        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Client disconnected");
        }
        @Override
        public void onMessage(WebSocket conn, String message) {
            System.out.println(conn + ": " + message);

            try {
                JSONTokener tokener = new JSONTokener(message);
                JSONObject root = new JSONObject(tokener);
                String protocol = root.getString("protocol");
                String command = root.getString("command");
                JSONObject payload = root.optJSONObject("payload");
                onFbpCommand(protocol, command, payload, conn);
            } catch (JSONException e) {
                System.err.println("JSON parsing error" + e.getMessage());
            }

        }
        @Override
        public void onError( WebSocket conn, Exception ex ) {
            ex.printStackTrace();
        }

    }

    static public class FlowhubRegistryPingTask extends TimerTask {

        static FlowhubRegistryPingTask setup(FlowhubApi api, String runtimeId) {
            FlowhubRegistryPingTask task = new FlowhubRegistryPingTask(api, runtimeId);
            Timer timer = new Timer();
            final double millisecondsPerMinute = 60e3;
            final long interval = (long)(10*millisecondsPerMinute);
            timer.scheduleAtFixedRate(task, (long)0, interval);
            return task;
        }

        private FlowhubApi api;
        private String runtimeId;

        FlowhubRegistryPingTask(FlowhubApi _api, String _runtimeId) {
            api = _api;
            runtimeId = _runtimeId;
        }

        @Override
        public void run() {
            try {
                api.pingRuntime(runtimeId);
            } catch (Exception e) {
                // Silently ignore, probably lacking internet access or similar
            }
        }
    }

    public static void main(final String[] argv) throws Exception {

        String p = "fbp.json"; // XXX: relative to cwd
        Runtime.ComponentLibrary lib = new Runtime.ComponentLibrary(p);

        if (argv.length == 1) {
            String graphPath = argv[0];
            Definition def = new Definition();
            def.loadFromJson(Runtime.Util.stringFromStream(new FileInputStream(graphPath)));
            RuntimeNetwork.startNetwork(lib, def);
        }

        FlowhubApi api = FlowhubApi.create();

        // TODO: allow to disable ping on commandline
        // FIXME: allow to specify on commandline
        int port = 3569;
        String host = "localhost";
        String label = "First JavaFBP";

        String userId = System.getenv().get("FLOWHUB_USER_ID");
        if (userId == null) {
            System.err.println("Missing FLOWHUB_USER_ID envvar");
            System.exit(1);
        }

        final String address = "ws://"+host+":"+port;
        String runtimeId = System.getenv().get("JAVAFBP_RUNTIME_ID");
        if (runtimeId == null) {
            runtimeId = java.util.UUID.randomUUID().toString();
            System.out.println("Registering new runtime with Flowhub: " + runtimeId);
            api.registerRuntime(runtimeId, userId, label, address);
        }

        FlowhubRegistryPingTask pinger = FlowhubRegistryPingTask.setup(api, runtimeId);

        WebSocketImpl.DEBUG = true;
        Server s = new Server(port, lib);
        s.start();
        System.out.println("Listening on port: " + s.getPort());
    }

}
