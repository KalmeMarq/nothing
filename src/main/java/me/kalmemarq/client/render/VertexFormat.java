package me.kalmemarq.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class VertexFormat {
	public static final VertexFormatAttribute POSITION = new VertexFormatAttribute(3, ComponentType.FLOAT, (index, size, type, stride, offset) -> {
		GL30.glEnableVertexAttribArray(index);
		GL30.glVertexAttribPointer(index, size, type, false, stride, offset);
	}, GL30::glDisableVertexAttribArray);
	public static final VertexFormatAttribute TEXTURE = new VertexFormatAttribute(2, ComponentType.FLOAT, (index, size, type, stride, offset) -> {
		GL30.glEnableVertexAttribArray(index);
		GL30.glVertexAttribPointer(index, size, type, false, stride, offset);
	}, GL30::glDisableVertexAttribArray);
	
	public static final VertexFormat POSITION_TEXTURE = new VertexFormat(POSITION, TEXTURE);
	
	private final int[] offsets;
	private final VertexFormatAttribute[] attributes;
	private final int stride;
	
	public VertexFormat(VertexFormatAttribute... atributes) {
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
	
	public static class VertexFormatAttribute {
		public final int size;
		public final ComponentType type;
		public final int byteLength;
		private final EnableState enableState;
		private final DisableState disableState;
		
		public VertexFormatAttribute(int size, ComponentType type, EnableState enableState, DisableState disableState) {
			this.size = size;
			this.type = type;
			this.byteLength = size * type.byteLength;
			this.enableState = enableState;
			this.disableState = disableState;
		}
		
		public void enable(int index, int stride, int offset) {
			this.enableState.enable(index, this.size, this.type.glEnum, stride, offset);
		}

		public void disable(int index) {
			this.disableState.disable(index);
		}
		
		interface EnableState {
			void enable(int index, int size, int type, int stride, int offset);
		}

		interface DisableState {
			void disable(int index);
		}
	}
	
	enum ComponentType {
		FLOAT(GL11.GL_FLOAT, 4);
		
		public final int glEnum;
		public final int byteLength;
		
		ComponentType(int glEnum, int byteLength) {
			this.glEnum = glEnum;
			this.byteLength = byteLength;
		}
	}
}
