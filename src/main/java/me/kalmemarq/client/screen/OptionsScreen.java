package me.kalmemarq.client.screen;

import me.kalmemarq.Utils;
import me.kalmemarq.client.Client;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class OptionsScreen extends Screen {
    private int selectedIndex;

    public OptionsScreen(Client client) {
        super(client);
    }

    @Override
    public void keyPressed(int key, int mods) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.client.screen = new TitleScreen(this.client);
            return;
        }

        if (key == GLFW.GLFW_KEY_DOWN) {
            this.selectedIndex = (this.selectedIndex + 1) % 4;
        } else if (key == GLFW.GLFW_KEY_UP) {
            --this.selectedIndex;
            if (this.selectedIndex < 0) this.selectedIndex = 3;
        }

        if (key == GLFW.GLFW_KEY_BACKSPACE && this.selectedIndex == 0) {
            if (this.client.settings.username.length() > 0) {
                this.client.settings.username = this.client.settings.username.substring(0, this.client.settings.username.length() - 1);
            }
        }

        if (key == GLFW.GLFW_KEY_LEFT) {
            if (this.selectedIndex == 1) this.client.settings.playerColorR = Utils.clamp(this.client.settings.playerColorR - 1, 0, 255);
            if (this.selectedIndex == 2) this.client.settings.playerColorG = Utils.clamp(this.client.settings.playerColorG - 1, 0, 255);
            if (this.selectedIndex == 3) this.client.settings.playerColorB = Utils.clamp(this.client.settings.playerColorB - 1, 0, 255);
        } else if (key == GLFW.GLFW_KEY_RIGHT) {
            if (this.selectedIndex == 1) this.client.settings.playerColorR = Utils.clamp(this.client.settings.playerColorR + 1, 0, 255);
            if (this.selectedIndex == 2) this.client.settings.playerColorG = Utils.clamp(this.client.settings.playerColorG + 1, 0, 255);
            if (this.selectedIndex == 3) this.client.settings.playerColorB = Utils.clamp(this.client.settings.playerColorB + 1, 0, 255);
        }
    }

    @Override
    public void charTyped(int codepoint) {
        this.client.settings.username += Character.toString(codepoint);
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

        this.font.drawText("Username: " + this.client.settings.username, 20, 30, this.selectedIndex == 0 ? 0xFFFFFF : 0x909090);
        this.font.drawText("Player Color Red: " + this.client.settings.playerColorR, 20, 44, this.selectedIndex == 1 ? 0xFFFFFF : 0x909090);
        this.font.drawText("Player Color Blue: " + this.client.settings.playerColorG, 20, 58, this.selectedIndex == 2 ? 0xFFFFFF : 0x909090);
        this.font.drawText("Player Color Green: " + this.client.settings.playerColorB, 20, 72, this.selectedIndex == 3 ? 0xFFFFFF : 0x909090);

        GL11.glColor4f(this.client.settings.playerColorR / 255.0f, this.client.settings.playerColorG / 255.0f, this.client.settings.playerColorB / 255.0f, 1.0f);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(20, 86, 0);
        GL11.glVertex3f(20, 86 + 8, 0);
        GL11.glVertex3f(20 + 8, 86 + 8, 0);
        GL11.glVertex3f(20 + 8, 86, 0);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
