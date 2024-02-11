package me.kalmemarq.network.packet;

public class LoginPacket extends Packet {
    private String username;
    private int color;

    public LoginPacket() {
    }

    public LoginPacket(String username, int color) {
        this.username = username;
        this.color = color;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeString(this.username);
        buffer.writeInt(this.color);
    }

    @Override
    public void read(PacketByteBuf buffer) {
        this.username = buffer.readString();
        this.color = buffer.readInt();
    }

    public String getUsername() {
        return this.username;
    }

    public int getColor() {
        return this.color;
    }
}
