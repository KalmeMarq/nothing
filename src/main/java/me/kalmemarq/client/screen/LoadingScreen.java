package me.kalmemarq.client.screen;

import me.kalmemarq.client.Client;
import me.kalmemarq.common.network.packet.LoginPacket;

public class LoadingScreen extends Screen {
	public LoadingScreen(Client client, boolean doIntegrated) {
		super(client);

		Thread doStuff = new Thread(() -> {
			if (doIntegrated && this.client.startIntegrated()) {
				this.client.connection.sendPacket(new LoginPacket(this.client.settings.username, this.client.settings.playerColorR << 16 | this.client.settings.playerColorG << 8 | this.client.settings.playerColorB));
				this.client.integratedServer.run();
			}
		});
		doStuff.setDaemon(true);
		doStuff.start();
	}

	@Override
	public void render(int screenWidth, int screenHeight, int mouseX, int mouseY) {
		super.render(screenWidth, screenHeight, mouseX, mouseY);
		
		this.font.drawText("Doing something... hopefully", 20, 30, 0xFFFFFF);
	}
}
