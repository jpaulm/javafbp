package com.jpaulmorrison.fbp.resourcekit.examples.networks;

import com.jpaulmorrison.fbp.core.components.audio.GenSamples;
import com.jpaulmorrison.fbp.core.components.audio.JingleBells;
import com.jpaulmorrison.fbp.core.components.audio.SoundMixer;
import com.jpaulmorrison.fbp.core.engine.Network;

public class PlayTune extends Network {
	
	// Jingle Bells - one voice

	@Override
	protected void define() throws Exception {
		component("JingleBells", JingleBells.class);
		component("GenSamples", GenSamples.class);
		component("SoundMixer", SoundMixer.class);
		
	    connect("JingleBells.OUT", "GenSamples.IN");
	    connect("GenSamples.OUT", "SoundMixer.IN");
	    initialize("1", "SoundMixer.GAINS");
	}

	public static void main(String[] args) throws Throwable {
		new PlayTune().go();

	}

}
