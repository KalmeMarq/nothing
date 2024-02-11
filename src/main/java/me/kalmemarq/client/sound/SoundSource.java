package me.kalmemarq.client.sound;

import org.lwjgl.openal.AL10;

public class SoundSource {
    private int handle;

    public SoundSource() {
        this.handle = AL10.alGenSources();
    }

    public void setBuffer(int buffer) {
        AL10.alSourcei(this.handle, AL10.AL_BUFFER, buffer);
    }

    public void play() {
        if (!this.isPlaying()) {
            AL10.alSourcePlay(this.handle);
        }
    }

    public void pause() {
        if (this.isPlaying()) {
            AL10.alSourcePause(this.handle);
        }
    }

    public void resume() {
        if (this.isPaused()) {
            AL10.alSourcePlay(this.handle);
        }
    }

    public void stop() {
        if (this.isPlaying()) {
            AL10.alSourceStop(this.handle);
        }
    }

    public void setPosition(float x, float y, float z) {
        AL10.alSource3f(this.handle, AL10.AL_POSITION, x, y, z);
    }

    public void setVolume(float volume) {
        AL10.alSourcef(this.handle, AL10.AL_GAIN, volume);
    }

    public void setPitch(float pitch) {
        AL10.alSourcef(this.handle, AL10.AL_PITCH, pitch);
    }

    public boolean isPlaying() {
        return AL10.alGetSourcei(this.handle, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public boolean isPaused() {
        return AL10.alGetSourcei(this.handle, AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED;
    }

    public boolean isStopped() {
        return AL10.alGetSourcei(this.handle, AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED;
    }

    public void close() {
        AL10.alDeleteSources(this.handle);
    }
}
