package me.kalmemarq.network.packet;

public class ChunkDataPacket extends Packet {
	private int x;
	private int y;
	private byte[] tiles;

	public ChunkDataPacket() {
	}

	public ChunkDataPacket(int x, int y, byte[] tiles) {
		this.x = x;
		this.y = y;
		this.tiles = tiles;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt(this.x);
		buffer.writeInt(this.y);
		buffer.writeVarInt(this.tiles.length);
		buffer.writeBytes(this.tiles);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		int length = buffer.readVarInt();
		this.tiles = new byte[length];
		buffer.readBytes(this.tiles);
	}

	@Override
	public void apply(PacketListener listener) {
		listener.onChunkData(this);
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public byte[] getTiles() {
		return this.tiles;
	}
}
