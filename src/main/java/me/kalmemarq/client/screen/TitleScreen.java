package me.kalmemarq.client.screen;

import me.kalmemarq.client.Client;
import me.kalmemarq.network.packet.LoginPacket;
import me.kalmemarq.network.packet.Packet;
import me.kalmemarq.network.packet.RequestPreviousMessagesPacket;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TitleScreen extends Screen {
    private String[] items = {
            "Singleplayer",
            "Multiplayer",
            "Options",
            "Quit"
    };
    private int selectedIndex;

    public TitleScreen(Client client) {
        super(client);
    }

    @Override
    public void keyPressed(int key, int mods) {
        if (key == GLFW.GLFW_KEY_S || key == GLFW.GLFW_KEY_DOWN) {
            this.client.getSoundManager().play("sounds/select.ogg", 1.0f, 1.0f);
            this.selectedIndex = (this.selectedIndex + 1) % this.items.length;
        } else if (key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_UP) {
            this.client.getSoundManager().play("sounds/select.ogg", 1.0f, 1.0f);
            --this.selectedIndex;
            if (this.selectedIndex < 0) this.selectedIndex = this.items.length - 1;
        }

        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_SPACE) {
            this.client.getSoundManager().play("sounds/confirm.ogg", 1.0f, 1.0f);

            if (this.selectedIndex == 0) {
                if (this.client.startIntegrated()) {
                    this.client.connection.sendPackets(new Packet[] {new LoginPacket(this.client.settings.username, 0xFFFFFFF), new RequestPreviousMessagesPacket()});
                    this.client.screen = null;
                }
            } else if (this.selectedIndex == 1) {
                this.client.screen = new MultiplayerScreen(this.client);
            } else if (this.selectedIndex == 2) {
                this.client.screen = new OptionsScreen(this.client);
            } else if (this.selectedIndex == 3) {
                this.client.shutdown();
            }
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

        int y = 30;
        for (int i = 0; i < this.items.length; ++i) {
            this.font.drawText(this.items[i], 20, y, this.selectedIndex == i ? 0xFFFFFF : 0x909090);
            y += 14;
        }

        this.font.drawText(this.client.connectionText.get(), 20, screenHeight - 12, 0x909090);
    }
}
