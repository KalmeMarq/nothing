package me.kalmemarq.common.network.packet;

public class TextParticlePacket extends Packet {
	private String text;
	private int x;
	private int y;
	private int lifetime;
	private double xa;
	private double ya;
	private double za;
	private int color;
	
	public TextParticlePacket() {
	}
	
	public TextParticlePacket(String text, int x, int y, int lifetime, double xa, double ya, double za, int color) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.lifetime = lifetime;
		this.xa = xa;
		this.ya = ya;
		this.za= za;
		this.color = color;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeString(this.text);
		buffer.writeInt(this.x);
		buffer.writeInt(this.y);
		buffer.writeInt(this.lifetime);
		buffer.writeDouble(this.xa);
		buffer.writeDouble(this.ya);
		buffer.writeDouble(this.za);
		buffer.writeInt(this.color);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.text = buffer.readString();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.lifetime = buffer.readInt();
		this.xa = buffer.readDouble();
		this.ya = buffer.readDouble();
		this.za = buffer.readDouble();
		this.color = buffer.readInt();
	}

	@Override
	public void apply(PacketListener listener) {
		listener.onTextParticle(this);
	}

	public String getText() {
		return this.text;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getLifetime() {
		return this.lifetime;
	}

	public double getXA() {
		return this.xa;
	}

	public double getYA() {
		return this.ya;
	}

	public double getZA() {
		return this.za;
	}

	public int getColor() {
		return this.color;
	}
}
