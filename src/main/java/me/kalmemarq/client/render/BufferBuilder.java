package me.kalmemarq.client.render;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class BufferBuilder {
    private final ByteBuffer buffer;
    private int vertexCount;
    private int offset;
	private PrimitiveType primitiveType;
	private VertexFormat format;

    public BufferBuilder(int initialCapacity) {
        this.buffer = MemoryUtil.memAlloc(initialCapacity);
    }

    public void begin(PrimitiveType primitiveType, VertexFormat format) {
        this.offset = 0;
        this.vertexCount = 0;
		this.primitiveType = primitiveType;
		this.format = format;
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

	public BufferBuilder color(int red, int green, int blue, int alpha) {
		this.buffer.put(this.offset, (byte) red);
		this.buffer.put(this.offset + 1, (byte) green);
		this.buffer.put(this.offset + 2, (byte) blue);
		this.buffer.put(this.offset + 3, (byte) alpha);
		this.offset += 4;
		return this;
	}
	
    public void next() {
        this.vertexCount++;
    }

    public ByteBuffer end() {
		return MemoryUtil.memSlice(this.buffer, 0, this.offset);
    }
	
	public BuiltBuffer endBuilt() {
		return new BuiltBuffer(MemoryUtil.memSlice(this.buffer, 0, this.offset), this.format, this.primitiveType, this.vertexCount);
	}

    public void close() {
        MemoryUtil.memFree(this.buffer);
    }
	
	public record BuiltBuffer(ByteBuffer buffer, VertexFormat format, PrimitiveType primitiveType, int vertexCount) {
	}
}
