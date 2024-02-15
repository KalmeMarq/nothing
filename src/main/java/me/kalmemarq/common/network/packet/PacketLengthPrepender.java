package me.kalmemarq.common.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketLengthPrepender extends MessageToByteEncoder<ByteBuf> {
	/**
	 * @see io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender
	 */
	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		int bodyLen = msg.readableBytes();
		int headerLen = PacketByteBuf.getVarIntLength(bodyLen);
		if (headerLen > 3) {
			throw new IllegalArgumentException("Unable to fit " + bodyLen + " into 3");
		}
		
		PacketByteBuf wrapper = new PacketByteBuf(out);
		wrapper.ensureWritable(headerLen + bodyLen);
		wrapper.writeInt(bodyLen);
		wrapper.writeBytes(msg, msg.readerIndex(), msg.readableBytes());
	}
}
