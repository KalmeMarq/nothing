package me.kalmemarq.client.sound;

import me.kalmemarq.common.Identifier;
import me.kalmemarq.client.Client;
import me.kalmemarq.client.resource.DefaultResourcePack;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SoundManager {
    private boolean initialized;
    private long device = -1;
    private ALCCapabilities deviceCaps;
    private long context;
    private ALCapabilities caps;
    private Map<Identifier, Integer> buffers = new HashMap<>();
    private List<SoundSource> sources = new ArrayList<>();
	private SoundListener soundListener = new SoundListener();
	
    public void init() {
        this.device = ALC10.alcOpenDevice((ByteBuffer) null);
        this.deviceCaps = ALC.createCapabilities(this.device);

        this.context = ALC10.alcCreateContext(this.device, (IntBuffer) null);

        ALC10.alcMakeContextCurrent(this.context);
        this.caps = AL.createCapabilities(this.deviceCaps);

        this.initialized = true;
    }
	
	public String getCurrentDevice() {
		return ALC10.alcGetString(this.device, ALC11.ALC_ALL_DEVICES_SPECIFIER);
	}
	
	public List<String> getAllDevicesAvailable() {
		return ALUtil.getStringList(0L, ALC11.ALC_ALL_DEVICES_SPECIFIER);
	}

	public Map<Identifier, Integer> getBuffers() {
		return this.buffers;
	}

	public void tick() {
        Iterator<SoundSource> iter = this.sources.iterator();
        while (iter.hasNext()) {
            SoundSource source = iter.next();

            if (source.isStopped()) {
                source.close();
                iter.remove();
            }
        }
    }

    public void play(Identifier name, float volume, float pitch) {
        if (!this.initialized) {
            System.out.println("Sound manager is not yet initialized");
            return;
        }

        int buffer;
        if (!this.buffers.containsKey(name)) {
            buffer = AL10.alGenBuffers();

            var rp = DefaultResourcePack.get();
            var m = rp.getResource(name);
            if (m.isEmpty()) {
                System.out.println("Sound path not found");
                AL10.alDeleteBuffers(buffer);
                return;
            }

            try {
                ByteBuffer data = Client.getByteBufferFromInputStream(m.get().inputSupplier().get());

                try (MemoryStack stack = MemoryStack.stackPush()) {
                    IntBuffer channels = stack.mallocInt(1);
                    IntBuffer sampleRate = stack.mallocInt(1);
                    ShortBuffer audioData = STBVorbis.stb_vorbis_decode_memory(data, channels, sampleRate);

                    AL10.alBufferData(buffer, channels.get(0) == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, audioData, sampleRate.get(0));
                }

                MemoryUtil.memFree(data);
            } catch (IOException e) {
                e.printStackTrace();
                AL10.alDeleteBuffers(buffer);
                return;
            }

            this.buffers.put(name, buffer);
        } else {
            buffer = this.buffers.get(name);
        }

        SoundSource source = new SoundSource();
        source.setBuffer(buffer);
        source.setVolume(volume);
        source.setPitch(pitch);
        source.play();

        this.sources.add(source);
    }

    public void pauseAll() {
        this.sources.forEach(SoundSource::pause);
    }

    public void resumeAll() {
        this.sources.forEach(SoundSource::resume);
    }

    public void stopAll() {
        this.sources.forEach(SoundSource::stop);
    }

    public void close() {
        this.sources.forEach(SoundSource::close);
        this.sources.clear();
        this.buffers.values().forEach(AL10::alDeleteBuffers);
        this.buffers.clear();

        if (this.device != -1) {
            ALC10.alcMakeContextCurrent(0);
            ALC10.alcDestroyContext(this.context);
            ALC10.alcCloseDevice(this.device);
        }
    }
}
