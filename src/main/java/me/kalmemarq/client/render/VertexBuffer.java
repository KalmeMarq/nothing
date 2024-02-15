package me.kalmemarq.client.render;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

public class VertexBuffer {
	private int vao;
	private int vbo;
	private int ibo;
	
	public VertexBuffer() {
		this.vao = GL30.glGenVertexArrays();
		this.vbo = GL30.glGenBuffers();
		this.ibo = GL30.glGenBuffers();
	}
	
	public void upload(BufferBuilder.BuiltBuffer builtBuffer) {
		GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, this.vbo);
		GL20.glBufferData(GL20.GL_ARRAY_BUFFER, builtBuffer.buffer().limit(), GL20.GL_DYNAMIC_DRAW);

		GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, this.ibo);
		GL20.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, 0, GL20.GL_DYNAMIC_DRAW);
	}
	
	public void bind() {
		GL30.glBindVertexArray(this.vao);
	}
	
	public void close() {
		GL30.glDeleteVertexArrays(this.vao);
		GL30.glDeleteBuffers(this.vbo);
		GL30.glDeleteBuffers(this.ibo);
	}
}
