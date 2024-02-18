package me.kalmemarq.common.network.packet;

public class AttackPacket extends Packet {
	private int tileX;
	private int tileY;
	private int damage;
	
	public AttackPacket() {
	}
	
	public AttackPacket(int tileX, int tileY, int damage) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.damage = damage;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt(this.tileX);
		buffer.writeInt(this.tileY);
		buffer.writeInt(this.damage);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.tileX = buffer.readInt();
		this.tileY = buffer.readInt();
		this.damage = buffer.readInt();
	}

	@Override
	public void apply(PacketListener listener) {
		listener.onAttack(this);
	}

	public int getTileX() {
		return this.tileX;
	}

	public int getTileY() {
		return this.tileY;
	}

	public int getDamage() {
		return this.damage;
	}
}
