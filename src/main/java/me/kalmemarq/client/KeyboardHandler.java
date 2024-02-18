package me.kalmemarq.client;

import me.kalmemarq.client.render.Window;
import me.kalmemarq.client.screen.PauseMenu;
import org.lwjgl.glfw.GLFW;

public class KeyboardHandler implements Window.KeyboardEventHandler {
    private final Client client;

    public KeyboardHandler(Client client) {
        this.client = client;
    }

    @Override
    public void onKey(int key, int scancode, int action, int mods) {
        if (this.client.debugMode && action == GLFW.GLFW_RELEASE && key == GLFW.GLFW_KEY_F4) {
            this.client.showImGuiLayer = !this.client.showImGuiLayer;
        }

		if (action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_F3) {
			this.client.showDebugHud = !this.client.showDebugHud;
		}

		if (action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_F11) {
			this.client.window.toggleFullscreen();
		}

        if (action == GLFW.GLFW_PRESS && key == GLFW.GLFW_KEY_ESCAPE && this.client.connection != null && this.client.menu == null) {
            this.client.menu = new PauseMenu(this.client);
        }

        if (this.client.menu == null) return;

        if (action == GLFW.GLFW_RELEASE) {
            this.client.menu.keyReleased(key, mods);
        } else {
            this.client.menu.keyPressed(key, mods);
        }
    }

    @Override
    public void onCharTyped(int codepoint) {
        if (this.client.menu == null) return;

        this.client.menu.charTyped(codepoint);
    }
}
