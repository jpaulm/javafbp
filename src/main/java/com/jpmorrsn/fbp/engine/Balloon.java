package com.jpmorrsn.fbp.engine;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.jpmorrsn.fbp.engine.Component;
import com.jpmorrsn.fbp.engine.ComponentDescription;
import com.jpmorrsn.fbp.engine.Connection;
import com.jpmorrsn.fbp.engine.FlowError;
import com.jpmorrsn.fbp.engine.InPort;
import com.jpmorrsn.fbp.engine.InputPort;
import com.jpmorrsn.fbp.engine.OutPort;
import com.jpmorrsn.fbp.engine.OutputPort;
import com.jpmorrsn.fbp.engine.Packet;

/**
 * Component to copy all incoming packets - balloons if output blocked. Note:
 * this has not been tested in production - more research is needed to determine
 * if this is the right approach to the problem it is trying to address. If the
 * limit logic on the list size is correct, it should be moved into an IIP...
 */
@ComponentDescription("Balloon if output blocked")
@OutPort("OUT")
@InPort("IN")
public class Balloon extends Component {

    static final String copyright = "Copyright 2009, 2012, J. Paul Morrison.  At your option, you may copy, "
	    + "distribute, or make derivative works under the terms of the Clarified Artistic License, "
	    + "based on the Everything Development Company's Artistic License.  A document describing "
	    + "this License may be found at http://www.jpaulmorrison.com/fbp/artistic2.htm. "
	    + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

    private InputPort inport;

    private OutputPort outport;

    Connection cnxt;

    LinkedBlockingQueue<Packet> boundedQueue = new LinkedBlockingQueue<Packet>(
	    50);

    Thread sendThread;

    // final Packet ENDPACKET = new Packet(Integer.MAX_VALUE, null, this);
    Packet ENDPACKET = null;

    // int local_Packet_count = 0;

    @Override
    protected void execute() {
	ENDPACKET = create(Integer.MAX_VALUE, null);
	// local_Packet_count++;
	start_SendPacket_Thread();
	processInputPackets();
	waitForEnd_SendPacket_Thread();
    }

    private void processInputPackets() {
	Packet p;
	while (null != (p = inport.receive())) {
	    // local_Packet_count++;
	    queuePacket(p);
	}
	queuePacket(ENDPACKET);
    }

    private void waitForEnd_SendPacket_Thread() {
	try {
	    sendThread.join();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    private void queuePacket(final Packet p) {
	try {
	    boundedQueue.put(p);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Start a thread to send packets periodically
     * 
     * @return the Thread that was created.
     */
    private void start_SendPacket_Thread() {
	Runnable runnable = new Unloader();
	sendThread = new Thread(runnable);
	int priority = sendThread.getPriority();
	sendThread.setPriority(Math.min(priority + 20, Thread.MAX_PRIORITY));
	sendThread.start();
    }

    @Override
    protected void openPorts() {
	ignorePacketCountError = true;
	inport = openInput("IN");

	outport = openOutput("OUT");
	cnxt = outport.getConnection();
	if (cnxt.getCapacity() != 1) {
	    FlowError.complain("Downstream capacity of Balloon must be 1");
	}

    }

    /**
     * Internal class to run concurrently with main thread and send packets once
     * every specified period: currently each hour.
     * 
     */
    class Unloader implements Runnable {

	public void run() {
	    Packet p;
	    while ((p = getPacket()) != null) {
		outport.send(p);
		// local_Packet_count--;
	    }
	    // setComponentPacketCount(outport, local_Packet_count);
	}

	// void setComponentPacketCount(final OutputPort outputPort, final int
	// i) {
	// outputPort.getSender().setPacketCount(i);
	// }

	private Packet getPacket() {
	    Packet p = null;
	    try {
		p = boundedQueue.poll(3600L, TimeUnit.SECONDS); // wait for 1 hr
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    /* Test for end Packet */
	    if (isEndPacket(p)) {
		drop(p);
		// local_Packet_count--;
		return null;
	    }
	    return p;
	}

	private boolean isEndPacket(final Packet p) {
	    return p.getType() == ENDPACKET.getType();
	}
    }
}
