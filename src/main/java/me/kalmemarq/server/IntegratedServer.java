package me.kalmemarq.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.kalmemarq.common.world.Level;
import me.kalmemarq.common.network.NetworkConnection;
import me.kalmemarq.common.network.NetworkSide;

import java.net.InetAddress;
import java.net.SocketAddress;

public class IntegratedServer extends Server {
	public void openToLan(int port) {
		ServerBootstrap bootstrap = new ServerBootstrap()
			.group(this.eventLoopGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					NetworkConnection connection = new NetworkConnection(NetworkSide.SERVER, IntegratedServer.this);
					ServerNetworkHandler handler = new ServerNetworkHandler(IntegratedServer.this, connection);
					connection.setListener(handler);
					IntegratedServer.this.connections.add(connection);
					IntegratedServer.this.connectionHandlers.put(connection, handler);
					IntegratedServer.this.printMessage("New connection at " + ch.remoteAddress());
					NetworkConnection.addCommonHandlers(ch.pipeline(), connection);
				}
			});

		var a = bootstrap.bind((InetAddress) null, port);
		this.bootstraps.add(a);
		a.syncUninterruptibly();
	}

	public SocketAddress startLocal() {
		this.isIntegrated = true;
		this.eventLoopGroup = new NioEventLoopGroup(0, (runnable) -> {
			Thread thread = new Thread(runnable);
			thread.setName("Netty Integrated Server IO");
			thread.setDaemon(true);
			return thread;
		});
		this.running.set(true);
		ServerBootstrap bootstrap = new ServerBootstrap()
			.group(this.eventLoopGroup)
			.channel(LocalServerChannel.class)
			.childHandler(new ChannelInitializer<>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					try {
						ch.config().setOption(ChannelOption.TCP_NODELAY, true);
					} catch (ChannelException ignored) {
					}
					NetworkConnection connection = new NetworkConnection(NetworkSide.SERVER, IntegratedServer.this);
					ServerNetworkHandler handler = new ServerNetworkHandler(IntegratedServer.this, connection);
					connection.setListener(handler);
					IntegratedServer.this.connections.add(connection);
					IntegratedServer.this.connectionHandlers.put(connection, handler);
					System.out.println("New connection at " + ch.remoteAddress());
					ch.pipeline().addLast("packet_handler", connection);
				}
			});

		this.level = new Level(8 * 12, 8 * 12);
		this.level.generate();

		try {
			var cf = bootstrap.localAddress(LocalAddress.ANY).bind().sync();
			this.bootstraps.add(cf);
			return cf.channel().localAddress();
		} catch (Exception e) {
			System.out.println("SERVER THREAD: " + e);
			return null;
		}
	}
}
