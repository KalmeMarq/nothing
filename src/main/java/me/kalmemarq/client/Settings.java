package me.kalmemarq.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import me.kalmemarq.common.Utils;
import me.kalmemarq.common.logging.LogManager;
import me.kalmemarq.common.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Settings {
	private static final Logger LOGGER = LogManager.getLogger(Settings.class);
    
	private final Path optionsPath;
    public int soundVolume = 100;
	public String username = "Player";
    public int playerColorR = 255;
    public int playerColorG = 255;
    public int playerColorB = 255;

    public Settings(Path savePath) {
        this.optionsPath = savePath.resolve("settings.json");
    }

    public void load() {
        if (!Files.exists(this.optionsPath)) {
            return;
        }
		
        try {
			var obj = Utils.OBJECT_MAPPER.readValue(Files.readString(this.optionsPath), ObjectNode.class);
            
			if (obj.has("username")) {
				this.username = obj.get("username").textValue();
			}

			if (obj.has("playerColorR")) {
				this.playerColorR = obj.get("playerColorR").asInt(255);
			}

			if (obj.has("playerColorG")) {
				this.playerColorG = obj.get("playerColorG").asInt(255);
			}

			if (obj.has("playerColorB")) {
				this.playerColorB = obj.get("playerColorB").asInt(255);
			}

			if (obj.has("soundVolume")) {
				this.soundVolume = obj.get("soundVolume").asInt(255);
			}
		} catch (IOException e) {
			LOGGER.warn("Failed to load settings from disk", e);
        }
    }

    public void save() {
        ObjectNode obj = Utils.OBJECT_MAPPER.createObjectNode();
		obj.put("version", 1);
		obj.put("username", this.username);
		obj.put("playerColorR", this.playerColorR);
		obj.put("playerColorG", this.playerColorG);
		obj.put("playerColorB", this.playerColorB);
		obj.put("soundVolume", this.soundVolume);

        try {
            Files.writeString(this.optionsPath, obj.toPrettyString());
        } catch (IOException e) {
			LOGGER.warn("Failed to save settings to disk", e);
        }
    }
}
