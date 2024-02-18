package me.kalmemarq.client.screen;

import me.kalmemarq.common.Identifier;
import me.kalmemarq.client.Client;
import me.kalmemarq.client.render.Font;
import me.kalmemarq.client.render.Renderer;
import me.kalmemarq.client.texture.TextureManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Menu {
    protected Client client;
    protected Font font;
	protected MenuContainer menuContainer;
	protected boolean hasDarkBackground = true;

    public Menu(Client client) {
        this.client = client;
        this.font = client.getFont();
    }

    public void keyPressed(int key, int mods) {
		if (this.menuContainer != null) {
			if (this.menuContainer.entries.get(this.menuContainer.selectedIndex).keyPressed(key, mods)) {
				return;
			}
			
			boolean wasIndexMoved = false;
			if (key == GLFW.GLFW_KEY_UP || (key == GLFW.GLFW_KEY_W && !(this.menuContainer.entries.get(this.menuContainer.selectedIndex) instanceof TextBoxEntry))) {
				if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) {
					this.menuContainer.selectedIndex = 0;
					wasIndexMoved = true;
				} else {
					this.menuContainer.selectedIndex -= 1;
					if (this.menuContainer.selectedIndex < 0) {
						this.menuContainer.selectedIndex = this.menuContainer.entries.size() - 1;
					} else {
						if (this.menuContainer.entries.get(this.menuContainer.selectedIndex) instanceof EmptyEntry) {
							this.menuContainer.selectedIndex -= 1;
						}

						if (this.menuContainer.selectedIndex < 0) {
							this.menuContainer.selectedIndex = this.menuContainer.entries.size() - 1;
						}
					}
					wasIndexMoved = true;
				}
			}

			if (key == GLFW.GLFW_KEY_DOWN || (key == GLFW.GLFW_KEY_S && !(this.menuContainer.entries.get(this.menuContainer.selectedIndex) instanceof TextBoxEntry))) {
				if ((mods & GLFW.GLFW_MOD_CONTROL) != 0) {
					this.menuContainer.selectedIndex = this.menuContainer.entries.size() - 1;
					wasIndexMoved = true;
				} else {
					this.menuContainer.selectedIndex += 1;
					if (this.menuContainer.selectedIndex >= this.menuContainer.entries.size()) {
						this.menuContainer.selectedIndex = 0;
					} else {
						if (this.menuContainer.entries.get(this.menuContainer.selectedIndex) instanceof EmptyEntry) {
							this.menuContainer.selectedIndex += 1;
						}

						if (this.menuContainer.selectedIndex >= this.menuContainer.entries.size()) {
							this.menuContainer.selectedIndex = 0;
						}
					}
					wasIndexMoved = true;
				}
			}
			
			if (wasIndexMoved) {
				this.client.getSoundManager().play(Identifier.of("minicraft:sounds/select.ogg"), 1.0f, 1.0f);
			}

			if (key == GLFW.GLFW_KEY_ENTER || (key == GLFW.GLFW_KEY_SPACE && !(this.menuContainer.entries.get(this.menuContainer.selectedIndex) instanceof TextBoxEntry))) {
				this.menuContainer.entries.get(this.menuContainer.selectedIndex).onPress.run();
				this.client.getSoundManager().play(Identifier.of("minicraft:sounds/confirm.ogg"), 1.0f, 1.0f);
			}
		}
    }

    public void keyReleased(int key, int mods) {
	}

    public void charTyped(int codepoint) {
		if (this.menuContainer != null) {
			this.menuContainer.entries.get(this.menuContainer.selectedIndex).charTyped(codepoint);
		}
    }

    public void mousePressed(int button, int mouseX, int mouseY) {
		if (this.menuContainer != null && button == 0) {
			int y = this.menuContainer.offsetY + (this.menuContainer.withBackground ? 8 : 0);
			int i = 0;
			for (Entry entry : this.menuContainer.entries) {
				boolean isHovered = mouseX >= this.menuContainer.offsetX + (this.menuContainer.withBackground ? 8 : 0) && mouseX < this.menuContainer.offsetX + (this.menuContainer.withBackground ? 8 : 0) + entry.getText().length() * 8 && mouseY >= y && mouseY < y + 12;
				if (isHovered) {
					entry.onPress.run();
					return;
				}
				y += 14;
				i++;
			}
		}
    }

    public void mouseReleased(int button, int mouseX, int mouseY) {
    }

    public void render(int screenWidth, int screenHeight, int mouseX, int mouseY) {
		if (this.hasDarkBackground) {
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex3f(0, 0, 0);
			GL11.glVertex3f(0, screenHeight, 0);
			GL11.glVertex3f(screenWidth, screenHeight, 0);
			GL11.glVertex3f(screenWidth, 0, 0);
			GL11.glEnd();
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		
		if (this.menuContainer != null) {
			if (this.menuContainer.withBackground) {
				int maxWidth = 0;
				for (Entry entry : this.menuContainer.entries) {
					int entryWidth = entry.getText().length() * 8;
					if (entryWidth > maxWidth) maxWidth = entryWidth;
				}
				
				this.client.textureManager.bind(Identifier.of("minicraft:textures/hud.png"));
				
				Renderer.enableBlend();
				Renderer.setDefaultBlendFunc();
				GL11.glBegin(GL11.GL_QUADS);

				drawTextureQuad(this.menuContainer.offsetX, this.menuContainer.offsetY, 8, 8, 0, 48, 8, 8, 88, 56, false, false);
				drawTextureQuad(this.menuContainer.offsetX + maxWidth + 8, this.menuContainer.offsetY, 8, 8, 0, 48, 8, 8, 88, 56, true, false);

				int horCount = maxWidth / 8;
				int height = this.menuContainer.entries.size() * 12;
				int verCount = height / 8 + 1;
				
				for (int i = 0; i < horCount; ++i) {
					drawTextureQuad(this.menuContainer.offsetX + 8 + 8 * i, this.menuContainer.offsetY, 8, 8, 8, 48, 8, 8, 88, 56, false, false);
				
					for (int j = 0; j < verCount; ++j) {
						drawTextureQuad(this.menuContainer.offsetX + 8 + 8 * i,  this.menuContainer.offsetY + 8 + j * 8, 8, 8, 24, 48, 8, 8, 88, 56, false, true);
					}

					drawTextureQuad(this.menuContainer.offsetX + 8 + 8 * i, this.menuContainer.offsetY + height + 12, 8, 8, 8, 48, 8, 8, 88, 56, false, true);
				}
				
				for (int i = 0; i < verCount; ++i) {
					drawTextureQuad(this.menuContainer.offsetX, this.menuContainer.offsetY + 8 + i * 8, 8, 8, 16, 48, 8, 8, 88, 56, false, false);
					drawTextureQuad(this.menuContainer.offsetX + maxWidth + 8, this.menuContainer.offsetY + 8 + i * 8, 8, 8, 16, 48, 8, 8, 88, 56, true, false);
				}

				drawTextureQuad(this.menuContainer.offsetX, this.menuContainer.offsetY + height + 12, 8, 8, 0, 48, 8, 8, 88, 56, false, true);
				drawTextureQuad(this.menuContainer.offsetX + maxWidth + 8, this.menuContainer.offsetY + height + 12, 8, 8, 0, 48, 8, 8, 88, 56, true, true);
				
				GL11.glEnd();
				
				Renderer.disableBlend();
			}
			
			int y = this.menuContainer.offsetY + (this.menuContainer.withBackground ? 8 : 0);
			int i = 0;
			for (Entry entry : this.menuContainer.entries) {
				boolean isHovered = mouseX >= this.menuContainer.offsetX + (this.menuContainer.withBackground ? 8 : 0) && mouseX < this.menuContainer.offsetX + (this.menuContainer.withBackground ? 8 : 0) + entry.getText().length() * 8 && mouseY >= y && mouseY < y + 12; 
				this.font.drawText(entry.getText(), this.menuContainer.offsetX + (this.menuContainer.withBackground ? 8 : 0), y + 2, this.menuContainer.selectedIndex == i || isHovered ? 0xFFFFFF : 0x808080);
				y += 14;
				i++;
			}
		}
    }
	
	public static void renderFrame(TextureManager textureManager, Font font, int x, int y, int width, int height, String text, int color) {
		textureManager.bind(Identifier.of("minicraft:textures/hud.png"));
		
		int xx = x;
		
		y += 8;
		x -= (width - 16) / 2 + 8;
		
		int horCount = (width - 16) / 8;
		
		Renderer.enableBlend();
		Renderer.setDefaultBlendFunc();
		GL11.glBegin(GL11.GL_QUADS);

		drawTextureQuad(x, y, 8, 8, 0, 48, 8, 8, 88, 56, false, false);
		drawTextureQuad(x, y + 16, 8, 8, 0, 48, 8, 8, 88, 56, false, true);
		
		for (int i = 0; i < horCount; ++i) {
			drawTextureQuad(x + 8 + 8 * i, y, 8, 8, 8, 48, 8, 8, 88, 56, false, false);
			drawTextureQuad(x + 8 + 8 * i, y + 8, 8, 8, 24, 48, 8, 8, 88, 56, false, true);
			drawTextureQuad(x + 8 + 8 * i, y + 16, 8, 8, 8, 48, 8, 8, 88, 56, false, true);
		}
		
		drawTextureQuad(x, y + 8, 8, 8, 16, 48, 8, 8, 88, 56, false, false);
		drawTextureQuad(x + width - 8, y + 8, 8, 8, 16, 48, 8, 8, 88, 56, true, false);
		
		drawTextureQuad(x + width - 8, y, 8, 8, 0, 48, 8, 8, 88, 56, true, false);
		drawTextureQuad(x + width - 8, y + 16,  8, 8, 0, 48, 8, 8, 88, 56, true, true);
				
		GL11.glEnd();

		font.drawText(text, xx - text.length() * 4, y + 8, color);
		
		Renderer.disableBlend();
	}
	
	private static void drawTextureQuad(int x, int y, int width, int height, int u, int v, int us, int vs, int textureWidth, int textureHeight, boolean flipH, boolean flipV) {
		float u0 = u / (float) textureWidth;
		float v0 = v / (float) textureHeight;
		float u1 = (u + us) / (float) textureWidth;
		float v1 = (v + vs) / (float) textureHeight;
		
		if (flipH) {
			float temp = u0;
			u0 = u1;
			u1 = temp;
		}

		if (flipV) {
			float temp = v0;
			v0 = v1;
			v1 = temp;
		}
		
		GL11.glTexCoord2f(u0, v0);
		GL11.glVertex3f(x, y, 0);
		GL11.glTexCoord2f(u0, v1);
		GL11.glVertex3f(x, y + height, 0);
		GL11.glTexCoord2f(u1, v1);
		GL11.glVertex3f(x + width, y + height, 0);
		GL11.glTexCoord2f(u1, v0);
		GL11.glVertex3f(x + width, y, 0);
	}
	
	public static class MenuContainer {
		private int selectedIndex;
		private final List<Entry> entries = new ArrayList<>();
		public int offsetX = 20;
		public int offsetY = 50;
		public Anchor anchor = Anchor.TOP_LEFT;
		public boolean withBackground;
		
		public void addEntry(Entry entry) {
			this.entries.add(entry);
		}

		public int getSelectedIndex() {
			return this.selectedIndex;
		}

		public List<Entry> getEntries() {
			return this.entries;
		}
	}
	
	public enum Anchor {
		TOP_LEFT,
		TOP_MIDDLE,
		TOP_RIGHT,
		LEFT_MIDDLE,
		CENTER,
		RIGHT_MIDDLE,
		BOTTOM_LEFT,
		BOTTOM_MIDDLE,
		BOTTOM_RIGHT
	}
	
	public static class Entry {
		public String text;
		public Runnable onPress;

		public Entry(String text, Runnable onPress) {
			this.text = text;
			this.onPress = onPress;
		}

		public String getText() {
			return this.text;
		}

		public boolean keyPressed(int key, int mods) {
			return false;
		}

		public boolean charTyped(int codepoint) {
			return false;
		}
		
		public Runnable getOnPress() {
			return this.onPress;
		}
	}
	
	public static class EmptyEntry extends Entry {
		public EmptyEntry() {
			super("", () -> {});
		}
	}
	
	public static class TextBoxEntry extends Entry {
		private String name;
		private String value;
		private int maxLength;
		private Predicate<String> textPredicate;
		private Consumer<String> onChange;
		
		public TextBoxEntry(String name, String value, int maxLength, Predicate<String> textPredicate, Consumer<String> onChange) {
			super(name + ": " + value, () -> {});
			this.name = name;
			this.value = value;
			this.maxLength = maxLength;
			this.textPredicate = textPredicate;
			this.onChange = onChange;
		}
		
		@Override
		public boolean keyPressed(int key, int mods) {
			if (key == GLFW.GLFW_KEY_BACKSPACE && !this.value.isEmpty()) {
				this.value = this.value.substring(0, this.value.length() - 1);
				this.text = this.name + ": " + this.value;
				this.onChange.accept(this.value);
				return true;
			}
			return false;
		}

		@Override
		public boolean charTyped(int codepoint) {
			var character = Character.toString(codepoint);
			
			if (this.textPredicate.test(character) && this.value.length() + 1 <= this.maxLength) {
				this.value += character;
				this.onChange.accept(this.value);
				this.text = this.name + ": " + this.value;
				return true;
			}
			
			return false;
		}
	}
}
