package com.jpmorrsn.fbp.test.networks;


import com.jpmorrsn.fbp.components.Discard;
import com.jpmorrsn.fbp.engine.Network;
import com.jpmorrsn.fbp.experimental.components.Act1;
import com.jpmorrsn.fbp.experimental.components.Act2;
import com.jpmorrsn.fbp.experimental.components.ActorDriver;
import com.jpmorrsn.fbp.test.components.GenerateTestData;


/** This network is intended for testing ActorDriver */

public class TestActorDriver extends Network {

  static final String copyright = "Copyright 2007, 2008,..., 2012, "
      + "J. Paul Morrison.  At your option, you may copy, "
      + "distribute, or make derivative works under the terms of the " + "Clarified Artistic License, "
      + "based on the Everything Development Company's Artistic " + "License.  A document describing "
      + "this License may be found at " + "http://www.jpaulmorrison.com/fbp/artistic2.htm. "
      + "THERE IS NO WARRANTY; USE THIS PRODUCT AT YOUR OWN RISK.";

  @Override
  protected void define() {

    connect(component("Generate", GenerateTestData.class), port("OUT"), component("ActorDriver", ActorDriver.class),
        port("IN"));
    connect(component("ActorDriver"), port("OUT[0]"), component("Discard", Discard.class), port("IN"));
    initialize("100000", component("Generate"), port("COUNT"));
    Object[] classes = {
        "Act2",
        Act2.class,
        "Act1",
        Act1.class,
        "OutActor",
        com.jpmorrsn.fbp.experimental.components.OutActor.class,
        "Act1 -> Act2.a -> Act2.b ->Act2.c ->Act2.d ->Act2.e ->" + "Act2.f->Act2.g->Act2.h ->"
            + "Act2.i ->Act2.j->Act2.k->Act2.l->Act2.m->Act2.n ->" + "Act2.o ->Act2.p ->Act2.q ->Act2.r ->"
            + "Act2.i2 ->Act2.j2->Act2.k2->Act2.l2->Act2.m2->" + "Act2.n2 ->Act2.o2 ->Act2.p2 ->Act2.q2 ->Act2.r2 ->"
            + "Act2.i3 ->Act2.j3->Act2.k3->Act2.l3->Act2.m3->Act2.n3 ->" + "Act2.o3 -> Act2.p3 ->Act2.q3 ->Act2.r3 ->"
            + "Act2.i4 ->Act2.j4->Act2.k4->Act2.l4->Act2.m4->" + "Act2.n4 ->Act2.o4 ->Act2.p4 ->Act2.q4 ->Act2.r4 ->"
            + "Act2.i5->Act2.j5->Act2.k5->Act2.l5->Act2.m5->Act2.n5 ->" + "Act2.o5 ->Act2.p5 ->Act2.q5 ->Act2.r5 ->"
            + "Act2.i6 ->Act2.j6->Act2.k6->Act2.l6->Act2.m6->" + "Act2.n6 ->Act2.o6 ->Act2.p6 ->Act2.q6 ->Act2.r6 ->"
            + "Act2.i7 ->Act2.j7->Act2.k7->Act2.l7->Act2.m7->" + "Act2.n7 ->Act2.o7 ->Act2.p7 ->Act2.q7 ->Act2.r7 ->"
            + "Act2.i7 ->Act2.j8->Act2.k8->Act2.l8->Act2.m8->" + "Act2.n8 ->Act2.o8 ->Act2.p8 ->Act2.q8 ->Act2.r8 ->"
            + "Act2.i9 ->Act2.j9->Act2.k9->OutActor (0)" };
    initialize(classes, component("ActorDriver"), port("ACTS"));

  }

  public static void main(final String[] argv) throws Exception {
    new TestActorDriver().go();
  }
}
