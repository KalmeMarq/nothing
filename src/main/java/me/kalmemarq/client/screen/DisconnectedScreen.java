package me.kalmemarq.client.screen;

import me.kalmemarq.client.Client;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class DisconnectedScreen extends Screen {
	public DisconnectedScreen(Client client) {
		super(client);
	}

	@Override
	public void keyPressed(int key, int mods) {
		if (key == GLFW.GLFW_KEY_ESCAPE) {
			this.client.screen = new TitleScreen(this.client);
		}
	}

	@Override
	public void render(int screenWidth, int screenHeight, int mouseX, int mouseY) {
		super.render(screenWidth, screenHeight, mouseX, mouseY);

		this.font.drawText(this.client.connectionText.get(), 20, 30, 0xFFFFFF);
	}
}
