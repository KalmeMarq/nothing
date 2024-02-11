package me.kalmemarq.client.screen;

import me.kalmemarq.Utils;
import me.kalmemarq.client.Client;
import me.kalmemarq.network.packet.LoginPacket;
import me.kalmemarq.network.packet.Packet;
import me.kalmemarq.network.packet.RequestPreviousMessagesPacket;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class MultiplayerScreen extends Screen {
    private int selectedIndex;
    private String ip = "";
    private String port = "";

    public MultiplayerScreen(Client client) {
        super(client);
    }

    @Override
    public void keyPressed(int key, int mods) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.client.screen = new TitleScreen(this.client);
            return;
        }

        if (key == GLFW.GLFW_KEY_DOWN) {
            this.selectedIndex = (this.selectedIndex + 1) % 3;
        } else if (key == GLFW.GLFW_KEY_UP) {
            --this.selectedIndex;
            if (this.selectedIndex < 0) this.selectedIndex = 2;
        }

        if (key == GLFW.GLFW_KEY_BACKSPACE) {
            if (this.selectedIndex == 0 && this.ip.length() > 0) {
                this.ip = this.ip.substring(0, this.ip.length() - 1);
            }

            if (this.selectedIndex == 1 && this.port.length() > 0) {
                this.port = this.port.substring(0, this.port.length() - 1);
            }
        }

        if (key == GLFW.GLFW_KEY_ENTER) {
            if (this.selectedIndex == 2) {
                if (this.client.connect(this.ip, Integer.parseInt(this.port))) {
                    this.client.connection.sendPackets(new Packet[] {new LoginPacket(this.client.settings.username, 0xFF_FFFFFF), new RequestPreviousMessagesPacket()});
                    this.client.screen = null;
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

        this.font.drawText("Server IP: " + this.ip, 20, 30, this.selectedIndex == 0 ? 0xFFFFFF : 0x909090);
        this.font.drawText("Server Port: " + this.port, 20, 44, this.selectedIndex == 1 ? 0xFFFFFF : 0x909090);

        this.font.drawText("Connect to Server", 20, 72, this.selectedIndex == 2 && this.ip.length() > 0 && this.port.length() > 0 ? 0xFFFFFF : 0x909090);
    }
}
