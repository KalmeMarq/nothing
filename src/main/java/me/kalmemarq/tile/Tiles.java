package me.kalmemarq.tile;

import me.kalmemarq.Identifier;
import me.kalmemarq.Registry;

public class Tiles {
	public static final Registry<Tile> REGISTRY = new Registry<>();
	
	public static void init() {
		REGISTRY.set(1, Identifier.of("grass"), new GrassTile());
		REGISTRY.set(Identifier.of("rock"), new RockTile());
		REGISTRY.set(Identifier.of("water"), new WaterTile());
	}
	
	static {
		init();
	}
}
