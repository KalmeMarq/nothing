package me.kalmemarq.client.screen;

import me.kalmemarq.client.Client;
import me.kalmemarq.client.render.Renderer;
import me.kalmemarq.common.network.packet.MessagePacket;
import org.lwjgl.glfw.GLFW;

import java.time.Instant;

public class ChatMenu extends Menu {
	private String input = "";
	
	public ChatMenu(Client client) {
		super(client);
	}

	@Override
	public void keyPressed(int key, int mods) {
		super.keyPressed(key, mods);
	
		if (key == GLFW.GLFW_KEY_ESCAPE) {
			this.client.menu = null;
		}
		
		if (key == GLFW.GLFW_KEY_BACKSPACE) {
			if (this.input.length() > 0) {
				this.input = this.input.substring(0, this.input.length() - 1);
			}
		}
		
		if (key == GLFW.GLFW_KEY_ENTER && this.input.trim().length() > 0) {
			this.client.connHandler.sendPacket(new MessagePacket(this.input.trim(), Instant.now()));
			this.input = "";
		}
	}

	@Override
	public void charTyped(int codepoint) {
		super.charTyped(codepoint);
		
		this.input += Character.toString(codepoint);
	}

	@Override
	public void render(int screenWidth, int screenHeight, int mouseX, int mouseY) {
		Renderer.renderRect(0, screenHeight - 12, 0, screenWidth, 12, 0x000000);
		this.font.drawText(this.input, 1, screenHeight - 10, 0xFFFFFF);
		
		for (int i = this.client.messages.size() - 1, j = 0; i >= 0; --i, j += 12) {
			this.font.drawText(this.client.messages.get(i), 1, screenHeight - 10 - j - 12, 0xFFFFFF);
		}
	}
}
