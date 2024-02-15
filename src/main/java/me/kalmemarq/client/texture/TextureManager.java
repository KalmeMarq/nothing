package me.kalmemarq.client.texture;

import me.kalmemarq.Identifier;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    private final Map<Identifier, Texture> textureMap = new HashMap<>();

    public TextureManager() {
    }

    public Map<Identifier, Texture> getTextures() {
        return this.textureMap;
    }

    public void load(Identifier texture) {
        Texture txr = new Texture(texture);
        txr.load();
        this.textureMap.put(texture, txr);
    }

    public int get(String texture) {
        return this.textureMap.get(texture).getId();
    }

    public void bind(Identifier path) {
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
            System.out.println("Texture " + t.getIdentifier() + " has been freed");
            t.close();
        });
        this.textureMap.clear();
    }
}
