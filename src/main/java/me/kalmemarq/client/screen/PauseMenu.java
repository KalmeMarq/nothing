package me.kalmemarq.client.screen;

import me.kalmemarq.common.Identifier;
import me.kalmemarq.client.Client;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class PauseMenu extends Menu {
    public PauseMenu(Client client) {
        super(client);
		this.hasDarkBackground = false;
		this.menuContainer = new MenuContainer();
		this.menuContainer.offsetY = 30;
		this.menuContainer.withBackground = true;
		this.menuContainer.addEntry(new Entry("Continue Game", () -> {
			this.client.menu = null;
		}));
		this.menuContainer.addEntry(new Entry("Open To Lan", () -> {
			if (this.client.integratedServer != null) this.client.integratedServer.openToLan(8080);
		}));
		this.menuContainer.addEntry(new Entry(this.client.integratedServer == null ? "Disconnect" : "Quit to title", () -> {
			this.client.disconnect();
			this.client.menu = new TitleMenu(this.client);
		}));
    }
}
