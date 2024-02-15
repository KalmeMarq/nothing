package me.kalmemarq.client.screen;

import me.kalmemarq.client.Client;
import me.kalmemarq.common.network.packet.LoginPacket;
import org.lwjgl.glfw.GLFW;

public class MultiplayerScreen extends Screen {
	private final Screen parentScreen;
	
    private int selectedIndex;
    private String ip = "";
    private String port = "";

    public MultiplayerScreen(Client client, Screen parentScreen) {
        super(client);
		this.parentScreen = parentScreen;
    }

    @Override
    public void keyPressed(int key, int mods) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.client.screen = this.parentScreen;
            return;
        }

        if (key == GLFW.GLFW_KEY_DOWN) {
            this.selectedIndex = (this.selectedIndex + 1) % 3;
        } else if (key == GLFW.GLFW_KEY_UP) {
            --this.selectedIndex;
            if (this.selectedIndex < 0) this.selectedIndex = 2;
        }

        if (key == GLFW.GLFW_KEY_BACKSPACE) {
            if (this.selectedIndex == 0 && !this.ip.isEmpty()) {
                this.ip = this.ip.substring(0, this.ip.length() - 1);
            }

            if (this.selectedIndex == 1 && !this.port.isEmpty()) {
                this.port = this.port.substring(0, this.port.length() - 1);
            }
        }

        if (key == GLFW.GLFW_KEY_ENTER) {
            if (this.selectedIndex == 2) {
                if (this.client.connect(this.ip, Integer.parseInt(this.port))) {
                    this.client.connection.sendPacket(new LoginPacket(this.client.settings.username, this.client.settings.playerColorR << 16 | this.client.settings.playerColorG << 8 | this.client.settings.playerColorB));
                    this.client.screen = new LoadingScreen(this.client, false);
                }
            }
        }
    }

    @Override
    public void charTyped(int codepoint) {
        if (this.selectedIndex == 0) {
            this.ip += Character.toString(codepoint);
        } else if (this.selectedIndex == 1) {
            this.port += Character.toString(codepoint);
        }
    }

    @Override
    public void render(int screenWidth, int screenHeight, int mouseX, int mouseY) {
		super.render(screenWidth, screenHeight, mouseX, mouseY);

        this.font.drawText("Server IP: " + this.ip, 20, 30, this.selectedIndex == 0 ? 0xFFFFFF : 0x909090);
        this.font.drawText("Server Port: " + this.port, 20, 44, this.selectedIndex == 1 ? 0xFFFFFF : 0x909090);

        this.font.drawText("Connect to Server", 20, 72, this.selectedIndex == 2 && !this.ip.isEmpty() && !this.port.isEmpty() ? 0xFFFFFF : 0x909090);
    }
}
