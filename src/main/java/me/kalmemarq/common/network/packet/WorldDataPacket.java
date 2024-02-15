package me.kalmemarq.common.network.packet;

public class WorldDataPacket extends Packet {
	private int width;
	private int height;
    private byte[] tiles;

    public WorldDataPacket() {
    }

    public WorldDataPacket(int width, int height, byte[] tiles) {
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeInt(this.width);
        buffer.writeInt(this.height);
		buffer.writeVarInt(this.tiles.length);
        buffer.writeBytes(this.tiles);
    }

    @Override
    public void read(PacketByteBuf buffer) {
        this.width = buffer.readInt();
        this.height = buffer.readInt();
		int length = buffer.readVarInt();
        this.tiles = new byte[length];
        buffer.readBytes(this.tiles);
    }

	@Override
	public void apply(PacketListener listener) {
		listener.onWorldData(this);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public byte[] getTiles() {
        return this.tiles;
    }
}
