package me.kalmemarq.client.render;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public class Window implements AutoCloseable {
    private long handle;
	private int width;
	private int height;
	private int framebufferWidth;
	private int framebufferHeight;
	private WindowEventHandler windowEventHandler;
    private MouseEventHandler mouseEventHandler;
    private KeyboardEventHandler keyboardEventHandler;

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

        GL.createCapabilities();

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

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(this.handle);
    }

    public void pollEvents() {
        GLFW.glfwPollEvents();
    }

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(this.handle);
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
    }

    public interface MouseEventHandler {
        void onMouseButton(int button, int action, int mods);
        void onCursorPos(double x, double y);
    }

    public interface KeyboardEventHandler {
        void onKey(int key, int scancode, int action, int mods);
        void onCharTyped(int codepoint);
    }
}
