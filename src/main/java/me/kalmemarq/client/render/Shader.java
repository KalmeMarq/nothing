package me.kalmemarq.client.render;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.kalmemarq.common.Utils;
import me.kalmemarq.client.resource.DefaultResourcePack;
import me.kalmemarq.common.logging.LogManager;
import me.kalmemarq.common.logging.Logger;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class Shader {
    protected static final Logger LOGGER = LogManager.getLogger(Shader.class);
    private final int id;
    private final Map<String, Integer> uniformLocations = new HashMap<>();
	private final Map<String, Uniform> uniforms = new HashMap<>();
    private IntBuffer uniformIntBuffer;
    private FloatBuffer uniformFloatBuffer;
	public Uniform projectMatUniform;
	public Uniform modelViewMatUniform;
	public Uniform colorUniform;

    public Shader(String name) {
        this.id = GL20.glCreateProgram();

        int verShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        int fragShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        var rp = DefaultResourcePack.get();

        try {
            var source = Utils.readString(rp.getResource("assets/minicraft/shaders/" + name + ".vsh").get().inputSupplier().get());
            GL20.glShaderSource(verShader, source);
        } catch (IOException e) {
            LOGGER.info("Could not set vertex shader source", e);
            GL20.glDeleteShader(verShader);
            GL20.glDeleteShader(fragShader);
            GL20.glDeleteProgram(this.id);
            return;
        }

        try {
            var source = Utils.readString(rp.getResource("assets/minicraft/shaders/" + name + ".fsh").get().inputSupplier().get());
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
            var obj = Utils.OBJECT_MAPPER.readValue(Utils.readString(rp.getResource("assets/minicraft/shaders/" + name + ".json").get().inputSupplier().get()), ObjectNode.class);
            var arr = obj.get("uniforms");

            for (JsonNode itemel : arr) {
                ObjectNode itemobj = (ObjectNode) itemel;
                String uniName = itemobj.get("name").textValue();
                String uniType = itemobj.get("type").textValue();
                int location = GL30.glGetUniformLocation(this.id, uniName);
                System.out.println(uniName + ";" + location);
                this.uniformLocations.put(uniName, location);
				
				this.uniforms.put(uniName, new Uniform(uniName, location, Uniform.DataType.getById(uniType)));
            }
        } catch (IOException e) {
            LOGGER.info("Failed to load shader config", e);
        }
		
		this.projectMatUniform = this.uniforms.get("uProjectionMat");
		this.modelViewMatUniform = this.uniforms.get("uModelViewMat");
		this.colorUniform = this.uniforms.get("uColor");
    }

    public int getId() {
        return this.id;
    }

    public Map<String, Integer> getUniformLocations() {
        return this.uniformLocations;
    }

	public Map<String, Uniform> getUniforms() {
		return this.uniforms;
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

    public void setSample(String name, int value) {
        GL20.glUniform1i(this.getUniformLocation(name), value);
    }

    public void close() {
		this.uniforms.values().forEach(Uniform::close);
		
        MemoryUtil.memFree(this.uniformIntBuffer);
        MemoryUtil.memFree(this.uniformFloatBuffer);
        GL20.glDeleteProgram(this.id);
    }
}
