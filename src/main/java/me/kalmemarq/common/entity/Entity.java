package me.kalmemarq.common.entity;

import me.kalmemarq.common.world.Level;

public class Entity {
	public Level level;
	public float x;
	public float y;
	public float prevX;
	public float prevY;

	public void setLevel(Level level) {
		this.level = level;
	}

	public void tick() {
	}
}
