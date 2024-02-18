package me.kalmemarq.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import me.kalmemarq.common.ChatMessage;
import me.kalmemarq.common.entity.TextParticle;
import me.kalmemarq.common.world.Level;
import me.kalmemarq.common.ThreadExecutor;
import me.kalmemarq.common.network.NetworkConnection;
import me.kalmemarq.common.network.NetworkSide;
import me.kalmemarq.common.network.packet.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server extends ThreadExecutor {
	public static final int PROTOCOL_VERSION = 1;

    public final Deque<NetworkConnection> connections = new ConcurrentLinkedDeque<>();
    public final Map<NetworkConnection, ServerNetworkHandler> connectionHandlers = new ConcurrentHashMap<>();
    protected final AtomicBoolean running = new AtomicBoolean(false);
    public final List<ChatMessage> messages = new ArrayList<>();
    protected EventLoopGroup eventLoopGroup;
    protected final List<ChannelFuture> bootstraps = new ArrayList<>();
	public PlayerManager playerManager = new PlayerManager();
    protected boolean isIntegrated;
	public Level level;
	public Thread thread;
	
    public Server() {
		this.thread = Thread.currentThread();
    }

	@Override
	public Thread getThread() {
		return this.thread;
	}

    protected void onSend(String msg) {
		if (msg.startsWith("/spawnparticle")) {
			for (NetworkConnection connection : this.playerManager.connectionMap.values()) {
				TextParticle textParticle = new TextParticle(20, 20, 60, "Hey Buddy", 0xFF0000);
				connection.sendPacket(new TextParticlePacket("Hey Buddy", 20, 20, textParticle.lifetime, textParticle.xa, textParticle.ya, textParticle.za, textParticle.color));
			}
		} else if (msg.startsWith("/kick ")) {
            if (msg.length() > "/kick ".length()) {
                var username = msg.substring(6);

                if ("all".equals(username)) {
                    for (Map.Entry<String, NetworkConnection> user : this.playerManager.connectionMap.entrySet()) {
                        user.getValue().sendPacket(new DisconnectPacket("Kicked out"));
                        user.getValue().disconnect();
                    }
                } else if (this.playerManager.connectionMap.containsKey(username)) {
                    this.playerManager.connectionMap.get(username).sendPacket(new DisconnectPacket("Kicked out"));
                    this.playerManager.connectionMap.get(username).disconnect();
                } else {
					this.printMessage("[Server] There's no player with that username");
                }
            } else {
                this.printMessage("[Server] Invalid command");
            }
        } else if (msg.equals("/stop")) {
            close();
		} else if (msg.equals("/listplayers")) {
			this.printMessage("Players: " + String.join(", ", this.playerManager.connectionMap.keySet()));
        } else if (msg.equals("/checkconns")) {
            Iterator<NetworkConnection> iter = this.connections.iterator();

            while (iter.hasNext()) {
                NetworkConnection connection = iter.next();
                if (!connection.isConnected()) {
					iter.remove();
                    this.connectionHandlers.remove(connection);
                    System.out.println(connection.getAddress() + " has disconnected");
                }
            }
        } else {
            this.messages.add(new ChatMessage("[Server] " + msg, Instant.now()));
            this.printMessage("[Server] " + msg);
            for (NetworkConnection connection1 : this.connections) {
                connection1.sendPacket(new MessagePacket("[Server] " + msg, Instant.now()));
            }
        }
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void startAt(String ip, int port, boolean showGui, boolean useStdIn) {
        this.eventLoopGroup = new NioEventLoopGroup(0, (runnable) -> {
			Thread thread = new Thread(runnable);
			thread.setName("Netty Server IO");
			thread.setDaemon(true);
			return thread;
		});
		this.running.set(true);
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(this.eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        NetworkConnection connection = new NetworkConnection(NetworkSide.SERVER, Server.this);
						ServerNetworkHandler handler = new ServerNetworkHandler(Server.this, connection);
						connection.setListener(handler);
                        Server.this.connections.add(connection);
						Server.this.connectionHandlers.put(connection, handler);
                      	Server.this.printMessage("New connection at " + ch.remoteAddress());
						NetworkConnection.addCommonHandlers(ch.pipeline(), connection);
					}
                });

		this.level = new Level(8 * 4, 8 * 4);
		this.level.generate();

        try {
            var cf = bootstrap.bind(ip, port).syncUninterruptibly();
            this.bootstraps.add(cf);
			this.printMessage("Server at " + ip + ":" + port);
			
			run();
		} catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.eventLoopGroup.shutdownGracefully();
        }
    }
	
	public void run() {
		while (this.running.get()) {
			long lastTime = System.currentTimeMillis();
            this.tick();
			this.runTasks(() -> System.currentTimeMillis() - lastTime < 16);
		}
	}
	
	public void tick() {
		this.checkConnections();
		
		for (ServerNetworkHandler handler : this.connectionHandlers.values()) {
			handler.tick();
		}
	}

    private void disconnectAll() {
        for (NetworkConnection connection : this.connections) {
            connection.sendPacket(new DisconnectPacket("Server is over bruv"));
            connection.disconnect();
        }
    }

    public void close() {
        System.out.println("[SERVER] closing");
        this.running.set(false);
        this.disconnectAll();
		
        for (ChannelFuture a : this.bootstraps) {
            try {
                a.channel().close().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (this.eventLoopGroup != null) this.eventLoopGroup.shutdownGracefully();
        System.out.println("[SERVER] closed");
    }
	
	public void checkConnections() {
		Iterator<NetworkConnection> iter = this.connections.iterator();

		while (iter.hasNext()) {
			NetworkConnection connection = iter.next();

			if (!connection.isConnected()) {
				iter.remove();
				this.connectionHandlers.remove(connection);
				
				if (this.playerManager.connectionMapN.containsKey(connection)) {
					var value = this.playerManager.connectionMapN.get(connection);

					for (NetworkConnection cc : this.playerManager.connectionMapN.keySet()) {
						if (cc == connection) continue;
						cc.sendPacket(new RemovePlayerPacket(value));
					}

					this.playerManager.removePlayer(value);
				}
				System.out.println("[Server] " + connection.getAddress() + " has disconnected");
			}
		}
	}
}
