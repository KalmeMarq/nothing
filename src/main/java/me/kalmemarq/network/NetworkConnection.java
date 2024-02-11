package me.kalmemarq.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import me.kalmemarq.network.packet.Packet;

import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NetworkConnection extends SimpleChannelInboundHandler<Packet> {
    BiConsumer<NetworkConnection, Packet> listener;
    NetworkSide side;
    private Channel channel;
    private SocketAddress address;
    private final Queue<Consumer<NetworkConnection>> queue = new ConcurrentLinkedDeque<>();

    public NetworkConnection(NetworkSide side, BiConsumer<NetworkConnection, Packet> listener) {
        this.side = side;
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
            this.send(new Packet[]{ packet });
        } else {
            this.queue.add((conn) -> conn.send(new Packet[]{ packet }));
        }
    }

    public void sendPackets(Packet[] packets) {
        if (this.isOpen()) {
            this.send(packets);
        } else {
//            System.out.println("[CONN] Queued " + packets.length + " packets");
            this.queue.add((conn) -> conn.send(packets));
        }
    }

    private void send(Packet[] packets) {
        for (var packet : packets) {
            this.channel.pipeline().write(packet);
        }
        this.channel.pipeline().flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        this.listener.accept(this, msg);
//        System.out.println("[SIDE:" + this.side.name() + "] [PACKET] " + msg.getClass().getSimpleName());
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

    public boolean isConnected() {
        return this.channel.isOpen();
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
}
