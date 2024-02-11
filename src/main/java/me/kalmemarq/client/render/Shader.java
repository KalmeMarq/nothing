package me.kalmemarq.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.kalmemarq.Utils;
import me.kalmemarq.client.resource.DefaultResourcePack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class Shader {
    private static final Logger LOGGER = LoggerFactory.getLogger(Shader.class);
    private int id;
    private final Map<String, Integer> uniformLocations = new HashMap<>();
    private IntBuffer uniformIntBuffer;
    private FloatBuffer uniformFloatBuffer;

    public Shader(String name) {
        this.id = GL20.glCreateProgram();

        int verShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        int fragShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        var rp = DefaultResourcePack.get();

        try {
            var source = Utils.readString(rp.get("shaders/" + name + ".vsh").get().get());
            GL20.glShaderSource(verShader, source);
        } catch (IOException e) {
            LOGGER.info("Could not set vertex shader source", e);
            GL20.glDeleteShader(verShader);
            GL20.glDeleteShader(fragShader);
            GL20.glDeleteProgram(this.id);
            return;
        }

        try {
            var source = Utils.readString(rp.get("shaders/" + name + ".fsh").get().get());
            GL20.glShaderSource(fragShader, source);
        } catch (IOException e) {
            LOGGER.info("Could not set fragment shader source", e);
            GL20.glDeleteShader(verShader);
            GL20.glDeleteShader(fragShader);
            GL20.glDeleteProgram(this.id);
            return;
        }

        GL20.glCompileShader(verShader);

        if (GL20.glGetShaderi(verShader, GL20.GL_COMPILE_STATUS) != GL20.GL_TRUE) {
            LOGGER.info("Failed to compile vertex shader: {}", GL20.glGetShaderInfoLog(verShader));
        }

        GL20.glCompileShader(fragShader);

        if (GL20.glGetShaderi(fragShader, GL20.GL_COMPILE_STATUS) != GL20.GL_TRUE) {
            LOGGER.info("Failed to compile fragment shader: {}", GL20.glGetShaderInfoLog(fragShader));
        }

        GL20.glAttachShader(this.id, verShader);
        GL20.glAttachShader(this.id, fragShader);

        GL20.glLinkProgram(this.id);

        if (GL20.glGetProgrami(this.id, GL20.GL_LINK_STATUS) != GL20.GL_TRUE) {
            LOGGER.info("Failed to link program: {}", GL20.glGetProgramInfoLog(this.id));
        }

        GL20.glDetachShader(this.id, verShader);
        GL20.glDetachShader(this.id, fragShader);

        GL20.glDeleteShader(verShader);
        GL20.glDeleteShader(fragShader);

        this.uniformIntBuffer = MemoryUtil.memAllocInt(4);
        this.uniformFloatBuffer = MemoryUtil.memAllocFloat(16);

        try {
            JsonObject obj = Utils.GSON.fromJson(Utils.readString(rp.get("shaders/" + name + ".json").get().get()), JsonObject.class);
            JsonArray arr = obj.getAsJsonArray("uniforms");

            for (JsonElement itemel : arr) {
                JsonObject itemobj = itemel.getAsJsonObject();
                String uniName = itemobj.get("name").getAsString();
                int location = GL30.glGetUniformLocation(this.id, uniName);
                System.out.println(uniName + ";" + location);
                this.uniformLocations.put(uniName, location);
            }
        } catch (IOException e) {
            LOGGER.info("Failed to load shader config", e);
        }
    }

    public int getId() {
        return this.id;
    }

    public Map<String, Integer> getUniformLocations() {
        return this.uniformLocations;
    }

    public int getUniformLocation(String name) {
        int location;
        if (!this.uniformLocations.containsKey(name)) {
            location = GL20.glGetUniformLocation(this.id, name);
            this.uniformLocations.put(name, location);
        } else {
            location = this.uniformLocations.get(name);
        }
        return location;
    }

    public void setUniform(String name, int value) {
        GL20.glUniform1i(this.getUniformLocation(name), value);
    }

    public void setUniform(String name, float value) {
        GL20.glUniform1f(this.getUniformLocation(name), value);
    }

    public void setUniform(String name, int value0, int value1) {
        GL20.glUniform2i(this.getUniformLocation(name), value0, value1);
    }

    public void setUniform(String name, float value0, float value1) {
        GL20.glUniform2f(this.getUniformLocation(name), value0, value1);
    }

    public void setUniform(String name, int value0, int value1, int value2) {
        GL20.glUniform3i(this.getUniformLocation(name), value0, value1, value2);
    }

    public void setUniform(String name, float value0, float value1, float value2) {
        GL20.glUniform3f(this.getUniformLocation(name), value0, value1, value2);
    }

    public void setUniform(String name, int value0, int value1, int value2, int value3) {
        GL20.glUniform4i(this.getUniformLocation(name), value0, value1, value2, value3);
    }

    public void setUniform(String name, float value0, float value1, float value2, float value3) {
        GL20.glUniform4f(this.getUniformLocation(name), value0, value1, value2, value3);
    }

    public void setUniform(String name, Matrix4f value) {
        this.uniformFloatBuffer.position(0);
        value.get(this.uniformFloatBuffer);
        this.uniformFloatBuffer.flip();
    }

    public void close() {
        MemoryUtil.memFree(this.uniformIntBuffer);
        MemoryUtil.memFree(this.uniformFloatBuffer);
        GL20.glDeleteProgram(this.id);
    }
}
