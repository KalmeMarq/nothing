package me.kalmemarq.client;

import me.kalmemarq.client.render.Window;
import org.lwjgl.glfw.GLFW;

public class MouseHandler implements Window.MouseEventHandler {
    private final Client client;
	private double x;
	private double y;

    public MouseHandler(Client client) {
        this.client = client;
    }

    @Override
    public void onMouseButton(int button, int action, int mods) {
		if (this.client.menu == null) return;
		
		if (action != GLFW.GLFW_RELEASE) {
			this.client.menu.mousePressed(button, (int) (x / 3), (int) (y / 3));
		} else {
			this.client.menu.mouseReleased(button, (int) (x / 3), (int) (y / 3));
		}
    }

    @Override
    public void onCursorPos(double x, double y) {
    	this.x = x;
		this.y = y;
	}

	@Override
	public void onScroll(double x, double y) {
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}
}
