package com.jpmorrsn.fbp.engine;

import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;

import java.io.FileReader;
import java.io.Reader;
import java.lang.Exception;
import java.lang.Iterable;
import java.lang.Override;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.net.*;
import org.java_websocket.*;
import org.java_websocket.handshake.*;
import org.java_websocket.server.*;
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

    }

    public static class ComponentLibrary {

        private HashMap<String, Class> mComponents;

        ComponentLibrary() {
            mComponents = new HashMap<String, Class>();

            JSONObject components = null;
            try {
                Reader in = new FileReader("fbp.json"); // XXX: relative to cwd
                JSONTokener tokener = new JSONTokener(in);
                JSONObject root = new JSONObject(tokener);
                components = root.optJSONObject("components");
            } catch (Exception e) {
                System.err.println("Unable to parse fbp.jon: " + e.toString());
            }

            if (components != null) {
                String baseLib = "com.jpmorrsn.fbp.components"; // FIXME: define in fbp.json
                Iterable<String> keys = new Runtime.Util.JSONObjectKeysIterable(components);
                for (String name : keys) {
                    final String className = baseLib + "." + name;
                    try {
                        Class c = Class.forName(className);
                        mComponents.put(name, c);
                    } catch (Exception e) {
                        System.err.println("Cannot load component " + name + ": " + e.toString());
                        e.printStackTrace();
                    }
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
                portInfo.put("type", "any"); // TODO: annotate more specifically
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
                portInfo.put("type", "any"); // TODO: annotate more specifically
                portInfo.put("description", port.description());
                portInfo.put("addressable", port.arrayPort());
                portInfo.put("required", !port.optional());
                outPorts.put(portInfo);
            }
            def.put("outPorts", outPorts);

            return def;
        }
    }

    private static class Definition {

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

        Definition() {
            nodes = new HashMap();
            connections = new ArrayList<Connection>();
            iips = new ArrayList<IIP>();
        }

        public void addNode(String id, String component) {
            this.nodes.put(id, component);
        }
        public void removeNode(String id, String component) {
            // FIXME: implement
        }
        public void addEdge(final String src, final String _srcPort,
                            final String tgt, final String _tgtPort) {
            this.connections.add(new Definition.Connection() {{
                srcNode=src; srcPort=_srcPort;
                tgtNode=tgt; tgtPort=_tgtPort;
            }});
        }
        public void removeEdge(String id, String component) {
            // FIXME: implement
        }
        public void addInitial(final String tgt, final String _tgtPort,
                               final String _data) {
            this.iips.add(new Definition.IIP() {{
                tgtNode=tgt; tgtPort=_tgtPort;
                data = _data;
            }});
        }
        public void removeInitial(String id, String component) {
            // FIXME: implement
        }

    }

    private static class RuntimeNetwork extends Network {

        static final String copyright = "";
        private Definition mDefinition;

        public RuntimeNetwork(Definition def) {
            mDefinition = def;
        }

        @Override
        protected void define() {

            // Add nodes
            for (Map.Entry<String, String> entry : mDefinition.nodes.entrySet()) {
                Runtime.ComponentLibrary lib = new Runtime.ComponentLibrary(); // TEMP: move out, object lifetime should be that of Runtime
                System.out.println("addNode: " + entry.getKey() + " " + entry.getValue() + " "); // cls.toString()
                Class cls = lib.getComponent(entry.getValue());
                component(entry.getKey(), cls);
            }

            // Connect
            for (Definition.Connection conn : mDefinition.connections) {
                connect(component(conn.srcNode), port(conn.srcPort),
                        component(conn.tgtNode), port(conn.tgtPort));
            }

            // Add IIPs
            for (Definition.IIP iip : mDefinition.iips) {
                System.out.println("addInitial: " + iip.tgtNode + " " + iip.tgtPort + " " + iip.data);
                initialize(iip.data, component(iip.tgtNode), port(iip.tgtPort));
            }

        }

        static public void startNetwork(Definition def) throws Exception {
            Runtime.RuntimeNetwork net = new Runtime.RuntimeNetwork(def);
            net.go();
        }

    }

    public static class Server extends WebSocketServer {

        private Definition mNetworkDefinition = null; // TEMP: move out, object lifetime should be that of Runtime
        // FIXME: support multiple networks

        public Server(int port) throws UnknownHostException {
            super(new InetSocketAddress(port));
        }
        public Server(InetSocketAddress address) {
            super(address);
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
                Runtime.ComponentLibrary lib = new Runtime.ComponentLibrary(); // TEMP: move out, object lifetime should be that of process
                for (String name : lib.getComponents().keySet()) {
                    JSONObject def = lib.getComponentInfoJson(name);
                    sendFbpResponse("component", "component", def, socket);
                }

            // Graph manipulation
            // FIXME: respect 'graph'
            } else if (protocol.equals("graph") && command.equals("clear")) {
                mNetworkDefinition = new Definition();
            } else if (protocol.equals("graph") && command.equals("addnode")) {
                String id = payload.getString("id");
                String component = payload.getString("component");
                mNetworkDefinition.addNode(id, component);
            } else if (protocol.equals("graph") && command.equals("removenode")) {
                //mNetworkDefinition.removeNode()
            } else if (protocol.equals("graph") && command.equals("addedge")) {
                // FIXME: handle addressable ports
                JSONObject src = payload.getJSONObject("src");
                JSONObject tgt = payload.getJSONObject("tgt");
                mNetworkDefinition.addEdge(src.getString("node"), src.getString("port"),
                                           tgt.getString("node"), tgt.getString("port")
                );
            } else if (protocol.equals("graph") && command.equals("removeedge")) {
                //mNetworkDefinition.removeEdge() FIXME: implement
            } else if (protocol.equals("graph") && command.equals("addinitial")) {
                JSONObject tgt = payload.getJSONObject("tgt");
                JSONObject src = payload.getJSONObject("src");
                // FIXME: handle addressable ports
                mNetworkDefinition.addInitial(tgt.getString("node"), tgt.getString("port"),
                                              src.getString("data")
                );
            } else if (protocol.equals("graph") && command.equals("removeinitial")) {
                //mNetworkDefinition.removeInitial()
            } else if (protocol.equals("network") && command.equals("start")) {

                try {
                    RuntimeNetwork.startNetwork(mNetworkDefinition);
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

    public static void main(final String[] argv) throws Exception {

        Definition def = new Definition();
        def.addNode("generate", "GenerateTestData");
        def.addNode("write", "WriteToConsole");
        def.addEdge("generate", "OUT", "write", "IN");
        def.addInitial("generate", "COUNT", "10");
        RuntimeNetwork.startNetwork(def);

        WebSocketImpl.DEBUG = true;
        int port = 3569;
        Server s = new Server(port);
        s.start();
        System.out.println("Listening on port: " + s.getPort());
    }

}
