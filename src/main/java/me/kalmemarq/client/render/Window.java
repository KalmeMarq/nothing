package me.kalmemarq.client.render;

import me.kalmemarq.client.Client;
import me.kalmemarq.client.resource.DefaultResourcePack;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Window implements AutoCloseable {
    private final long handle;

	private int windowedX;
	private int windowedY;
	private int windowedWidth;
	private int windowedHeight;

	private int x;
	private int y;
	private int width;
	private int height;
	private int framebufferWidth;
	private int framebufferHeight;
	private boolean focused;
	private WindowEventHandler windowEventHandler;
    private MouseEventHandler mouseEventHandler;
    private KeyboardEventHandler keyboardEventHandler;
	private boolean fullscreen;
	private boolean currentFullscreen;
	private GLCapabilities capabilities;

    public Window(int width, int height, String title) {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Could not init GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        this.handle = GLFW.glfwCreateWindow(width, height, title, 0L, 0L);

        GLFW.glfwMakeContextCurrent(this.handle);
        GLFW.glfwSwapInterval(1);

        GLFW.glfwSetMouseButtonCallback(this.handle, (_w, button, action, mods) -> {
           if (this.mouseEventHandler != null)
               this.mouseEventHandler.onMouseButton(button, action, mods);
        });

        GLFW.glfwSetCursorPosCallback(this.handle, (_w, x, y) -> {
            if (this.mouseEventHandler != null)
                this.mouseEventHandler.onCursorPos(x, y);
        });

        GLFW.glfwSetKeyCallback(this.handle, (_w, key, scancode, action, mods) -> {
            if (this.keyboardEventHandler != null)
                this.keyboardEventHandler.onKey(key, scancode, action, mods);
        });

        GLFW.glfwSetCharCallback(this.handle, (_w, codepoint) -> {
            if (this.keyboardEventHandler != null)
                this.keyboardEventHandler.onCharTyped(codepoint);
        });
		
		GLFW.glfwSetFramebufferSizeCallback(this.handle, (_w, w, h) -> {
			this.framebufferWidth = w;
			this.framebufferHeight = h;
			
			if (this.windowEventHandler != null) this.windowEventHandler.onResize();
		});

        this.capabilities = GL.createCapabilities();

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer wP = stack.mallocInt(1);
			IntBuffer hP = stack.mallocInt(1);

			GLFW.glfwGetWindowSize(this.handle, wP, hP);

			this.width = wP.get(0);
			this.height = hP.get(0);
		}
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer wP = stack.mallocInt(1);
			IntBuffer hP = stack.mallocInt(1);
			
			GLFW.glfwGetFramebufferSize(this.handle, wP, hP);
			
			this.framebufferWidth = wP.get(0);
			this.framebufferHeight = hP.get(0);
		}
		
		
		this.focused = GLFW.glfwGetWindowAttrib(this.handle, GLFW.GLFW_FOCUSED) != 0;
		
		GLFW.glfwSetWindowFocusCallback(this.handle, (_w, focused) -> {
			this.focused = focused;
			if (this.windowEventHandler != null) this.windowEventHandler.onFocusChanged();
		});

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer xP = stack.mallocInt(1);
			IntBuffer yP = stack.mallocInt(1);

			GLFW.glfwGetWindowPos(this.handle, xP, yP);

			this.x = xP.get(0);
			this.y = yP.get(0);
		}
		
		GLFW.glfwSetWindowPosCallback(this.handle, (_w, x, y) -> {
			this.x = x;
			this.y = y;
		});

		var rp = DefaultResourcePack.get();
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer wP = stack.mallocInt(1);
			IntBuffer hP = stack.mallocInt(1);
			IntBuffer cP = stack.mallocInt(1);

			try (GLFWImage.Buffer icons = GLFWImage.malloc(3)) {
				ByteBuffer icon16Data = Client.getByteBufferFromInputStream(rp.getResource("assets/minicraft/icons/icon16.png").get().getInputStream());
				ByteBuffer icon16Pixels = STBImage.stbi_load_from_memory(icon16Data, wP, hP, cP, 4);

				icons.position(0);
				icons.width(16);
				icons.height(16);
				icons.pixels(icon16Pixels);

				ByteBuffer icon32Data = Client.getByteBufferFromInputStream(rp.getResource("assets/minicraft/icons/icon32.png").get().getInputStream());
				ByteBuffer icon32Pixels = STBImage.stbi_load_from_memory(icon32Data, wP, hP, cP, 4);

				icons.position(1);
				icons.width(32);
				icons.height(32);
				icons.pixels(icon32Pixels);

				ByteBuffer icon64Data = Client.getByteBufferFromInputStream(rp.getResource("assets/minicraft/icons/icon64.png").get().getInputStream());
				ByteBuffer icon64Pixels = STBImage.stbi_load_from_memory(icon64Data, wP, hP, cP, 4);

				icons.position(2);
				icons.width(64);
				icons.height(64);
				icons.pixels(icon64Pixels);

				icons.position(0);
				GLFW.glfwSetWindowIcon(this.handle, icons);

				STBImage.stbi_image_free(icon16Pixels);
				STBImage.stbi_image_free(icon32Pixels);
				STBImage.stbi_image_free(icon64Pixels);
				MemoryUtil.memFree(icon16Data);
				MemoryUtil.memFree(icon32Data);
				MemoryUtil.memFree(icon64Data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

	public void setWindowEventHandler(WindowEventHandler windowEventHandler) {
		this.windowEventHandler = windowEventHandler;
	}

	public void setKeyboardEventHandler(KeyboardEventHandler keyboardEventHandler) {
        this.keyboardEventHandler = keyboardEventHandler;
    }

    public void setMouseEventHandler(MouseEventHandler mouseEventHandler) {
        this.mouseEventHandler = mouseEventHandler;
    }

	public GLCapabilities getCapabilities() {
		return this.capabilities;
	}

	public void toggleFullscreen() {
		this.fullscreen = !this.fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public boolean isFocused() {
		return this.focused;
	}

	public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(this.handle);
    }

    public void pollEvents() {
        GLFW.glfwPollEvents();
    }

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(this.handle);
		
		if (this.fullscreen != this.currentFullscreen) {
			this.currentFullscreen = this.fullscreen;
			
			if (this.fullscreen) {
				this.windowedX = this.x;
				this.windowedY = this.y;
				this.windowedWidth = this.width;
				this.windowedHeight = this.height;
				
				long monitor = GLFW.glfwGetPrimaryMonitor();
				var videoMode = GLFW.glfwGetVideoMode(monitor);
			
				GLFW.glfwSetWindowMonitor(this.handle, monitor, 0, 0, videoMode.width(), videoMode.height(), videoMode.refreshRate());
			} else {
				this.x = this.windowedX;
				this.y = this.windowedY;
				this.width = this.windowedWidth;
				this.height = this.windowedHeight;
				
				GLFW.glfwSetWindowMonitor(this.handle, 0L, this.x, this.y, this.width, this.height, 0);
			}
		}
    }

    public long getHandle() {
        return this.handle;
    }

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getFramebufferWidth() {
		return this.framebufferWidth;
	}

	public int getFramebufferHeight() {
		return this.framebufferHeight;
	}

	@Override
    public void close() {
        Callbacks.glfwFreeCallbacks(this.handle);
        GLFW.glfwTerminate();
    }

    public interface WindowEventHandler {
		void onResize();
		void onFocusChanged();
    }

    public interface MouseEventHandler {
        void onMouseButton(int button, int action, int mods);
        void onCursorPos(double x, double y);
    	void onScroll(double x, double y);
	}

    public interface KeyboardEventHandler {
        void onKey(int key, int scancode, int action, int mods);
        void onCharTyped(int codepoint);
    }
}
