package me.kalmemarq;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class Utils {
    public static final Gson GSON = new GsonBuilder().setLenient().create();

    private Utils() {
    }

	/**
	 * Clamps the given value between the lower and upper bound
	 * @param value value to clamp
	 * @param min lower bound
	 * @param max upper bound
	 * @return clamped value
	 */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

	/**
	 * Read an {@link InputStream} as a string
	 * @param inputStream inputStream to read
	 * @return the string content of the {@link InputStream}
	 */
    public static String readString(InputStream inputStream) {
        StringBuilder builder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            builder.deleteCharAt(builder.lastIndexOf("\n"));
        } catch (IOException e) {
        }

        return builder.toString();
    }

    public static int packARGB(float[] src) {
        int color = (int)(src[0] * 255.0f) << 16 | (int)(src[1] * 255.0f) << 8 | (int)(src[2] * 255.0f);
        if (src.length == 4) color |= (int)(src[3] * 255.0f) << 24;
        return color;
    }

    public static void unpackARGB(int color, float[] dst) {
        dst[0] = (color >> 16 & 0xFF) / 255.0f;
        dst[1] = (color >> 8 & 0xFF) / 255.0f;
        dst[2] = (color & 0xFF) / 255.0f;

        if (dst.length == 4) {
            dst[3] = (color >> 24 & 0xFF) / 255.0f;
        }
    }

    public static void unpackARGB(int color, int[] dst) {
        dst[0] = color >> 16 & 0xFF;
        dst[1] = color >> 8 & 0xFF;
        dst[2] = color & 0xFF;

        if (dst.length == 4) {
            dst[3] = color >> 24 & 0xFF;
        }
    }
}
