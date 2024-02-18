package me.kalmemarq.common.tile;

import me.kalmemarq.common.entity.PlayerEntity;
import me.kalmemarq.common.world.Level;
import org.lwjgl.opengl.GL11;

public class Tile {
	public Tile() {
	}
	
	public boolean isSolid() {
		return false;
	}
	
	public boolean interact(Level level, int xt, int yt, PlayerEntity player) {
		return false;
	}
	
	public void render(int x, int y) {
		int tu = 24;
		int tv = 48;
		float tu0 = tu / 256.0f;
		float tv0 = tv / 256.0f;
		float tu1 = (tu + 16) / 256.0f;
		float tv1 = (tv + 16) / 256.0f;
		
		GL11.glTexCoord2f(tu0, tv0);
		GL11.glVertex3f(x * 16, y * 16, 0);
		GL11.glTexCoord2f(tu0, tv1);
		GL11.glVertex3f(x * 16, y * 16 + 16, 0);
		GL11.glTexCoord2f(tu1, tv1);
		GL11.glVertex3f(x * 16 + 16, y * 16 + 16, 0);
		GL11.glTexCoord2f(tu1, tv0);
		GL11.glVertex3f(x * 16 + 16, y * 16, 0);
	}
}
