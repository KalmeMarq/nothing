package me.kalmemarq.network.packet;

public class WorldDataPacket extends Packet {
    private byte[] tiles;

    public WorldDataPacket() {
    }

    public WorldDataPacket(byte[] tiles) {
        this.tiles = tiles;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeVarInt(this.tiles.length);
        buffer.writeBytes(this.tiles);
    }

    @Override
    public void read(PacketByteBuf buffer) {
        int length = buffer.readVarInt();
        this.tiles = new byte[length];
        buffer.readBytes(this.tiles);
    }

    public byte[] getTiles() {
        return this.tiles;
    }
}
