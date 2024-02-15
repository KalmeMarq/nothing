package me.kalmemarq.client;

import me.kalmemarq.common.Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Settings {
    private final Path optionsPath;
    public String username = "Player";
    public int playerColorR = 255;
    public int playerColorG = 255;
    public int playerColorB = 255;

    public Settings(Path savePath) {
        this.optionsPath = savePath.resolve("settings.txt");
    }

    public void load() {
        if (!Files.exists(this.optionsPath)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(this.optionsPath)) {
                String[] lineData = line.trim().split("=");
                if (lineData.length != 2) continue;

                if ("username".equals(lineData[0])) {
                    this.username = lineData[1];
                }

                if ("playerColorR".equals(lineData[0])) {
                    this.playerColorR = Utils.clamp(Integer.parseInt(lineData[1]), 0, 255);
                }

                if ("playerColorG".equals(lineData[0])) {
                    this.playerColorG = Utils.clamp(Integer.parseInt(lineData[1]), 0, 255);
                }

                if ("playerColorB".equals(lineData[0])) {
                    this.playerColorB = Utils.clamp(Integer.parseInt(lineData[1]), 0, 255);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(this.optionsPath)) {
            writer.append("username=" + this.username); writer.newLine();
            writer.append("playerColorR=" + this.playerColorR); writer.newLine();
            writer.append("playerColorG=" + this.playerColorG); writer.newLine();
            writer.append("playerColorB=" + this.playerColorB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
