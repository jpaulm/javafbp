package com.jpmorrsn.fbp.examples.networks;

	import com.jpmorrsn.fbp.engine.Network;

	public class TestLoadBalanceWithSubstreams extends Network {
	  
	  @Override
	  protected void define() {
		boolean makeMergeSubstreamSensitive = true;
		
	    component("GenData", com.jpmorrsn.fbp.examples.components.GenSS.class);
	    component("LoadBalance", com.jpmorrsn.fbp.components.LoadBalance.class);	    
	    component("Passthru0", com.jpmorrsn.fbp.examples.components.SlowPass.class);
	    component("Passthru1", com.jpmorrsn.fbp.components.Passthru.class);
	    component("Passthru2", com.jpmorrsn.fbp.components.Passthru.class);
	    component("PassthruF", com.jpmorrsn.fbp.examples.components.SlowPass.class);
	    component("Show", com.jpmorrsn.fbp.components.WriteToConsole.class);
	    
	    connect(component("GenData"), port("OUT"), component("LoadBalance"), port("IN"), 4);
	    connect(component("LoadBalance"), port("OUT[0]"), component("Passthru0"), port("IN"), 4);
	    connect(component("LoadBalance"), port("OUT[1]"), component("Passthru1"), port("IN"), 4);
	    connect(component("LoadBalance"), port("OUT[2]"), component("Passthru2"), port("IN"), 4);
	    
		if (makeMergeSubstreamSensitive) {
			component("SubstreamSensitiveMerge",
					com.jpmorrsn.fbp.components.SubstreamSensitiveMerge.class);
			
			connect(component("Passthru0"), port("OUT"),
					component("SubstreamSensitiveMerge"), port("IN[0]"), 4);
			connect(component("Passthru1"), port("OUT"),
					component("SubstreamSensitiveMerge"), port("IN[1]"), 4);
			connect(component("Passthru2"), port("OUT"),
					component("SubstreamSensitiveMerge"), port("IN[2]"), 4);
			
			connect(component("SubstreamSensitiveMerge"), port("OUT"),
					component("PassthruF"), port("IN"), 4);
		} else {
			connect(component("Passthru0"), port("OUT"), component("PassthruF"),
					port("IN"), 4);
			connect(component("Passthru1"), port("OUT"), component("PassthruF"),
					port("IN"), 4);
			connect(component("Passthru2"), port("OUT"), component("PassthruF"),
					port("IN"), 4);
		}
	    connect(component("PassthruF"), port("OUT"), component("Show"), port("IN"), 4);
	    
	    initialize("400", component("GenData"), port("COUNT"));
	    
	  }

	  public static void main(final String[] argv) throws Exception {
	    new TestLoadBalanceWithSubstreams().go();
	  }

	}

 
