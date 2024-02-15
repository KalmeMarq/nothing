package me.kalmemarq.client;

import com.google.gson.JsonObject;
import me.kalmemarq.common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Settings {
	private static final Logger LOGGER = LoggerFactory.getLogger(Settings.class);
    
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
			JsonObject obj = Utils.GSON.fromJson(Files.readString(this.optionsPath), JsonObject.class);
            
			if (obj.has("username")) {
				this.username = obj.get("username").getAsString();
			}

			if (obj.has("playerColorR")) {
				this.playerColorR = obj.get("playerColorR").getAsInt();
			}

			if (obj.has("playerColorG")) {
				this.playerColorG = obj.get("playerColorG").getAsInt();
			}

			if (obj.has("playerColorB")) {
				this.playerColorB = obj.get("playerColorB").getAsInt();
			}

			if (obj.has("soundVolume")) {
				this.soundVolume = obj.get("soundVolume").getAsInt();
			}
		} catch (IOException e) {
			LOGGER.warn("Failed to load settings from disk", e);
        }
    }

    public void save() {
        JsonObject obj = new JsonObject();
		obj.addProperty("version", 1);
		obj.addProperty("username", this.username);
		obj.addProperty("playerColorR", this.playerColorR);
		obj.addProperty("playerColorG", this.playerColorG);
		obj.addProperty("playerColorB", this.playerColorB);
		obj.addProperty("soundVolume", this.soundVolume);

        try {
            Files.writeString(this.optionsPath, Utils.GSON.toJson(obj));
        } catch (IOException e) {
			LOGGER.warn("Failed to save settings to disk", e);
        }
    }
}
