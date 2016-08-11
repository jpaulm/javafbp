/*
 * JavaFBP - A Java Implementation of Flow-Based Programming (FBP)
 * Copyright (C) 2009, 2016 J. Paul Morrison
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, see the GNU Library General Public License v3
 * at https://www.gnu.org/licenses/lgpl-3.0.en.html for more details.
 */

package com.jpmorrsn.fbp.core.engine;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.jpmorrsn.fbp.core.engine.Component;
import com.jpmorrsn.fbp.core.engine.ComponentDescription;
import com.jpmorrsn.fbp.core.engine.Connection;
import com.jpmorrsn.fbp.core.engine.FlowError;
import com.jpmorrsn.fbp.core.engine.InPort;
import com.jpmorrsn.fbp.core.engine.InputPort;
import com.jpmorrsn.fbp.core.engine.OutPort;
import com.jpmorrsn.fbp.core.engine.OutputPort;
import com.jpmorrsn.fbp.core.engine.Packet;


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
