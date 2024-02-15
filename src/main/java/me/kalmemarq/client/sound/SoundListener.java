package me.kalmemarq.client.sound;

import org.lwjgl.openal.AL10;

public class SoundListener {
	public void setPosition(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
	}

	public void setVelocity(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_VELOCITY, x, y, z);
	}
}
