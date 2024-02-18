package me.kalmemarq.common.entity;

import me.kalmemarq.common.network.packet.TextParticlePacket;

import java.util.Random;

public class TextParticle extends Entity {
	protected final Random random = new Random();
	public int lifetime;
	public String text;
	public int time;
	public int color;

	public double xa, ya, za; // x, y, z acceleration
	public double xx, yy, zz; // x, y, z coordinates
	
	public TextParticle(int x, int y, int lifetime, String text, int color) {
		this.x = x;
		this.y = y;
		this.xx = x;
		this.yy = y;
		this.zz = 2;
		this.lifetime = lifetime;
		this.text = text;
		this.xa = this.random.nextGaussian() * 0.3;
		this.ya = this.random.nextGaussian() * 0.2;
		this.za = this.random.nextFloat() * 0.7 + 2;
		this.time = 0;
		this.color = color;
	}
	
	public TextParticle(TextParticlePacket packet) {
		this.x = packet.getX();
		this.y = packet.getY();
		this.xx = this.x;
		this.yy = this.y;
		this.zz = 2;
		this.lifetime = packet.getLifetime();
		this.text = packet.getText();
		this.xa = packet.getXA();
		this.ya = packet.getYA();
		this.za = packet.getZA();
		this.time = 0;
		this.color = packet.getColor();
	}

	@Override
	public void tick() {
		this.time++;
		if (this.time > this.lifetime) {
			this.remove();
		}

		xx += xa;
		yy += ya;
		zz += za;
		if (zz < 0) {

			// If z pos if less than 0, alter accelerations...
			zz = 0;
			za *= -0.5;
			xa *= 0.6;
			ya *= 0.6;
		}
		za -= 0.15;  // za decreases by 0.15 every tick.
		// Truncate x and y coordinates to integers:
		x = (int) xx;
		y = (int) yy;
	}
}
