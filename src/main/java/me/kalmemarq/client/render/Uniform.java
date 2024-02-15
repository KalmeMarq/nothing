package me.kalmemarq.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Uniform {
	private final String name;
	private final int location;
	private final DataType type;
	private IntBuffer intBuffer;
	private FloatBuffer floatBuffer;
	
	public Uniform(String name, int location, DataType type) {
		this.name = name;
		this.location = location;
		this.type = type;
		Shader.LOGGER.info("Name: {} Loc: {} Type: {} Size: {}", name, location, type.id, type.size);
		if (type.isFloat) {
			this.floatBuffer = MemoryUtil.memAllocFloat(type.size);
		} else {
			this.intBuffer = MemoryUtil.memAllocInt(type.size);
		}
	}
	
	public void upload() {
		if (this.type.isFloat) {
			this.floatBuffer.flip();
			this.floatBuffer.limit(this.type.size);

			int error = GL11.glGetError();
			if (error != 0) {
				Shader.LOGGER.info("[BUU] OpenGL Error: {}", error);
			}

			switch (this.type) {
				case Float1 -> GL20.glUniform1fv(this.location, this.floatBuffer);
				case Float2 -> GL20.glUniform2fv(this.location, this.floatBuffer);
				case Float3 -> GL20.glUniform3fv(this.location, this.floatBuffer);
				case Float4 -> GL20.glUniform4fv(this.location, this.floatBuffer);
				case Mat3x3 -> GL20.glUniformMatrix3fv(this.location, false, this.floatBuffer);
				case Mat4x4 -> GL20.glUniformMatrix4fv(this.location, false, this.floatBuffer);
			}

			error = GL11.glGetError();
			if (error != 0) {
				Shader.LOGGER.info("[AUU] OpenGL Error: {}", error);
			}

		} else {
			this.intBuffer.flip();
			this.intBuffer.limit(this.type.size);

			switch (this.type) {
				case Int1 -> GL20.glUniform1iv(this.location, this.intBuffer);
				case Int2 -> GL20.glUniform2iv(this.location, this.intBuffer);
				case Int3 -> GL20.glUniform3iv(this.location, this.intBuffer);
				case Int4 -> GL20.glUniform4iv(this.location, this.intBuffer);
			}
		}
	}

	public void set(int value) {
		this.intBuffer.position(0);
		this.intBuffer.put(0, value);
	}

	public void set(float value) {
		this.floatBuffer.position(0);
		this.floatBuffer.put(0, value);
	}

	public void set(int value0, int value1) {
		this.intBuffer.position(0);
		this.intBuffer.put(0, value0);
		this.intBuffer.put(1, value1);
	}

	public void set(float value0, float value1) {
		this.floatBuffer.position(0);
		this.floatBuffer.put(0, value0);
		this.floatBuffer.put(1, value1);
	}

	public void set(int value0, int value1, int value2) {
		this.intBuffer.position(0);
		this.intBuffer.put(0, value0);
		this.intBuffer.put(1, value1);
		this.intBuffer.put(2, value2);
	}

	public void set(float value0, float value1, float value2) {
		this.floatBuffer.position(0);
		this.floatBuffer.put(0, value0);
		this.floatBuffer.put(1, value1);
		this.floatBuffer.put(2, value2);
	}

	public void set(int value0, int value1, int value2, int value3) {
		this.intBuffer.position(0);
		this.intBuffer.put(0, value0);
		this.intBuffer.put(1, value1);
		this.intBuffer.put(2, value2);
		this.intBuffer.put(3, value3);
	}

	public void set(float value0, float value1, float value2, float value3) {
		this.floatBuffer.position(0);
		this.floatBuffer.put(0, value0);
		this.floatBuffer.put(1, value1);
		this.floatBuffer.put(2, value2);
		this.floatBuffer.put(3, value3);
	}
	
	public void close() {
		if (this.type.isFloat) {
			MemoryUtil.memFree(this.floatBuffer);
		} else {
			MemoryUtil.memFree(this.intBuffer);
		}
	}
	
	public enum DataType {
		Int1("int", 1),
		Int2("vec2i", 2),
		Int3("vec3i", 3),
		Int4("vec4i", 4),
		Float1("float", 1),
		Float2("vec2", 2),
		Float3("vec3", 3),
		Float4("vec4", 4),
		Mat3x3("mat3", 9),
		Mat4x4("mat4", 16);
		
		public final String id;
		public final int size;
		public final boolean isFloat;
		
		DataType(String id, int size) {
			this.id = id;
			this.size = size;
			this.isFloat = !id.endsWith("i") || id.startsWith("mat");
		}
		
		public static DataType getById(String id) {
			for (DataType type : DataType.values()) {
				if (type.id.equals(id)) {
					return type;
				}
			}
			return null;
		}
	}
}
