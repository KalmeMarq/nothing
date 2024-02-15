package me.kalmemarq.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int i = in.readableBytes();
		if (i == 0) {
			return;
		}
		
        var wrapper = new PacketByteBuf(in);
        int id = wrapper.readVarInt();
        var clazz = Packet.ID_TO_CLASS.get(id);

        if (clazz == null) {
            throw new IllegalStateException("Invalid packet id " + id);
        }

        var packet = clazz.getConstructor().newInstance();
        packet.read(wrapper);
        out.add(packet);
    }
}
