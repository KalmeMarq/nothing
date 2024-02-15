package me.kalmemarq.client.render;

import org.lwjgl.opengl.GL11;

public enum PrimitiveType {
	TRIANGLES(GL11.GL_TRIANGLES),
	TRIANGLE_STRIP(GL11.GL_TRIANGLE_STRIP),
	TRIANGLE_FAN(GL11.GL_TRIANGLE_FAN),
	QUADS(GL11.GL_QUADS),
	QUAD_STRIP(GL11.GL_QUAD_STRIP);
	
	public final int glEnum;
	
	PrimitiveType(int glEnum) {
		this.glEnum = glEnum;
	}
}
