package me.kalmemarq.client.screen;

import me.kalmemarq.common.Identifier;
import me.kalmemarq.client.Client;
import me.kalmemarq.client.render.Font;
import me.kalmemarq.client.render.Renderer;
import me.kalmemarq.client.texture.TextureManager;
import org.lwjgl.opengl.GL11;

public class Screen {
    protected Client client;
    protected Font font;

    public Screen(Client client) {
        this.client = client;
        this.font = client.getFont();
    }

    public void keyPressed(int key, int mods) {
    }

    public void keyReleased(int key, int mods) {
    }

    public void charTyped(int codepoint) {
    }

    public void mousePressed(int button, int mouseX, int mouseY) {
    }

    public void mouseReleased(int button, int mouseX, int mouseY) {
    }

    public void render(int screenWidth, int screenHeight, int mouseX, int mouseY) {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(0, screenHeight, 0);
		GL11.glVertex3f(screenWidth, screenHeight, 0);
		GL11.glVertex3f(screenWidth, 0, 0);
		GL11.glEnd();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
	
	public static void renderFrame(TextureManager textureManager, Font font, int x, int y, int width, int height, String text, int color) {
		textureManager.bind(Identifier.of("minicraft:textures/hud.png"));
		
		int xx = x;
		
		y += 8;
		x -= (width - 16) / 2 + 8;
		
		int horCount = (width - 16) / 8;
		
		Renderer.enableBlend();
		Renderer.setDefaultBlendFunc();
		GL11.glBegin(GL11.GL_QUADS);

		drawTextureQuad(x, y, 8, 8, 0, 48, 8, 8, 88, 56, false, false);
		drawTextureQuad(x, y + 16, 8, 8, 0, 48, 8, 8, 88, 56, false, true);
		
		for (int i = 0; i < horCount; ++i) {
			drawTextureQuad(x + 8 + 8 * i, y, 8, 8, 8, 48, 8, 8, 88, 56, false, false);
			drawTextureQuad(x + 8 + 8 * i, y + 8, 8, 8, 24, 48, 8, 8, 88, 56, false, true);
			drawTextureQuad(x + 8 + 8 * i, y + 16, 8, 8, 8, 48, 8, 8, 88, 56, false, true);
		}
		
		drawTextureQuad(x, y + 8, 8, 8, 16, 48, 8, 8, 88, 56, false, false);
		drawTextureQuad(x + width - 8, y + 8, 8, 8, 16, 48, 8, 8, 88, 56, true, false);
		
		drawTextureQuad(x + width - 8, y, 8, 8, 0, 48, 8, 8, 88, 56, true, false);
		drawTextureQuad(x + width - 8, y + 16,  8, 8, 0, 48, 8, 8, 88, 56, true, true);
				
		GL11.glEnd();

		font.drawText(text, xx - text.length() * 4, y + 8, color);
		
		Renderer.disableBlend();
	}
	
	private static void drawTextureQuad(int x, int y, int width, int height, int u, int v, int us, int vs, int textureWidth, int textureHeight, boolean flipH, boolean flipV) {
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
		
		GL11.glTexCoord2f(u0, v0);
		GL11.glVertex3f(x, y, 0);
		GL11.glTexCoord2f(u0, v1);
		GL11.glVertex3f(x, y + height, 0);
		GL11.glTexCoord2f(u1, v1);
		GL11.glVertex3f(x + width, y + height, 0);
		GL11.glTexCoord2f(u1, v0);
		GL11.glVertex3f(x + width, y, 0);
	}
}
