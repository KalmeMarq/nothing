package me.kalmemarq.client.screen;

import me.kalmemarq.common.Identifier;
import me.kalmemarq.client.Client;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class PauseScreen extends Screen {
    private int selectedIndex;

    public PauseScreen(Client client) {
        super(client);
    }

    @Override
    public void keyPressed(int key, int mods) {
        if (key == GLFW.GLFW_KEY_DOWN) {
            this.selectedIndex = (this.selectedIndex + 1) % 3;
        } else if (key == GLFW.GLFW_KEY_UP) {
            --this.selectedIndex;
            if (this.selectedIndex < 0) this.selectedIndex = 2;
        }

        if (key == GLFW.GLFW_KEY_ENTER) {
            if (this.selectedIndex == 0) {
                this.client.screen = null;
            } else if (this.selectedIndex == 1) {
                if (this.client.integratedServer != null) this.client.integratedServer.openToLan(8080);
            } else if (this.selectedIndex == 2) {
                this.client.disconnect();
                this.client.screen = new TitleScreen(this.client);
            }
        }
    }

    private void renderFrame(int x, int y, int width, int height) {
        this.client.textureManager.bind(Identifier.of("minicraft:textures/hud.png"));
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex3f(x, y, 0);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex3f(x, y + height, 0);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex3f(x + width, y + height, 0);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex3f(x + width, y, 0);
        GL11.glEnd();
    }

    @Override
    public void render(int screenWidth, int screenHeight, int mouseX, int mouseY) {
        this.renderFrame(10, 20, 20 + "Continue Game".length() * 8, 20 + 24);
        this.font.drawText("Continue Game", 20, 30, this.selectedIndex == 0 ? 0xFFFFFF : 0x909090);
        this.font.drawText("Open to Lan", 20, 44, this.selectedIndex == 1 ? 0xFFFFFF : 0x909090);
        this.font.drawText("Disconnect", 20, 58, this.selectedIndex == 2 ? 0xFFFFFF : 0x909090);
    }
}
