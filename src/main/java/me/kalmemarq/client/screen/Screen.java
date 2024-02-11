package me.kalmemarq.client.screen;

import me.kalmemarq.client.Client;
import me.kalmemarq.client.render.Font;

public class Screen {
    protected Client client;
    protected Font font;

    public Screen(Client client) {
        this.client = client;
        this.font = client.getFont();
    }

    public void keyPressed(int key, int mods) {
    }

    public void keyReleased(int key, int mods) {
    }

    public void charTyped(int codepoint) {
    }

    public void mousePressed(int button, int mouseX, int mouseY) {
    }

    public void mouseReleased(int button, int mouseX, int mouseY) {
    }

    public void render(int screenWidth, int screenHeight, int mouseX, int mouseY) {
    }
}
