package me.kalmemarq.common.network.packet;

public class RequestPreviousMessagesPacket extends Packet {
    public RequestPreviousMessagesPacket() {
    }

    @Override
    public void write(PacketByteBuf buffer) {
    }

    @Override
    public void read(PacketByteBuf buffer) {
    }
	
    @Override
    public void apply(PacketListener listener) {
        listener.onRequestPreviousMessages(this);
    }
}
