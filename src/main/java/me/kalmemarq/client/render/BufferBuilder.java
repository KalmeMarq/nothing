package me.kalmemarq.client.render;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class BufferBuilder {
    private ByteBuffer buffer;
    private int vertexCount;
    private int offset;

    public BufferBuilder(int initialCapacity) {
        this.buffer = MemoryUtil.memAlloc(initialCapacity);
    }

    public void begin() {
        this.offset = 0;
        this.vertexCount = 0;
    }

    public BufferBuilder vertex(float x, float y, float z) {
        this.buffer.putFloat(this.offset, x);
        this.buffer.putFloat(this.offset + 4, y);
        this.buffer.putFloat(this.offset + 8, z);
        this.offset += 12;
        return this;
    }

    public BufferBuilder texture(float u, float v) {
        this.buffer.putFloat(this.offset, u);
        this.buffer.putFloat(this.offset + 4, v);
        this.offset += 8;
        return this;
    }
	
    public void next() {
        this.vertexCount++;
    }

    public ByteBuffer end() {
		return MemoryUtil.memSlice(this.buffer, 0, this.offset);
    }

    public void close() {
        MemoryUtil.memFree(this.buffer);
    }
}
