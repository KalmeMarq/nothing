package me.kalmemarq.common.world;

public class Chunk {
	public int x;
	public int y;
	public byte[] tiles;

	public Chunk(int x, int y) {
		this.x = x;
		this.y = y;
		this.tiles = new byte[8 * 8];
	}

	public Chunk(int x, int y, byte[] tiles) {
		this.x = x;
		this.y = y;
		this.tiles = tiles;
	}

	public int getTileId(int x, int y) {
		return this.tiles[(y % 8) * 8 + x % 8];
	}

	public void generate() {
		for (int y = 0; y < 8; ++y) {
			for (int x = 0; x < 8; ++x) {
				this.tiles[y * 8 + x] = (byte) (Level.RANDOM.nextInt(10) <= 1 ? 2 : Level.RANDOM.nextInt(10) <= 1 ? 3 : 1);
			}
		}
	}
}
