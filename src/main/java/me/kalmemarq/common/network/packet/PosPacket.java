package me.kalmemarq.common.network.packet;

public class PosPacket extends Packet {
    private String username;
    private float x;
    private float y;
    private int color;
    private int dir;

    public PosPacket() {
    }

    public PosPacket(String username, float x, float y, int color, int dir) {
        this.username = username;
        this.x = x;
        this.y = y;
        this.color = color;
        this.dir = dir;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeString(this.username);
        buffer.writeFloat(this.x);
        buffer.writeFloat(this.y);
        buffer.writeInt(this.color);
        buffer.writeInt(this.dir);
    }

    @Override
    public void read(PacketByteBuf buffer) {
        this.username = buffer.readString();
        this.x = buffer.readFloat();
        this.y = buffer.readFloat();
        this.color = buffer.readInt();
        this.dir = buffer.readInt();
    }

	@Override
	public void apply(PacketListener listener) {
		listener.onPos(this);
	}

	public String getUsername() {
        return this.username;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int getColor() {
        return this.color;
    }

    public int getDir() {
        return this.dir;
    }
}
