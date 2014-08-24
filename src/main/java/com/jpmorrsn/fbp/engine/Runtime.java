package com.jpmorrsn.fbp.engine;

import com.jpmorrsn.fbp.components.WriteToConsole;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.examples.components.GenerateTestData;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

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

    public static void main(final String[] argv) throws Exception {

        Definition def = new Definition();
        def.nodes.put("Generate", GenerateTestData.class);
        def.nodes.put("Write", WriteToConsole.class);
        def.connections.add(new Definition.Connection() {{ srcNode="Generate"; srcPort="OUT"; tgtNode="Write"; tgtPort="IN"; }} );
        def.iips.add(new Definition.IIP() {{ tgtNode="Generate"; tgtPort="COUNT"; data = "10"; }});

        RuntimeNetwork.startNetwork(def);
/*
        connect(component("Generate", ), port("OUT"), component("Write", WriteToConsole.class), port("IN"));

        initialize("100", component("Generate"), port("COUNT"));
*/

    }
    // Class.forName(className);

    // nodes: id, .class
    // addNode, delNode,
}
