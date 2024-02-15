package me.kalmemarq.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class Framebuffer {
    private int fbo = -1;
    private int txr = -1;
    private int rbo = -1;
    private int vao = -1;
    private int vbo = -1;
    private int ibo = -1;
	private int width;
	private int height;

    public void create(int width, int height) {
        this.width = width;
		this.height = height;
		
		this.fbo = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);

        this.txr = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.txr);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, 0L);

        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.txr, 0);

        this.rbo = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.rbo);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);

        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, this.rbo);

        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.out.println(getErrorStatus(status));
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        this.vao = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(this.vao);

        this.vbo = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.vbo);
		
		BufferBuilder bufferBuilder = new BufferBuilder(128);
		bufferBuilder.vertex(-1.0f, -1.0f, 0.0f).texture(0.0f, 0.0f).next();
		bufferBuilder.vertex(-1.0f,  1.0f, 0.0f).texture(0.0f, 1.0f).next();
		bufferBuilder.vertex( 1.0f,  1.0f, 0.0f).texture(1.0f, 1.0f).next();
		bufferBuilder.vertex( 1.0f, -1.0f, 0.0f).texture(1.0f, 0.0f).next();

        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, bufferBuilder.end(), GL30.GL_STATIC_DRAW);
		bufferBuilder.close();

        this.ibo = GL30.glGenBuffers();

		ByteBuffer indexBuffer = MemoryUtil.memAlloc(6);
		indexBuffer.put(0, (byte) 0);
		indexBuffer.put(1, (byte) 1);
		indexBuffer.put(2, (byte) 2);
		indexBuffer.put(3, (byte) 2);
		indexBuffer.put(4, (byte) 3);
		indexBuffer.put(5, (byte) 0);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.ibo);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL30.GL_STATIC_DRAW);
		MemoryUtil.memFree(indexBuffer);
		
        GL30.glBindVertexArray(0);
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
		GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void resize(int width, int height) {
    	this.width = Math.max(width, 1);
		this.height = Math.max(height, 1);

		GL11.glDeleteTextures(this.txr);
		GL30.glDeleteRenderbuffers(this.rbo);
		GL30.glDeleteFramebuffers(this.fbo);

		this.fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);

		this.txr = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.txr);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, this.width, this.height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, 0L);

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.txr, 0);

		this.rbo = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, this.rbo);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, this.width, this.height);

		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, this.rbo);

		int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println(getErrorStatus(status));
		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

    public void begin(int width, int height) {
		if (width != 0 && height != 0) {
			if (this.width != width || this.height != height) {
			this.resize(width, height);
			}
		}
		
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.fbo);
    }

    public void end() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void draw(Shader shader) {
        GL11.glColorMask(true, true, true, false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        GL20.glUseProgram(shader.getId());
        GL20.glActiveTexture(GL20.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.txr);
        GL30.glBindVertexArray(this.vao);
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.vbo);
		GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.ibo);
		
        shader.setSample("Sampler0", 0);
        shader.colorUniform.set(1.0f, 1.0f, 1.0f, 1.0f);
		shader.colorUniform.upload();

		VertexLayout.POSITION_TEXTURE.enable();
		
        GL30.glDrawElements(GL30.GL_TRIANGLES, 6, GL30.GL_UNSIGNED_BYTE, 0);

		VertexLayout.POSITION_TEXTURE.disable();
		
        GL30.glBindVertexArray(0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL20.glUseProgram(0);
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
		GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL11.glDepthMask(true);
        GL11.glColorMask(true, true, true, true);
    }

    public void close() {
        if (this.fbo != -1) {
            GL30.glDeleteBuffers(this.ibo);
            GL30.glDeleteBuffers(this.vbo);
            GL30.glDeleteVertexArrays(this.vao);
            GL11.glDeleteTextures(this.txr);
            GL30.glDeleteRenderbuffers(this.rbo);
            GL30.glDeleteFramebuffers(this.fbo);
        }
    }

    public static String getErrorStatus(int status) {
        return switch (status) {
            case GL30.GL_FRAMEBUFFER_UNDEFINED -> "GL_FRAMEBUFFER_UNDEFINED";
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT -> "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT -> "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER -> "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER";
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER -> "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER";
            case GL30.GL_FRAMEBUFFER_UNSUPPORTED -> "GL_FRAMEBUFFER_UNSUPPORTED";
            case GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE -> "GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE";
            case GL33.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS -> "GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS";
            default -> "Status: " + status;
        };
    }
}
