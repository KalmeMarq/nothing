package me.kalmemarq.common.network.packet;

public class TileUpdatePacket extends Packet {
	private int tileX;
	private int tileY;
	private byte id;
	private byte data;
	
	public TileUpdatePacket() {
	}

	public TileUpdatePacket(int tileX, int tileY, int id, int data) {
		this.tileX = tileX;
		this.tileY = tileY;
		this.id = (byte) id;
		this.data = (byte) data;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt(this.tileX);
		buffer.writeInt(this.tileY);
		buffer.writeByte(this.id);
		buffer.writeByte(this.data);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.tileX = buffer.readInt();
		this.tileY = buffer.readInt();
		this.id = buffer.readByte();
		this.data = buffer.readByte();
	}

	@Override
	public void apply(PacketListener listener) {
		listener.onTileUpdate(this);
	}

	public int getTileX() {
		return this.tileX;
	}

	public int getTileY() {
		return this.tileY;
	}

	public byte getId() {
		return this.id;
	}

	public byte getData() {
		return this.data;
	}
}
