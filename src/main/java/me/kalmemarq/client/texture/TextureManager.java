package me.kalmemarq.client.texture;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    private final Map<String, Texture> textureMap = new HashMap<>();

    public TextureManager() {
    }

    public Map<String, Texture> getTextures() {
        return this.textureMap;
    }

    public void load(String texture) {
        Texture txr = new Texture(texture);
        txr.load();
        this.textureMap.put(texture, txr);
    }

    public int get(String texture) {
        return this.textureMap.get(texture).getId();
    }

    public void bind(String path) {
        Texture texture = this.textureMap.get(path);
        if (texture == null) {
            texture = new Texture(path);
            texture.load();
            System.out.println("Texture " + path + " has been loaded");
            this.textureMap.put(path, texture);
        }
        texture.bind();
    }

    public void close() {
        this.textureMap.values().forEach(t -> {
            System.out.println("Texture " + t.getPath() + " has been freed");
            t.close();
        });
        this.textureMap.clear();
    }
}
