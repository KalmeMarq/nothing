package me.kalmemarq.client;

import imgui.type.ImString;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.kalmemarq.client.render.Font;
import me.kalmemarq.client.render.Framebuffer;
import me.kalmemarq.client.render.ImGuiLayer;
import me.kalmemarq.client.render.Shader;
import me.kalmemarq.client.render.Window;
import me.kalmemarq.client.resource.DefaultResourcePack;
import me.kalmemarq.client.resource.ResourcePack;
import me.kalmemarq.client.texture.TextureManager;
import me.kalmemarq.network.NetworkConnection;
import me.kalmemarq.network.NetworkSide;
import me.kalmemarq.Player;
import me.kalmemarq.client.screen.Screen;
import me.kalmemarq.client.screen.TitleScreen;
import me.kalmemarq.network.packet.WorldDataPacket;
import me.kalmemarq.server.Server;
import me.kalmemarq.Utils;
import me.kalmemarq.network.packet.DisconnectPacket;
import me.kalmemarq.network.packet.MessagePacket;
import me.kalmemarq.network.packet.Packet;
import me.kalmemarq.network.packet.PacketDecoder;
import me.kalmemarq.network.packet.PacketEncoder;
import me.kalmemarq.network.packet.PlayPacket;
import me.kalmemarq.network.packet.PosPacket;
import me.kalmemarq.network.packet.RemovePlayerPacket;
import me.kalmemarq.client.sound.SoundManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Client implements Window.WindowEventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("Client");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/dd/MM HH:mm:ss").withZone(ZoneId.systemDefault());
    private EventLoopGroup eventLoopGroup;

    private boolean running;
    private final ResourcePack rp;
    public final Window window;

    public final List<String> messages = new ArrayList<>();

    public NetworkConnection connection;
    public Server integratedServer;
    private boolean hasOpenToLan = false;
    public final ImString connectionText = new ImString();

    private final Map<String, Player> playerList = new ConcurrentHashMap<>();
    public Player player;
    public final TextureManager textureManager;
    private SoundManager soundManager;
    private Font font;
    public Screen screen;
    public boolean showImGuiLayer;
    private ImGuiLayer imGuiLayer;
    public final Path savePath;
    public final Settings settings;

    public Client(Path savePath) {
        this.savePath = savePath;
        this.rp = DefaultResourcePack.get();
        this.textureManager = new TextureManager();
        this.font = new Font(this.textureManager);
        this.soundManager = new SoundManager();
        this.settings = new Settings(this.savePath);
        this.settings.load();
        this.window = new Window(800, 400, "Test Game");
        this.imGuiLayer = new ImGuiLayer(this);
    }

	@Override
	public void onResize() {
		GL11.glViewport(0, 0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
	}

	public SoundManager getSoundManager() {
        return this.soundManager;
    }

    private void handlePacket(NetworkConnection conn, Packet packet) {
        if (packet instanceof MessagePacket messagePacket) {
            this.messages.add("[" + DATE_FORMATTER.format(messagePacket.getTimestamp()) + "] " + messagePacket.getMessage());
        } else if (packet instanceof DisconnectPacket disconnectPacket) {
            this.connectionText.set(disconnectPacket.getReason());
            this.disconnect();
        } else if (packet instanceof RemovePlayerPacket removePlayerPacket) {
             this.playerList.remove(removePlayerPacket.getUsername());
        } else if (packet instanceof PosPacket posPacket) {
            Player p = this.playerList.get(posPacket.getUsername());
            if (p == null) {
                p = new Player();
                p.color = posPacket.getColor();
                this.playerList.put(posPacket.getUsername(), p);
            }
            p.prevX = p.x;
            p.prevY = p.y;
            p.x = posPacket.getX();
            p.y = posPacket.getY();
            p.dir = posPacket.getDir();
        } else if (packet instanceof PlayPacket playPacket) {
            this.player = new Player();
            this.player.x = playPacket.getX();
            this.player.y = playPacket.getY();
            this.player.prevX = this.player.x;
            this.player.prevY = this.player.y;
            this.player.color = playPacket.getColor();
            this.player.dir = playPacket.getDir();
        } else if (packet instanceof WorldDataPacket worldDataPacket) {
        }
    }

    public boolean connect(String ip, int port) {
        if (this.eventLoopGroup == null) this.eventLoopGroup = new NioEventLoopGroup();

        NetworkConnection connection = new NetworkConnection(NetworkSide.Client, this::handlePacket);

        Bootstrap bootstrap = new Bootstrap()
                .group(this.eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new PacketDecoder(), new PacketEncoder(), connection);
                    }
                });

        try {
            bootstrap.connect(ip, port).syncUninterruptibly();
            this.connection = connection;
            this.connectionText.set("");
        } catch (Exception e) {
            this.connectionText.set(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean startIntegrated() {
        Server server = new Server();
        SocketAddress address = server.startLocal();

        if (address == null) {
            this.connectionText.set("Failed to start integrated server!");
            return false;
        }

        this.integratedServer = server;

        if (this.eventLoopGroup == null) this.eventLoopGroup = new NioEventLoopGroup();

        NetworkConnection connection = new NetworkConnection(NetworkSide.Client, this::handlePacket);

        try {
            Bootstrap bootstrap = new Bootstrap()
                .group(this.eventLoopGroup)
                .channel(LocalChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new PacketDecoder(), new PacketEncoder(), connection);
                    }
                });
            bootstrap.connect(address).syncUninterruptibly();
            this.connectionText.set("");
            this.connection = connection;
        } catch (Exception e) {
            this.connectionText.set(e.getMessage());
            return false;
        }

        return true;
    }

    private void tick() {
        if (this.player != null && (this.player.x != this.player.prevX || this.player.y != this.player.prevY)) {
            this.connection.sendPacket(new PosPacket(this.settings.username, this.player.x, this.player.y, 0, this.player.dir));
        }

        if (this.player != null) {
            this.player.prevX = this.player.x;
            this.player.prevY = this.player.y;

            float xa = 0;
            float ya = 0;

            if (GLFW.glfwGetKey(this.window.getHandle(), GLFW.GLFW_KEY_W) != GLFW.GLFW_RELEASE) {
                ya -= 1;
            }

            if (GLFW.glfwGetKey(this.window.getHandle(), GLFW.GLFW_KEY_S) != GLFW.GLFW_RELEASE) {
                ya += 1;
            }

            if (GLFW.glfwGetKey(this.window.getHandle(), GLFW.GLFW_KEY_D) != GLFW.GLFW_RELEASE) {
                xa += 1;
            }

            if (GLFW.glfwGetKey(this.window.getHandle(), GLFW.GLFW_KEY_A) != GLFW.GLFW_RELEASE) {
                xa -= 1;
            }

            if (xa < 0) this.player.dir = 2;
            if (xa > 0) this.player.dir = 3;
            if (ya < 0) this.player.dir = 1;
            if (ya > 0) this.player.dir = 0;

            this.player.x += xa * 2f;
            this.player.y += ya * 2f;
        }

        if (this.connection != null) this.connection.tick();

        this.soundManager.tick();
    }

    public Font getFont() {
        return this.font;
    }

    public void shutdown() {
        this.running = false;
    }

    public void run() {
        this.running = true;

        long lastFCTime = System.currentTimeMillis();
        long lastTimeTick = System.nanoTime();
        double unprocessed = 0;
        int frameCounter = 0;
        int tickCounter = 0;
		this.window.setWindowEventHandler(this);
        this.window.setKeyboardEventHandler(new KeyboardHandler(this));
        this.window.setMouseEventHandler(new MouseHandler(this));
        this.screen = new TitleScreen(this);
        this.font.load();
        this.soundManager.init();

        Framebuffer f = new Framebuffer();
        f.create(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());

        Shader shader = new Shader("blit_screen");
        for (Map.Entry<String, Integer> entry : shader.getUniformLocations().entrySet()) {
            LOGGER.info("Name: {} | Loc: {}", entry.getKey(), entry.getValue());
        }

        while (this.running) {
            if (this.window.shouldClose()) {
                this.running = false;
            }

            this.window.pollEvents();

            long nowa = System.nanoTime();
            double nsPerTick = 1E9D / 20;
            unprocessed += (nowa - lastTimeTick) / nsPerTick;
            lastTimeTick = nowa;
            while (unprocessed >= 1) {
                if (unprocessed > 10) unprocessed = 10;
                tickCounter++;
                this.tick();
                unprocessed--;
            }

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            f.begin(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());

            GL11.glClearColor(0.1f, 0.5f, 0.7f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0, this.window.getFramebufferWidth() / 3, this.window.getFramebufferHeight() / 3, 0, 1000, 3000);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0, 0, -2000);

            if (this.screen != null) this.screen.render(this.window.getFramebufferWidth() / 3, this.window.getFramebufferHeight() / 3, 0, 0);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.textureManager.bind("skins.png");

            if (this.connection != null && this.player != null) {
                GL11.glPushMatrix();
                GL11.glTranslatef(this.window.getFramebufferWidth() / 3 / 2 - this.player.x, this.window.getFramebufferHeight() / 3 / 2 - this.player.y, 0);


                float[] color = new float[3];
                int u = this.player.dir == 2 ? 3 * 16 : this.player.dir * 16;
                int v = 0;
                float u0 = (u) / 256.0f;
                float v0 = v / 256.0f;
                float u1 = (u + 16) / 256.0f;
                float v1 = (v + 16) / 256.0f;

                if (this.player.dir == 2) {
                    float temp = u0;
                    u0 = u1;
                    u1 = temp;
                }

                GL11.glBegin(GL11.GL_QUADS);

                Utils.unpackARGB(this.player.color, color);
                GL11.glColor4f(color[0], color[1], color[2], 1.0f);

                GL11.glTexCoord2f(u0, v0);
                GL11.glVertex3f(this.player.x - 8, this.player.y - 8, 0);
                GL11.glTexCoord2f(u0, v1);
                GL11.glVertex3f(this.player.x - 8, this.player.y + 8, 0);
                GL11.glTexCoord2f(u1, v1);
                GL11.glVertex3f(this.player.x + 8, this.player.y + 8, 0);
                GL11.glTexCoord2f(u1, v0);
                GL11.glVertex3f(this.player.x + 8, this.player.y - 8, 0);

                for (Player p : this.playerList.values()) {
                    u = p.dir == 2 ? 3 * 16 : p.dir * 16;
                    v = 0;
                    u0 = (u) / 256.0f;
                    v0 = v / 256.0f;
                    u1 = (u + 16) / 256.0f;
                    v1 = (v + 16) / 256.0f;

                    if (p.dir == 2) {
                        float temp = u0;
                        u0 = u1;
                        u1 = temp;
                    }

                    Utils.unpackARGB(p.color, color);
                    GL11.glColor4f(color[0], color[1], color[2], 1.0f);

                    GL11.glTexCoord2f(u0, v0);
                    GL11.glVertex3f(p.x, p.y, 0);
                    GL11.glTexCoord2f(u0, v1);
                    GL11.glVertex3f(p.x, p.y + 16, 0);
                    GL11.glTexCoord2f(u1, v1);
                    GL11.glVertex3f(p.x + 16, p.y + 16, 0);
                    GL11.glTexCoord2f(u1, v0);
                    GL11.glVertex3f(p.x + 16, p.y, 0);
                }

                GL11.glEnd();
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }

            f.end();

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            f.draw(shader);

            if (this.showImGuiLayer) {
                this.imGuiLayer.render();
            }

            this.window.swapBuffers();

            ++frameCounter;

            if (System.currentTimeMillis() - lastFCTime > 1000) {
                lastFCTime = System.currentTimeMillis();
                GLFW.glfwSetWindowTitle(this.window.getHandle(), "Test Game - " + frameCounter + " FPS " + tickCounter +  " TK");
                frameCounter = 0;
                tickCounter = 0;
            }
        }

		shader.close();
        f.close();
        this.close();
    }

    public void disconnect() {
        if (this.connection != null) {
            this.connection.disconnect();
            this.playerList.clear();
            this.player = null;
            if (this.integratedServer != null) this.integratedServer.close();
//            if (this.integratedServer != null) this.connectionText.set("");
            this.connection = null;
            this.integratedServer = null;
            this.messages.clear();
            this.hasOpenToLan = false;
        }
    }

    public void close() {
        this.settings.save();
        this.disconnect();

        this.imGuiLayer.close();

        this.textureManager.close();
        this.soundManager.close();
        this.window.close();

        if (this.eventLoopGroup != null) this.eventLoopGroup.shutdownGracefully();
    }

    public static ByteBuffer getByteBufferFromInputStream(InputStream inputStream) {
        ByteBuffer buffer = MemoryUtil.memAlloc(8192);

        try (ReadableByteChannel channel = Channels.newChannel(inputStream)) {
            while (true) {
                int bytes = channel.read(buffer);
                if (bytes == -1) break;
                if (buffer.remaining() == 0) {
                    buffer = MemoryUtil.memRealloc(buffer, buffer.capacity() * 2);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        buffer.flip();
        return buffer;
    }
}
