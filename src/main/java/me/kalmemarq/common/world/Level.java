package me.kalmemarq.common.world;

import java.util.Random;

public class Level {
	protected static final Random RANDOM = new Random();
	
	public int width;
	public int height;
	public int xChunks;
	public int yChunks;
	public final Chunk[] chunks;
	
	public Level(int width, int height) {
		this.width = width;
		this.height = height;
		this.xChunks = this.width / 8;
		this.yChunks = this.height / 8;
		this.chunks = new Chunk[this.xChunks * this.yChunks];
	}
	
	public void generate() {
		for (int y = 0; y < this.yChunks; ++y) {
			for (int x = 0; x < this.xChunks; ++x) {
				Chunk chunk = new Chunk(x, y);
				chunk.generate();
				this.chunks[y * this.xChunks + x] = chunk;
			}
		}
	}
	
	public void load(int x, int y, byte[] tiles) {
		this.chunks[y * this.xChunks + x] = new Chunk(x, y, tiles); 
	}
	
	public int getTileId(int x, int y) {
		int xc = x / 8;
		int yc = y / 8;
		if (xc < 0 || yc < 0) return 0;
		if (xc >= this.width || yc >= this.height) return 0;
		return this.chunks[yc * this.xChunks + xc].getTileId(x, y);
	}

}
