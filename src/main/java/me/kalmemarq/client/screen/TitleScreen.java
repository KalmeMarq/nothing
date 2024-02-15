package me.kalmemarq.client.screen;

import me.kalmemarq.Identifier;
import me.kalmemarq.client.Client;
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
            this.client.getSoundManager().play(Identifier.of("minicraft:sounds/select.ogg"), 1.0f, 1.0f);
            this.selectedIndex = (this.selectedIndex + 1) % this.items.length;
        } else if (key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_UP) {
            this.client.getSoundManager().play(Identifier.of("minicraft:sounds/select.ogg"), 1.0f, 1.0f);
            --this.selectedIndex;
            if (this.selectedIndex < 0) this.selectedIndex = this.items.length - 1;
        }

        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_SPACE) {
            this.client.getSoundManager().play(Identifier.of("minicraft:sounds/confirm.ogg"), 1.0f, 1.0f);

            if (this.selectedIndex == 0) {
               this.client.screen = new LoadingScreen(this.client, true);
            } else if (this.selectedIndex == 1) {
                this.client.screen = new MultiplayerScreen(this.client, this);
            } else if (this.selectedIndex == 2) {
                this.client.screen = new OptionsScreen(this.client, this);
            } else if (this.selectedIndex == 3) {
                this.client.shutdown();
            }
        }
    }

    @Override
    public void render(int screenWidth, int screenHeight, int mouseX, int mouseY) {
        super.render(screenWidth, screenHeight, mouseX, mouseY);

        int y = 30;
        for (int i = 0; i < this.items.length; ++i) {
            this.font.drawText(this.items[i], 20, y, this.selectedIndex == i ? 0xFFFFFF : 0x909090);
            y += 14;
        }
		
		this.font.drawText("Version 0.1.0", 1, 1, 0x555555);
    }
}
