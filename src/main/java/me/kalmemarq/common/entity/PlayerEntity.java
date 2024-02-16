package me.kalmemarq.common.entity;

import me.kalmemarq.client.Client;
import me.kalmemarq.common.tile.Tile;
import me.kalmemarq.common.tile.Tiles;
import org.lwjgl.glfw.GLFW;

public class PlayerEntity extends MobEntity {
    public int dir;
    public int color;
	public int health = 5;
	public int maxHealth = 10;
	public int stamina = 8;
	public int maxStamina = 10;
	public int hunger = 2;
	public int maxHunger = 10;
	
	public void tickClient(Client client) {
		this.prevX = this.x;
		this.prevY = this.y;
		
		if (this.level == null) return;

		float xa = 0;
		float ya = 0;

		if (GLFW.glfwGetKey(client.window.getHandle(), GLFW.GLFW_KEY_W) != GLFW.GLFW_RELEASE
		|| GLFW.glfwGetKey(client.window.getHandle(), GLFW.GLFW_KEY_UP) != GLFW.GLFW_RELEASE) {
			ya -= 1;
		}

		if (GLFW.glfwGetKey(client.window.getHandle(), GLFW.GLFW_KEY_S) != GLFW.GLFW_RELEASE
		|| GLFW.glfwGetKey(client.window.getHandle(), GLFW.GLFW_KEY_DOWN) != GLFW.GLFW_RELEASE) {
			ya += 1;
		}

		if (GLFW.glfwGetKey(client.window.getHandle(), GLFW.GLFW_KEY_D) != GLFW.GLFW_RELEASE
		|| GLFW.glfwGetKey(client.window.getHandle(), GLFW.GLFW_KEY_RIGHT) != GLFW.GLFW_RELEASE) {
			xa += 1;
		}

		if (GLFW.glfwGetKey(client.window.getHandle(), GLFW.GLFW_KEY_A) != GLFW.GLFW_RELEASE
		|| GLFW.glfwGetKey(client.window.getHandle(), GLFW.GLFW_KEY_LEFT) != GLFW.GLFW_RELEASE) {
			xa -= 1;
		}

		if (xa < 0) this.dir = 2;
		if (xa > 0) this.dir = 3;
		if (ya < 0) this.dir = 1;
		if (ya > 0) this.dir = 0;

		int xx = (int) ((this.x + (xa > 0 ? 6 : -6) + xa) / 16);
		int yy = (int) ((this.y + (ya > 0 ? 6 : -6) + ya) / 16);

		if (this.level == null) return;
		int tilea = this.level.getTileId(xx, yy);
		Tile tile = Tiles.REGISTRY.getValueByRawId(tilea);

		if (tile != null && tile.isSolid()) {
			xa = 0.0f;
			ya = 0.0f;
		}

		this.x += xa * 1f;
		this.y += ya * 1f;
		
		if (this.x - 6 < 0) {
			this.x = 6;
		}
		
		if (this.x + 6 > this.level.width * 16) {
			this.x = this.level.width * 16 - 6;
		}

		if (this.y - 8 < 0) {
			this.y = 8;
		}

		if (this.y + 8 > this.level.height * 16) {
			this.y = this.level.height * 16 - 8;
		}
	}

	@Override
	public void tick() {
	}
}
