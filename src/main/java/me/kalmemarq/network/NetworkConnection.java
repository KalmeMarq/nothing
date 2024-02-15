package me.kalmemarq.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import me.kalmemarq.ThreadExecutor;
import me.kalmemarq.network.packet.Packet;
import me.kalmemarq.network.packet.PacketDecoder;
import me.kalmemarq.network.packet.PacketEncoder;
import me.kalmemarq.network.packet.PacketFrameDecoder;
import me.kalmemarq.network.packet.PacketLengthPrepender;
import me.kalmemarq.network.packet.PacketListener;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

public class NetworkConnection extends SimpleChannelInboundHandler<Packet> {
    public NetworkSide side;
    public Channel channel;
    public SocketAddress address;
    public final Queue<Consumer<NetworkConnection>> queue = new ConcurrentLinkedDeque<>();
	public PacketListener listener;
	public ThreadExecutor executor;
	
    public NetworkConnection(NetworkSide side, ThreadExecutor executor) {
        this.side = side;
		this.executor = executor;
    }

	public void setListener(PacketListener listener) {
		this.listener = listener;
	}

	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channel = ctx.channel();
        this.address = this.channel.remoteAddress();
    }

    public void sendPacket(Packet packet) {
        if (this.isOpen()) {
            this.send(packet);
        } else {
            this.queue.add((conn) -> conn.send(packet));
        }
    }

    public void sendPackets(Packet[] packets) {
        if (this.isOpen()) {
			for (Packet packet : packets) {
				this.send(packet);
			}
		} else {
			for (Packet packet : packets) {
	            this.queue.add((conn) -> conn.send(packet));
			}
        }
    }

    private void send(Packet packet) {
		this.channel.pipeline().writeAndFlush(packet);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
		if (this.listener != null) {
			this.executor.execute(() -> msg.apply(this.listener));
			
		}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println(cause.getMessage());
    }

    public SocketAddress getAddress() {
        return this.address;
    }

    public boolean isOpen() {
        return this.channel != null && this.channel.isOpen();
    }

	public boolean isLocal() {
		return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
	}

    public boolean isConnected() {
        return this.channel == null || this.channel.isOpen();
    }

    public void disconnect() {
        this.channel.close().syncUninterruptibly();
    }

    public void tick() {
        if (this.isOpen()) {
            synchronized (this.queue) {
                Consumer<NetworkConnection> consumer;
                while ((consumer = this.queue.poll()) != null) {
                    consumer.accept(this);
                }
            }
        }
    }
	
	public static void addCommonHandlers(ChannelPipeline channelPipeline, NetworkConnection connection) {
		channelPipeline
			.addLast("frame_decoder", new PacketFrameDecoder())
			.addLast("decoder", new PacketDecoder())
			.addLast("prepender", new PacketLengthPrepender())
			.addLast("encoder", new PacketEncoder())
			.addLast("handler", connection);
	}
}
