package me.kalmemarq.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class VertexLayout {
	public static final VertexAttribute POSITION = new VertexAttribute(3, ComponentType.FLOAT);
	public static final VertexAttribute TEXTURE = new VertexAttribute(2, ComponentType.FLOAT);
	
	public static final VertexLayout POSITION_TEXTURE = new VertexLayout(POSITION, TEXTURE);
	
	private final int[] offsets;
	private final VertexAttribute[] attributes;
	private final int stride;
	
	public VertexLayout(VertexAttribute... atributes) {
		this.offsets = new int[atributes.length];
		this.attributes = atributes;
		
		int strd = 0;
		for (int i = 0; i < atributes.length; ++i) {
			this.offsets[i] = strd;
			strd += atributes[i].byteLength;
		}
		this.stride = strd;
	}

	public void enable() {
		for (int i = 0; i < this.attributes.length; ++i) {
			this.attributes[i].enable(i, this.stride, this.offsets[i]);
		}
	}

	public void disable() {
		for (int i = 0; i < this.attributes.length; ++i) {
			this.attributes[i].disable(i);
		}
	}
	
	public static class VertexAttribute {
		public final int size;
		public final ComponentType type;
		public final int byteLength;
		
		public VertexAttribute(int size, ComponentType type) {
			this.size = size;
			this.type = type;
			this.byteLength = size * type.byteLength;
		}
		
		public void enable(int index, int stride, int offset) {
			GL30.glEnableVertexAttribArray(index);
			GL30.glVertexAttribPointer(index, this.size, this.type.glEnum, false, stride, offset);
		}

		public void disable(int index) {
			GL30.glDisableVertexAttribArray(index);
		}
	}
	
	public enum ComponentType {
		FLOAT(GL11.GL_FLOAT, 4);
		
		public final int glEnum;
		public final int byteLength;
		
		ComponentType(int glEnum, int byteLength) {
			this.glEnum = glEnum;
			this.byteLength = byteLength;
		}
	}
}
