package me.kalmemarq.client.render;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kalmemarq.Utils;
import me.kalmemarq.client.resource.DefaultResourcePack;
import me.kalmemarq.client.texture.TextureManager;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Font {

    private TextureManager textureManager;
    private Map<Character, Glyph> glyphMap = new HashMap<>();

    public Font(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void load() {
        var rp = DefaultResourcePack.get();
        try {
            JsonObject obj = Utils.GSON.fromJson(Utils.readString(rp.get("font.json").get().get()), JsonObject.class);
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

//        for (Map.Entry<Character, Glyph> glyph : this.glyphMap.entrySet()) {
//            Glyph info = glyph.getValue();
//            System.out.println(glyph.getKey() + " {u0=" + info.u0 + ",v0=" + info.v0 + ",u1=" + info.u1 + ",v1=" + info.v1 + "}");
//        }
    }

    public void drawText(String text, int x, int y, int color) {
        if (text == null || text.isEmpty()) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.textureManager.bind("font.png");
        GL11.glBegin(GL11.GL_QUADS);

        char[] charr = text.toCharArray();
        float[] colorarr = new float[3];
        Utils.unpackARGB(color, colorarr);
        GL11.glColor4f(colorarr[0], colorarr[1], colorarr[2], 1.0f);
        int xx = x;
        for (int i = 0; i < charr.length; ++i) {
            char c = charr[i];

            if (!Character.isWhitespace(c)) {
                Glyph g = this.glyphMap.get(c);

                if (g != null) {

                    GL11.glTexCoord2f(g.u0, g.v0);
                    GL11.glVertex3f(xx, y - 3, 0);
                    GL11.glTexCoord2f(g.u0, g.v1);
                    GL11.glVertex3f(xx, y + 12 - 3, 0);
                    GL11.glTexCoord2f(g.u1, g.v1);
                    GL11.glVertex3f(xx + 8, y + 12 - 3, 0);
                    GL11.glTexCoord2f(g.u1, g.v0);
                    GL11.glVertex3f(xx + 8, y - 3, 0);
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
