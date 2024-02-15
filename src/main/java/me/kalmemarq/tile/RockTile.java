package me.kalmemarq.tile;

import org.lwjgl.opengl.GL11;

public class RockTile extends Tile {
	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public void render(int x, int y) {
		int tu = 168;
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
