package me.kalmemarq.common.network.packet;

public class LevelInfoPacket extends Packet {
	private int width;
	private int height;
	
	public LevelInfoPacket() {
	}

	public LevelInfoPacket(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeVarInt(this.width);
		buffer.writeVarInt(this.height);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.width = buffer.readVarInt();
		this.height = buffer.readVarInt();
	}

	@Override
	public void apply(PacketListener listener) {
		listener.onLevelInfo(this);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
}
