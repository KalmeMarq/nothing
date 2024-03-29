package me.kalmemarq.client;

import imgui.type.ImString;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.kalmemarq.client.screen.ChatMenu;
import me.kalmemarq.common.logging.LogManager;
import me.kalmemarq.common.logging.Logger;
import me.kalmemarq.common.world.Level;
import me.kalmemarq.common.ThreadExecutor;
import me.kalmemarq.client.render.Font;
import me.kalmemarq.client.render.Framebuffer;
import me.kalmemarq.client.render.ImGuiLayer;
import me.kalmemarq.client.render.Renderer;
import me.kalmemarq.client.render.Shader;
import me.kalmemarq.client.render.Window;
import me.kalmemarq.client.resource.DefaultResourcePack;
import me.kalmemarq.client.resource.ResourcePack;
import me.kalmemarq.client.texture.TextureManager;
import me.kalmemarq.common.network.NetworkConnection;
import me.kalmemarq.common.network.NetworkSide;
import me.kalmemarq.common.entity.PlayerEntity;
import me.kalmemarq.client.screen.Menu;
import me.kalmemarq.client.screen.TitleMenu;
import me.kalmemarq.common.network.packet.*;
import me.kalmemarq.server.IntegratedServer;
import me.kalmemarq.client.sound.SoundManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

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

public class Client extends ThreadExecutor implements Window.WindowEventHandler {
    public static final Logger LOGGER = LogManager.getLogger("Client");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/dd/MM HH:mm:ss").withZone(ZoneId.systemDefault());
    public EventLoopGroup eventLoopGroup;

	private final Thread thread;
    public boolean running;
    public final ResourcePack rp;
    public final Window window;

    public final List<String> messages = new ArrayList<>();

    public NetworkConnection connection;
	public ClientNetworkHandler connHandler;
    public IntegratedServer integratedServer;
    public boolean hasOpenToLan = false;
    public final ImString connectionText = new ImString();

    public final Map<String, PlayerEntity> playerList = new ConcurrentHashMap<>();
    public PlayerEntity player;
    public final TextureManager textureManager;
    public SoundManager soundManager;
    public Font font;
    public Menu menu;
    public boolean showImGuiLayer;
    public ImGuiLayer imGuiLayer;
    public final Path savePath;
    public final Settings settings;
	public int currentFps;
	public int currentTicks;
	public Level level;
	public Renderer renderer;
	public boolean showDebugHud;
	public Shader blitScreenShader;
	public DiscordHelper discordHelper;
	public final boolean debugMode;
	public final MouseHandler mouseHandler;

	public Client(boolean debugMode, Path savePath) {
        this.debugMode = debugMode;
		this.savePath = savePath;
        this.rp = DefaultResourcePack.get();
        this.textureManager = new TextureManager();
        this.font = new Font(this.textureManager);
        this.soundManager = new SoundManager(this);
        this.settings = new Settings(this.savePath);
        this.settings.load();
		this.mouseHandler = new MouseHandler(this);
        this.window = new Window();
        this.imGuiLayer = new ImGuiLayer(this);
		this.renderer = new Renderer(this);
		this.thread = Thread.currentThread();
		this.discordHelper = new DiscordHelper(this);
    }

	@Override
	public Thread getThread() {
		return this.thread;
	}

	@Override
	public void onFocusChanged() {
	}

	@Override
	public void onResize() {
		GL11.glViewport(0, 0, this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
	}

	public SoundManager getSoundManager() {
        return this.soundManager;
    }

    public boolean connect(String ip, int port) {
        if (this.eventLoopGroup == null) this.eventLoopGroup = new NioEventLoopGroup(0, (runnable) -> {
			Thread thread = new Thread(runnable);
			thread.setName("Netty Client IO");
			thread.setDaemon(true);
			return thread;
		});

        NetworkConnection connection = new NetworkConnection(NetworkSide.CLIENT, this);
		ClientNetworkHandler handler = new ClientNetworkHandler(this, connection);
		connection.setListener(handler);

        Bootstrap bootstrap = new Bootstrap()
                .group(this.eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
						try {
							ch.config().setOption(ChannelOption.TCP_NODELAY, true);
						} catch (ChannelException ignored) {
						}
						NetworkConnection.addCommonHandlers(ch.pipeline(), connection);
                    }
                });

