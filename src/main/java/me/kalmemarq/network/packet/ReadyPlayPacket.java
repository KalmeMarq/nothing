package me.kalmemarq.network.packet;

public class ReadyPlayPacket extends Packet {
	@Override
	public void write(PacketByteBuf buffer) {
	}

	@Override
	public void read(PacketByteBuf buffer) {
	}

	@Override
	public void apply(PacketListener listener) {
		listener.onReadyPlay(this);
	}
}
