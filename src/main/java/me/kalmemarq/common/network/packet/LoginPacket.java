package me.kalmemarq.common.network.packet;

public class LoginPacket extends Packet {
	private int protocolVersion;
    private String username;
    private int color;

    public LoginPacket() {
    }

    public LoginPacket(int protocolVersion, String username, int color) {
		this.protocolVersion = protocolVersion;
        this.username = username;
        this.color = color;
    }

    @Override
    public void write(PacketByteBuf buffer) {
		buffer.writeInt(this.protocolVersion);
        buffer.writeString(this.username);
        buffer.writeInt(this.color);
    }

    @Override
    public void read(PacketByteBuf buffer) {
		this.protocolVersion = buffer.readInt();
        this.username = buffer.readString();
        this.color = buffer.readInt();
    }

	@Override
	public void apply(PacketListener listener) {
		listener.onLogin(this);
	}

	public int getProtocolVersion() {
		return this.protocolVersion;
	}

	public String getUsername() {
        return this.username;
    }

    public int getColor() {
        return this.color;
    }
}
