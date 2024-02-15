package me.kalmemarq.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kalmemarq.common.Identifier;
import me.kalmemarq.common.Utils;
import me.kalmemarq.client.resource.DefaultResourcePack;
import me.kalmemarq.client.texture.TextureManager;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Font {
    private final TextureManager textureManager;
    private final Map<Character, Glyph> glyphMap = new HashMap<>();
	private boolean italic = false;

    public Font(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

	public void setItalic(boolean italic) {
		this.italic = italic;
	}
	
	public void load() {
        var rp = DefaultResourcePack.get();
        try {
            JsonObject obj = Utils.GSON.fromJson(Utils.readString(rp.getResource("assets/minicraft/fonts/font.json").get().inputSupplier().get()), JsonObject.class);
            JsonArray arr = obj.getAsJsonArray("chars");

            int r = 0;
            for (JsonElement line : arr) {
                int c = 0;

                for (char ch : line.getAsString().toCharArray()) {
                    if (ch != '\u0000')
                        this.glyphMap.put(ch, new Glyph(
                                (c * 8) / 128.0f,
                                (r * 12) / 128.0f,
                                (c * 8 + 8) / 128.0f,
                                (r * 12 + 12) / 128.0f,
                                8
                        ));

                    ++c;
                }

                ++r;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void drawText(String text, int x, int y, int color) {
		this.drawTextInternal(text, x, y, color, false, 0);
	}

	public void drawTextOutlined(String text, int x, int y, int color, int outlineColor) {
		this.drawTextInternal(text, x, y, color, true, outlineColor);
	}
	
    private void drawTextInternal(String text, int x, int y, int color, boolean hasOutline, int outlineColor) {
        if (text == null || text.isEmpty()) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.textureManager.bind(Identifier.of("minicraft:textures/font.png"));
        GL11.glBegin(GL11.GL_QUADS);

        char[] charr = text.toCharArray();
        float[] colorarr = new float[3];
        float[] outcolorarr = new float[3];
        
		Utils.unpackARGB(color, colorarr);
		Utils.unpackARGB(outlineColor, outcolorarr);
		
        int xx = x;
        for (char c : charr) {
            if (!Character.isWhitespace(c)) {
                Glyph g = this.glyphMap.get(c);

                if (g != null) {
                    if (hasOutline) {
                        GL11.glColor4f(outcolorarr[0], outcolorarr[1], outcolorarr[2], 1.0f);

                        // Top
                        GL11.glTexCoord2f(g.u0, g.v0);
                        GL11.glVertex3f(xx + (this.italic ? 4f : 0), y - 3 - 1, 0);
                        GL11.glTexCoord2f(g.u0, g.v1);
                        GL11.glVertex3f(xx, y + 12 - 3 - 1, 0);
                        GL11.glTexCoord2f(g.u1, g.v1);
                        GL11.glVertex3f(xx + 8, y + 12 - 3 - 1, 0);
                        GL11.glTexCoord2f(g.u1, g.v0);
                        GL11.glVertex3f(xx + 8 + (this.italic ? 4f : 0), y - 3 - 1, 0);

                        // Left
                        GL11.glTexCoord2f(g.u0, g.v0);
                        GL11.glVertex3f(xx - 1 + (this.italic ? 4f : 0), y - 3, 0);
                        GL11.glTexCoord2f(g.u0, g.v1);
                        GL11.glVertex3f(xx - 1, y + 12 - 3, 0);
                        GL11.glTexCoord2f(g.u1, g.v1);
                        GL11.glVertex3f(xx + 8 - 1, y + 12 - 3, 0);
                        GL11.glTexCoord2f(g.u1, g.v0);
                        GL11.glVertex3f(xx + 8 - 1 + (this.italic ? 4f : 0), y - 3, 0);

                        // Right
                        GL11.glTexCoord2f(g.u0, g.v0);
                        GL11.glVertex3f(xx + 1 + (this.italic ? 4f : 0), y - 3, 0);
                        GL11.glTexCoord2f(g.u0, g.v1);
                        GL11.glVertex3f(xx + 1, y + 12 - 3, 0);
                        GL11.glTexCoord2f(g.u1, g.v1);
                        GL11.glVertex3f(xx + 8 + 1, y + 12 - 3, 0);
                        GL11.glTexCoord2f(g.u1, g.v0);
                        GL11.glVertex3f(xx + 8 + 1 + (this.italic ? 4f : 0), y - 3, 0);

                        // Bottom
                        GL11.glTexCoord2f(g.u0, g.v0);
                        GL11.glVertex3f(xx + (this.italic ? 4f : 0), y - 3 + 1, 0);
                        GL11.glTexCoord2f(g.u0, g.v1);
                        GL11.glVertex3f(xx, y + 12 - 3 + 1, 0);
                        GL11.glTexCoord2f(g.u1, g.v1);
                        GL11.glVertex3f(xx + 8, y + 12 - 3 + 1, 0);
                        GL11.glTexCoord2f(g.u1, g.v0);
                        GL11.glVertex3f(xx + 8 + (this.italic ? 4f : 0), y - 3 + 1, 0);
                    }

                    GL11.glColor4f(colorarr[0], colorarr[1], colorarr[2], 1.0f);

                    GL11.glTexCoord2f(g.u0, g.v0);
                    GL11.glVertex3f(xx + (this.italic ? 4f : 0), y - 3, 0);
                    GL11.glTexCoord2f(g.u0, g.v1);
                    GL11.glVertex3f(xx, y + 12 - 3, 0);
                    GL11.glTexCoord2f(g.u1, g.v1);
                    GL11.glVertex3f(xx + 8, y + 12 - 3, 0);
                    GL11.glTexCoord2f(g.u1, g.v0);
                    GL11.glVertex3f(xx + 8 + (this.italic ? 4f : 0), y - 3, 0);
                }
            }

            xx += 8;
        }

        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL11.GL_BLEND);
    }

    record Glyph(float u0, float v0, float u1, float v1, int width) {
    }
}
