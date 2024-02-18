package me.kalmemarq.client.screen;

import me.kalmemarq.client.Client;
import me.kalmemarq.common.network.packet.LoginPacket;
import me.kalmemarq.server.Server;
import org.lwjgl.glfw.GLFW;

public class MultiplayerMenu extends Menu {
	private final Menu parentScreen;
	
    private String ip = "";
    private String port = "";

    public MultiplayerMenu(Client client, Menu parentScreen) {
        super(client);
		this.parentScreen = parentScreen;
		this.menuContainer = new MenuContainer();
		this.menuContainer.offsetY = 30;
		this.menuContainer.addEntry(new TextBoxEntry("Server Address", this.ip, 255, "abcdefghijklmnopqrstuvwxyz_:ABCDEFGHIJKLMNOPQRSTUVWXYZ "::contains, newValue -> {
			this.ip = newValue;
		}));
		this.menuContainer.addEntry(new TextBoxEntry("Server Port", this.port, 255, "0123456789"::contains, newValue -> {
			this.port = newValue;
		}));
		this.menuContainer.addEntry(new EmptyEntry());
		this.menuContainer.addEntry(new Entry("Connect to Server", () -> {
			if (this.client.connect(this.ip, Integer.parseInt(this.port))) {
				this.client.connection.sendPacket(new LoginPacket(Server.PROTOCOL_VERSION, this.client.settings.username, this.client.settings.playerColorR << 16 | this.client.settings.playerColorG << 8 | this.client.settings.playerColorB));
				this.client.menu = new LoadingMenu(this.client, false);
			}
		}));
    }

    @Override
    public void keyPressed(int key, int mods) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.client.menu = this.parentScreen;
            return;
        }
		
		super.keyPressed(key, mods);
    }
}
