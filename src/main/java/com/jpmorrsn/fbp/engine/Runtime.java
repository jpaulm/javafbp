package com.jpmorrsn.fbp.engine;

import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;
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

        public Map<String, Class> nodes;
        public List<Connection> connections;
        public List<IIP> iips;

        Definition() {
            nodes = new HashMap();
            connections = new ArrayList<Connection>();
            iips = new ArrayList<IIP>();
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
            for (Map.Entry<String, Class> entry : mDefinition.nodes.entrySet()) {
                component(entry.getKey(), entry.getValue());
            }

            // Connect
            for (Definition.Connection conn : mDefinition.connections) {
                connect(component(conn.srcNode), port(conn.srcPort),
                        component(conn.tgtNode), port(conn.tgtPort));
            }

            // Add IIPs
            for (Definition.IIP iip : mDefinition.iips) {
                initialize(iip.data, component(iip.tgtNode), port(iip.tgtPort));
            }

        }

        static public void startNetwork(Definition def) throws Exception {
            Runtime.RuntimeNetwork net = new Runtime.RuntimeNetwork(def);
            net.go();
        }

    }

    public static class Server extends WebSocketServer {

        public Server(int port) throws UnknownHostException {
            super(new InetSocketAddress(port));
        }
        public Server(InetSocketAddress address) {
            super(address);
        }

        public void onFbpCommand(String protocol, String command, JSONObject payload,
                                 WebSocket socket) throws JSONException {
            if (protocol.equals("runtime") && command.equals("getruntime")) {
                JSONObject p = new JSONObject();
                p.put("type", "javafbp");
                p.put("version", "0.4");
                p.put("capabilities", new JSONArray() {{ put("protocol:component"); }});
                sendFbpResponse("runtime", "runtime", p, socket);
            } else if (protocol.equals("component") && command.equals("list")) {

            } else {

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

        /*
        Definition def = new Definition();
        def.nodes.put("Generate", GenerateTestData.class);
        def.nodes.put("Write", WriteToConsole.class);
        def.connections.add(new Definition.Connection() {{ srcNode="Generate"; srcPort="OUT"; tgtNode="Write"; tgtPort="IN"; }} );
        def.iips.add(new Definition.IIP() {{ tgtNode="Generate"; tgtPort="COUNT"; data = "10"; }});
        RuntimeNetwork.startNetwork(def);
        */

        WebSocketImpl.DEBUG = true;
        int port = 3569;
        Server s = new Server(port);
        s.start();
        System.out.println("Listening on port: " + s.getPort());
    }
    // Class.forName(className);

    // nodes: id, .class
    // addNode, delNode,
}
