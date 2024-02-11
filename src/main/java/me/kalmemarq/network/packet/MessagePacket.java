package me.kalmemarq.network.packet;

import java.time.Instant;

public class MessagePacket extends Packet {
    private String message;
    private Instant timestamp;

    public MessagePacket() {
    }

    public MessagePacket(String message, Instant timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeString(this.message);
        buffer.writeLong(this.timestamp.toEpochMilli());
    }

    @Override
    public void read(PacketByteBuf buffer) {
        this.message = buffer.readString();
        this.timestamp = Instant.ofEpochMilli(buffer.readLong());
    }

    @Override
    public void apply(PacketListener listener) {
        listener.onMessage(this);
    }

    public String getMessage() {
        return this.message;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }
}
