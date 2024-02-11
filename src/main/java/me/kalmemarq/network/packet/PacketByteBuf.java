package me.kalmemarq.network.packet;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PacketByteBuf {
    private ByteBuf parent;

    public PacketByteBuf(ByteBuf buffer) {
        this.parent = buffer;
    }

    public void writeBytes(byte[] src) {
        this.parent.writeBytes(src);
    }

    public void readBytes(byte[] dst) {
        this.parent.readBytes(dst);
    }

    public void writeByte(int value) {
        this.parent.writeByte(value);
    }

    public byte readByte() {
        return this.parent.readByte();
    }

    public void writeShort(int value) {
        this.parent.writeShort(value);
    }

    public short readShort() {
        return this.parent.readShort();
    }

    public void writeInt(int value) {
        this.parent.writeInt(value);
    }

    public int readInt() {
        return this.parent.readInt();
    }

    public void writeLong(long value) {
        this.parent.writeLong(value);
    }

    public long readLong() {
        return this.parent.readLong();
    }

    public void writeFloat(float value) {
        this.parent.writeFloat(value);
    }

    public float readFloat() {
        return this.parent.readFloat();
    }

    public void writeDouble(int value) {
        this.parent.writeDouble(value);
    }

    public double readDouble() {
        return this.parent.readDouble();
    }

    public void writeString(String value) {
        this.writeString(value, StandardCharsets.UTF_8);
    }

    public void writeString(String value, Charset charset) {
        byte[] data = value.getBytes(charset);
        this.writeVarInt(data.length);
        this.parent.writeBytes(data);
    }

    public String readString() {
        return this.readString(StandardCharsets.UTF_8);
    }

    public String readString(Charset charset) {
        int len = this.readVarInt();
        String value = this.parent.toString(this.parent.readerIndex(), len, charset);
        this.parent.readerIndex(this.parent.readerIndex() + len);
        return value;
    }

    public void writeVarInt(int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                this.writeByte(value);
                return;
            }
            this.writeByte((value & 0x7F) | 0x7F);
            value >>>= 7;
        }
    }

    public int readVarInt() {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = readByte();
            value |= (currentByte & 0x7F) << position;

            if ((currentByte & 0x80) == 0) break;

            position += 7;

            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }

        return value;
    }
}