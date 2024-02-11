package me.kalmemarq.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.kalmemarq.ChatMessage;
import me.kalmemarq.network.NetworkConnection;
import me.kalmemarq.network.NetworkSide;
import me.kalmemarq.Player;
import me.kalmemarq.network.packet.DisconnectPacket;
import me.kalmemarq.network.packet.LoginPacket;
import me.kalmemarq.network.packet.MessagePacket;
import me.kalmemarq.network.packet.Packet;
import me.kalmemarq.network.packet.PacketDecoder;
import me.kalmemarq.network.packet.PacketEncoder;
import me.kalmemarq.network.packet.PlayPacket;
import me.kalmemarq.network.packet.PosPacket;
import me.kalmemarq.network.packet.RemovePlayerPacket;
import me.kalmemarq.network.packet.RequestPreviousMessagesPacket;
import me.kalmemarq.network.packet.WorldDataPacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private final Deque<NetworkConnection> connections = new ConcurrentLinkedDeque<>();
    private final Map<String, NetworkConnection> connectionMap = new ConcurrentHashMap<>();
    private final Map<NetworkConnection, String> connectionMapN = new ConcurrentHashMap<>();

    private ServerConsoleGui gui;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final List<ChatMessage> messages = new ArrayList<>();
    private EventLoopGroup eventLoopGroup;
    private final List<ChannelFuture> bootstraps = new ArrayList<>();
    private Map<String, Player> playerMap = new ConcurrentHashMap<>();
    private boolean isIntegrated;

    public Server() {
    }

    private void onSend(String msg) {
        if (msg.startsWith("/kick ")) {
            if (msg.length() > "/kick ".length()) {
                var username = msg.substring(6);

                if ("all".equals(username)) {
                    for (Map.Entry<String, NetworkConnection> user : this.connectionMap.entrySet()) {
                        user.getValue().sendPacket(new DisconnectPacket("Kicked out"));
                        user.getValue().disconnect();
                    }
                } else if (this.connectionMap.containsKey(username)) {
                    this.connectionMap.get(username).sendPacket(new DisconnectPacket("Kicked out"));
                    this.connectionMap.get(username).disconnect();
                } else {
                    if (this.gui != null) this.gui.textArea.append("[Server] There's no player with that username \n");
                    System.out.println("[Server] There's no player with that username");
                }
            } else {
                if (this.gui != null) this.gui.textArea.append("[Server] Invalid command \n");
                System.out.println("[Server] Invalid command");
            }
        } else if (msg.equals("/stop")) {
            close();
        } else if (msg.equals("/checkconns")) {
            Iterator<NetworkConnection> iter = this.connections.iterator();

            while (iter.hasNext()) {
                NetworkConnection connection = iter.next();
                if (!connection.isConnected()) {
                    iter.remove();
                    System.out.println(connection.getAddress() + " has disconnected");
                }
            }
        } else {
            this.messages.add(new ChatMessage("[Server] " + msg, Instant.now()));
            this.gui.textArea.append("[Server] " + msg + "\n");
            System.out.println("[Server] " + msg);
            for (NetworkConnection connection1 : this.connections) {
                connection1.sendPacket(new MessagePacket("[Server] " + msg, Instant.now()));
            }
        }
    }

    public SocketAddress startLocal() {
        this.isIntegrated = true;
        this.eventLoopGroup = new NioEventLoopGroup();
        this.running.set(true);
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(this.eventLoopGroup)
                .channel(LocalServerChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        NetworkConnection connection = new NetworkConnection(NetworkSide.Server, Server.this::handlePacket);
                        Server.this.connections.add(connection);
                        System.out.println("New connection at " + ch.remoteAddress());
                        ch.pipeline().addLast(new PacketDecoder(), new PacketEncoder(), connection);
                    }
                });

        Thread checkConnsThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Stopped checking bruv: " + e);
                    break;
                }

                Iterator<NetworkConnection> iter = this.connections.iterator();

                while (iter.hasNext()) {
                    NetworkConnection connection = iter.next();
                    if (!connection.isConnected()) {
                        iter.remove();
                        if (this.connectionMapN.containsKey(connection)) {
                            var value = this.connectionMapN.remove(connection);
                            this.connectionMap.remove(value);

                            for (NetworkConnection cc : this.connectionMapN.keySet()) {
                                if (cc == connection) continue;
                                cc.sendPacket(new RemovePlayerPacket(value));
                            }
                        }
                        System.out.println("[Server] " + this.connectionMapN.getOrDefault(connection, "Unknown") + " logged out");
                        System.out.println("[Server] " + connection.getAddress() + " has disconnected");
                    }
                }
            }
        }, "Check Conns");
        checkConnsThread.setDaemon(true);
        checkConnsThread.start();

        try {
            var cf = bootstrap.localAddress(LocalAddress.ANY).bind().sync();
            this.bootstraps.add(cf);
            return cf.channel().localAddress();
        } catch (Exception e) {
            System.out.println("SERVER THREAD: " + e);
            return null;
        }
    }

    public void openToLan(int port) {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(this.eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        NetworkConnection connection = new NetworkConnection(NetworkSide.Server, Server.this::handlePacket);
                        Server.this.connections.add(connection);
                        System.out.println("New connection at " + ch.remoteAddress());
                        ch.pipeline().addLast(new PacketDecoder(), new PacketEncoder(), connection);
                    }
                });

        var a = bootstrap.bind((InetAddress) null, port);
        this.bootstraps.add(a);
        a.syncUninterruptibly();
    }

    private void printMessage(String message) {
        System.out.println(message);
        if (this.gui != null) this.gui.textArea.append(message + "\n");
    }

    private void handlePacket(NetworkConnection conn, Packet packet) {
        if (packet instanceof LoginPacket loginPacket) {
            this.connectionMap.put(loginPacket.getUsername(), conn);
            this.connectionMapN.put(conn, loginPacket.getUsername());

            this.printMessage(loginPacket.getUsername() + " logged in");

            Player p = this.playerMap.get(loginPacket.getUsername());
            if (p == null) {
                p = new Player();
                p.color = loginPacket.getColor();// 0xFF << 24 | (int)(Math.random() * 254) << 16 | (int)(Math.random() * 254) << 8 | (int)(Math.random() * 254);
            }

            conn.sendPacket(new PlayPacket(loginPacket.getUsername(), p.x, p.y, p.color, p.dir));
            this.playerMap.put(loginPacket.getUsername(), p);

            for (Map.Entry<String, NetworkConnection> coo : this.connectionMap.entrySet()) {
                if (!coo.getKey().equals(loginPacket.getUsername())) {
                    coo.getValue().sendPacket(new PosPacket(loginPacket.getUsername(), p.x, p.y, p.color, p.dir));

                    Player a = this.playerMap.get(coo.getKey());
                    if (a != null) {
                        conn.sendPacket(new PosPacket(coo.getKey(), a.x, a.y, a.color, p.dir));
                    }
                }
            }

            conn.sendPacket(new WorldDataPacket(new byte[] { 1, 0, 1, 1 }));
        } else if (packet instanceof MessagePacket messagePacket) {
            var msg = "<" + this.connectionMapN.get(conn) + "> " + messagePacket.getMessage();
            this.messages.add(new ChatMessage(msg, messagePacket.getTimestamp()));
            for (NetworkConnection connection1 : this.connections) {
                connection1.sendPacket(new MessagePacket(msg, messagePacket.getTimestamp()));
            }
            if (this.gui != null) this.gui.textArea.append(msg + "\n");
        } else if (packet instanceof PosPacket posPacket) {
            if (this.playerMap.containsKey(posPacket.getUsername())) {
                Player p = this.playerMap.get(posPacket.getUsername());
                p.x = posPacket.getX();
                p.y = posPacket.getY();
                p.dir = posPacket.getDir();

                for (NetworkConnection c : this.connectionMapN.keySet()) {
                    if (c == conn) continue;
                    c.sendPacket(new PosPacket(posPacket.getUsername(), p.x, p.y, p.color, p.dir));
                }
            } else {
                conn.sendPacket(new DisconnectPacket("Who the fuck are you bruv?"));
                conn.disconnect();
            }
        } else if (packet instanceof RequestPreviousMessagesPacket) {
            conn.sendPackets(this.messages.stream().map((msg) -> new MessagePacket(msg.message(), msg.timestamp())).toArray(Packet[]::new));
        }
    }

    public void startAt(String ip, int port, boolean showGui, boolean useStdIn) {
        this.eventLoopGroup = new NioEventLoopGroup();
        if (showGui) this.startGui();
        this.running.set(true);
        if (showGui) this.gui.setOnSend(this::onSend);
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(this.eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        NetworkConnection connection = new NetworkConnection(NetworkSide.Server, Server.this::handlePacket);
                        Server.this.connections.add(connection);
                        System.out.println("New connection at " + ch.remoteAddress());
                        if (showGui) Server.this.gui.textArea.append("New connection at " + ch.remoteAddress() + "\n");
                        ch.pipeline().addLast(new PacketDecoder(), new PacketEncoder(), connection);
                    }
                });

        Thread checkConnsThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("Stopped checking bruv: " + e);
                    break;
                }

                Iterator<NetworkConnection> iter = this.connections.iterator();

                while (iter.hasNext()) {
                    NetworkConnection connection = iter.next();

                    if (!connection.isConnected()) {
                        iter.remove();
                        System.out.println(this.connectionMapN.getOrDefault(connection, "Unknown") + " logged out");
                        System.out.println(connection.getAddress() + " has disconnected");
                        if (this.gui != null) this.gui.textArea.append(connection.getAddress() + " has disconnected" + "\n");
                    }
                }
            }
        }, "Check Conns");
        checkConnsThread.setDaemon(true);
        checkConnsThread.start();

        try {
            var cf = bootstrap.bind(ip, port).syncUninterruptibly();
            this.bootstraps.add(cf);
            System.out.println("Server at " + ip + ":" + port);

            if (useStdIn) {
                Thread anotherThread = new Thread(() -> {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                    String line;
                    try {
                        while (this.running.get()) {
                            if ((line = reader.readLine()) == null) {
                                this.running.set(false);
                                this.gui.frame.dispose();
                                break;
                            }

                            line = line.trim();

                            if (!line.isEmpty()) {
                                this.gui.onSend.accept(line);
                            }
                        }
                    } catch (IOException ignored) {
                    }
                });
                anotherThread.setDaemon(true);
                anotherThread.start();
            }

            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.eventLoopGroup.shutdownGracefully();
        }
    }

    public void startGui() {
        if (this.gui == null) {
            this.gui = new ServerConsoleGui();
            this.gui.setOnClose(this::close);
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

        if (this.gui != null) {
            this.gui.frame.dispose();
            this.gui = null;
        }

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
}
