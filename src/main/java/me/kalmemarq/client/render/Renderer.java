package me.kalmemarq.client.render;

import me.kalmemarq.Identifier;
import me.kalmemarq.Utils;
import me.kalmemarq.client.Client;
import me.kalmemarq.client.screen.Screen;
import me.kalmemarq.entity.PlayerEntity;
import me.kalmemarq.tile.Tile;
import me.kalmemarq.tile.Tiles;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Renderer {
	protected static final Logger LOGGER = LoggerFactory.getLogger(Renderer.class);
	private final Client client;
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f modelViewMatrix = new Matrix4f();

	public Renderer(Client client) {
		this.client = client;
	}
	
	public void render() {
		int error = GL11.glGetError();
		if (error != 0) {
			LOGGER.info("[BOP] OpenGL Error: {}", error);
		}

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Math.max(this.client.window.getFramebufferWidth() / 3, 1), Math.max(this.client.window.getFramebufferHeight() / 3, 1), 0, 1000, 3000);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0, 0, -2000);

		error = GL11.glGetError();
		if (error != 0) {
			LOGGER.info("[AOP] OpenGL Error: {}", error);
		}

		if (this.client.connection != null && this.client.player != null && this.client.level != null) {
			GL11.glPushMatrix();
			float xOffset = this.client.player.x - this.client.window.getFramebufferWidth() / 3 / 2;
			float yOffset = this.client.player.y - (this.client.window.getFramebufferHeight() / 3 - 8) / 2;
			if (xOffset < 0) {
				xOffset = 0;
			}
			if (yOffset < 0) {
				yOffset = 0;
			}
			if (xOffset > this.client.level.width * 16 - this.client.window.getFramebufferWidth() / 3) {
				xOffset = this.client.level.width * 16 - this.client.window.getFramebufferWidth() / 3;
			}

			if (yOffset > this.client.level.height * 16 - this.client.window.getFramebufferHeight() / 3) {
				yOffset = this.client.level.height * 16 - this.client.window.getFramebufferHeight() / 3;
			}

			GL11.glTranslatef(-xOffset, -yOffset, 0);

			if (this.client.level != null) {
				this.client.textureManager.bind(Identifier.of("minicraft:textures/tiles.png"));

				GL11.glBegin(GL11.GL_QUADS);

				for (int y = 0; y < this.client.level.height; ++y) {
					for (int x = 0; x < this.client.level.width; ++x) {
						Tile tile = Tiles.REGISTRY.getValueByRawId(this.client.level.getTileId(x, y));
						tile.render(x, y);
					}
				}

				GL11.glEnd();
			}

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			this.client.textureManager.bind(Identifier.of("minicraft:textures/skins.png"));

			float[] color = new float[3];
			int u = this.client.player.dir == 2 ? 3 * 16 : this.client.player.dir * 16;
			int v = 0;
			float u0 = (u) / 256.0f;
			float v0 = v / 256.0f;
			float u1 = (u + 16) / 256.0f;
			float v1 = (v + 16) / 256.0f;

			if (this.client.player.dir == 2) {
				float temp = u0;
				u0 = u1;
				u1 = temp;
			}

			GL11.glBegin(GL11.GL_QUADS);

			Utils.unpackARGB(this.client.player.color, color);
			GL11.glColor4f(color[0], color[1], color[2], 1.0f);

			GL11.glTexCoord2f(u0, v0);
			GL11.glVertex3f(this.client.player.x - 8, this.client.player.y - 8, 0);
			GL11.glTexCoord2f(u0, v1);
			GL11.glVertex3f(this.client.player.x - 8, this.client.player.y + 8, 0);
			GL11.glTexCoord2f(u1, v1);
			GL11.glVertex3f(this.client.player.x + 8, this.client.player.y + 8, 0);
			GL11.glTexCoord2f(u1, v0);
			GL11.glVertex3f(this.client.player.x + 8, this.client.player.y - 8, 0);

			for (PlayerEntity p : this.client.playerList.values()) {
				u = p.dir == 2 ? 3 * 16 : p.dir * 16;
				v = 0;
				u0 = (u) / 256.0f;
				v0 = v / 256.0f;
				u1 = (u + 16) / 256.0f;
				v1 = (v + 16) / 256.0f;

				if (p.dir == 2) {
					float temp = u0;
					u0 = u1;
					u1 = temp;
				}

				Utils.unpackARGB(p.color, color);
				GL11.glColor4f(color[0], color[1], color[2], 1.0f);

				GL11.glTexCoord2f(u0, v0);
				GL11.glVertex3f(p.x - 8, p.y - 8, 0);
				GL11.glTexCoord2f(u0, v1);
				GL11.glVertex3f(p.x - 8, p.y + 8, 0);
				GL11.glTexCoord2f(u1, v1);
				GL11.glVertex3f(p.x + 8, p.y + 8, 0);
				GL11.glTexCoord2f(u1, v0);
				GL11.glVertex3f(p.x + 8, p.y - 8, 0);
			}

			GL11.glEnd();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

			GL11.glPopMatrix();

			GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex3f(0, this.client.window.getFramebufferHeight() - 16, 0);
			GL11.glVertex3f(0, this.client.window.getFramebufferHeight(), 0);
			GL11.glVertex3f(104, this.client.window.getFramebufferHeight(), 0);
			GL11.glVertex3f(104, this.client.window.getFramebufferHeight() - 16, 0);
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

			enableBlend();
			setDefaultBlendFunc();
			this.client.textureManager.bind(Identifier.of("minicraft:textures/hud.png"));
			for (int i = 0; i < 10; ++i) {
				renderTexture(i * 8, this.client.window.getFramebufferHeight() / 3 - 16, 0, 8, 8, 0, 0, 8, 8, 88, 56, false, false);
				renderTexture(i * 8, this.client.window.getFramebufferHeight() / 3 - 8, 0, 8, 8, 8, 0, 8, 8, 88, 56, false, false);
			}
			disableBlend();

			if (this.client.showDebugHud) {
				this.client.font.drawTextOutlined("Minicraft Not Plus v0.1.0", 1, 1, 0xFFFFFF, 0x000000);
				this.client.font.drawTextOutlined(this.client.currentFps + " FPS", 1, 12, 0xFFFFFF, 0x000000);
			}
		}
		
		if (this.client.screen != null) this.client.screen.render(this.client.window.getFramebufferWidth() / 3, this.client.window.getFramebufferHeight() / 3, 0, 0);

		if (!this.client.window.isFocused()) {
			Screen.renderFrame(this.client.textureManager, this.client.font, this.client.window.getFramebufferWidth() / 3 / 2, this.client.window.getFramebufferHeight() / 3 / 2 - 20, "Click to Focus".length() * 8 + 16, 24, "Click to Focus", System.currentTimeMillis() / 100 % 4 == 0 ? 0x777777 : 0xFFFFFF);
		}
	}
	
	public static void renderTexture(int x, int y, int z, int width, int height, int u, int v, int us, int vs, int textureWidth, int textureHeight, boolean flipH, boolean flipV) {
		float u0 = u / (float) textureWidth;
		float v0 = v / (float) textureHeight;
		float u1 = (u + us) / (float) textureWidth;
		float v1 = (v + vs) / (float) textureHeight;
		
		if (flipH) {
			float temp = u0;
			u0 = u1;
			u1 = temp;
		}

		if (flipV) {
			float temp = v0;
			v0 = v1;
			v1 = temp;
		}

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(u0, v0);
		GL11.glVertex3f(x, y, z);
		GL11.glTexCoord2f(u0, v1);
		GL11.glVertex3f(x, y + height, z);
		GL11.glTexCoord2f(u1, v1);
		GL11.glVertex3f(x + width, y + height, z);
		GL11.glTexCoord2f(u1, v0);
		GL11.glVertex3f(x + width, y, z);
		GL11.glEnd();
	}
	
	public static void renderRect(int x, int y, int z, int width, int height, int color) {
		disableTexture();
		
		float[] c = new float[4];
		Utils.unpackARGB(color, c);
			
		GL11.glColor4f(c[0], c[1], c[2], 1.0f);
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glVertex3f(x, y, 0);
		GL11.glVertex3f(x, y + height, 0);
		GL11.glVertex3f(x + width, y + height, 0);
		GL11.glVertex3f(x + width, y, 0);
		
		GL11.glEnd();
		
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		enableTexture();
	}
	
	public static void enableBlend() {
		GL11.glEnable(GL11.GL_BLEND);
	}

	public static void disableBlend() {
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void enableTexture() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void disableTexture() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public static void setDefaultBlendFunc() {
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Deprecated
	public static void setMatrixMode(MatrixMode matrixMode) {
		GL11.glMatrixMode(matrixMode.glEnum);
	}
	
	@Deprecated
	public static void loadMatrixIdentity() {
		GL11.glLoadIdentity();
	}
	
	public enum MatrixMode {
		PROJECTION(GL11.GL_PROJECTION),
		MODELVIEW(GL11.GL_MODELVIEW);
		
		public final int glEnum;
		
		MatrixMode(int glEnum) {
			this.glEnum = glEnum;
		}
	}
}
