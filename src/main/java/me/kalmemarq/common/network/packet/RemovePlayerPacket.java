package me.kalmemarq.common.network.packet;

public class RemovePlayerPacket extends Packet {
    private String username;

    public RemovePlayerPacket() {
    }

    public RemovePlayerPacket(String username) {
        this.username = username;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeString(this.username);
    }

    @Override
    public void read(PacketByteBuf buffer) {
        this.username = buffer.readString();
    }

	@Override
	public void apply(PacketListener listener) {
		listener.onRemovePlayer(this);
	}

	public String getUsername() {
        return this.username;
    }
}
