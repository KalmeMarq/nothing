package me.kalmemarq.client;

import me.kalmemarq.client.render.Window;
import me.kalmemarq.client.screen.PauseScreen;
import org.lwjgl.glfw.GLFW;

public class KeyboardHandler implements Window.KeyboardEventHandler {
    private final Client client;

    public KeyboardHandler(Client client) {
        this.client = client;
    }

    @Override
    public void onKey(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE && key == GLFW.GLFW_KEY_F3) {
            this.client.showImGuiLayer = !this.client.showImGuiLayer;
        }

        if (action == GLFW.GLFW_RELEASE && key == GLFW.GLFW_KEY_ESCAPE && this.client.connection != null && this.client.screen == null) {
            this.client.screen = new PauseScreen(this.client);
        }

        if (this.client.screen == null) return;

        if (action == GLFW.GLFW_RELEASE) {
            this.client.screen.keyReleased(key, mods);
        } else {
            this.client.screen.keyPressed(key, mods);
        }
    }

    @Override
    public void onCharTyped(int codepoint) {
        if (this.client.screen == null) return;

        this.client.screen.charTyped(codepoint);
    }
}