        try {
            bootstrap.connect(ip, port).syncUninterruptibly();
            this.connection = connection;
			this.connHandler = handler;
            this.connectionText.set("");
        } catch (Exception e) {
            this.connectionText.set(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean startIntegrated() {
		IntegratedServer server = new IntegratedServer();
        SocketAddress address = server.startLocal();

        if (address == null) {
            this.connectionText.set("Failed to start integrated server!");
            return false;
        }

        this.integratedServer = server;

        if (this.eventLoopGroup == null) this.eventLoopGroup = new NioEventLoopGroup(0, (runnable) -> {
			Thread thread = new Thread(runnable);
			thread.setName("Netty Client IO");
			thread.setDaemon(true);
			return thread;
		});

        NetworkConnection connection = new NetworkConnection(NetworkSide.CLIENT, this);
		ClientNetworkHandler handler = new ClientNetworkHandler(this, connection);
		connection.setListener(handler);

        try {
            Bootstrap bootstrap = new Bootstrap()
                .group(this.eventLoopGroup)
                .channel(LocalChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
						try {
							ch.config().setOption(ChannelOption.TCP_NODELAY, true);
						} catch (ChannelException ignored) {
						}
						ch.pipeline().addLast("handler", connection);
					}
                });
            bootstrap.connect(address).syncUninterruptibly();
            this.connectionText.set("");
            this.connection = connection;
            this.connHandler = handler;
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

        if (this.level != null) {
			this.level.tick();
			
			if (this.player != null) {
				this.player.tickClient(this);
			}
			
			if (this.menu == null && GLFW.glfwGetKey(this.window.getHandle(), GLFW.GLFW_KEY_T) == GLFW.GLFW_PRESS) {
				this.menu = new ChatMenu(this);
			}
        }

        if (this.connection != null) this.connection.tick();

        this.soundManager.tick();
		
		this.renderer.tick();
    }

    public Font getFont() {
        return this.font;
    }

    public void shutdown() {
        this.running = false;
    }

    public void run() {
        this.running = true;

		this.discordHelper.connect();
		
        long lastFCTime = System.currentTimeMillis();
        long lastTimeTick = System.nanoTime();
        double unprocessed = 0;
        int frameCounter = 0;
        int tickCounter = 0;
		
		this.window.init(800, 400, "Minicraft Not Plus");
		this.window.setWindowEventHandler(this);
        this.window.setKeyboardEventHandler(new KeyboardHandler(this));
        this.window.setMouseEventHandler(this.mouseHandler);
        this.menu = new TitleMenu(this);
        this.font.load();
        this.soundManager.init();
		this.renderer.setCapabilities(this.window.getCapabilities());

        Framebuffer f = new Framebuffer();

        this.blitScreenShader = new Shader("blit_screen");

		this.discordHelper.setStatus("In Main Menu");
		
        while (this.running) {
            if (this.window.shouldClose()) {
                this.running = false;
            }

            this.window.pollEvents();
			
			this.runTasks();

            long nowa = System.nanoTime();
            double nsPerTick = 1E9D / 60;
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

			this.renderer.render();
			
            f.end();

			Renderer.setMatrixMode(Renderer.MatrixMode.PROJECTION);
            Renderer.loadMatrixIdentity();
			Renderer.setMatrixMode(Renderer.MatrixMode.MODELVIEW);
			Renderer.loadMatrixIdentity();
            f.draw(this.blitScreenShader);

            if (this.showImGuiLayer) {
                this.imGuiLayer.render();
            }

            this.window.swapBuffers();

            ++frameCounter;

            if (System.currentTimeMillis() - lastFCTime > 1000) {
                lastFCTime = System.currentTimeMillis();
                this.currentFps = frameCounter;
                this.currentTicks = tickCounter;
				frameCounter = 0;
                tickCounter = 0;
            }
        }

		this.blitScreenShader.close();
        f.close();
        this.close();
    }

    public void disconnect() {
        if (this.connection != null) {
			this.discordHelper.setStatus("In Main Menu");
			this.level = null;
            this.connection.disconnect();
            this.playerList.clear();
            this.player = null;
            if (this.integratedServer != null) this.integratedServer.close();
            this.connection = null;
            this.connHandler = null;
            this.integratedServer = null;
            this.messages.clear();
            this.hasOpenToLan = false;
        }
    }

    public void close() {
        this.settings.save();
        this.disconnect();
		this.discordHelper.disconnect();

		this.renderer.dispose();
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
