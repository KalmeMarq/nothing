package me.kalmemarq.common.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

public class PacketFrameDecoder extends ByteToMessageDecoder  {
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		in.markReaderIndex();

		byte[] bs = new byte[3];

		for (int i = 0; i < bs.length; ++i) {
			if (!in.isReadable()) {
				in.resetReaderIndex();
				return;
			}
			bs[i] = in.readByte();
			if (bs[i] < 0) continue;
			PacketByteBuf lv = new PacketByteBuf(Unpooled.wrappedBuffer(bs));
			try {
				int j = lv.readVarInt();
				if (in.readableBytes() < j) {
					in.resetReaderIndex();
					return;
				}
				out.add(in.readBytes(j));
				return;
			}
			finally {
				lv.release();
			}
		}
		throw new CorruptedFrameException("length wider than 21-bit");
	}
}
