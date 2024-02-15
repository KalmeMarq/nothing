package me.kalmemarq.common.network.packet;

public class DisconnectPacket extends Packet {
    private String reason;

    public DisconnectPacket() {
    }

    public DisconnectPacket(String reason) {
        this.reason = reason;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeString(this.reason);
    }

    @Override
    public void read(PacketByteBuf buffer) {
        this.reason = buffer.readString();
    }

	@Override
	public void apply(PacketListener listener) {
		listener.onDisconnect(this);
	}

	public String getReason() {
        return this.reason;
    }
}
