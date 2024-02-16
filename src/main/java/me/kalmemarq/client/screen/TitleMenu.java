package me.kalmemarq.client.screen;

import me.kalmemarq.client.render.Renderer;
import me.kalmemarq.common.Identifier;
import me.kalmemarq.client.Client;

public class TitleMenu extends Menu {
	private final Identifier TITLE_TEXTURE = Identifier.of("minicraft:textures/title.png");
	
    public TitleMenu(Client client) {
        super(client);
		this.menuContainer = new MenuContainer();
		this.menuContainer.withBackground = true;
		this.menuContainer.addEntry(new Menu.Entry("Singleplayer", () -> {
			this.client.menu = new LoadingMenu(this.client, true);
		}));
		this.menuContainer.addEntry(new Menu.Entry("Multiplayer", () -> {
			this.client.menu = new MultiplayerMenu(this.client, this);
		}));
		this.menuContainer.addEntry(new Menu.Entry("Help", () -> {
		}));
		this.menuContainer.addEntry(new Menu.Entry("Options", () -> {
			this.client.menu = new OptionsMenu(this.client, this);
		}));
		this.menuContainer.addEntry(new Menu.Entry("Quit", () -> {
			this.client.shutdown();
		}));
    }

    @Override
    public void render(int screenWidth, int screenHeight, int mouseX, int mouseY) {
        super.render(screenWidth, screenHeight, mouseX, mouseY);

		this.client.textureManager.bind(this.TITLE_TEXTURE);
		Renderer.renderTexture(20, 20, 0, 112, 16, 0, 0, 112, 16, 112, 16, false, false);

		this.font.drawText("Version 0.1.0", 1, 1, 0x555555);
    }
}
