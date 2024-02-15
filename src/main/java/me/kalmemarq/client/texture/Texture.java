package me.kalmemarq.client.texture;

import me.kalmemarq.common.Identifier;
import me.kalmemarq.client.Client;
import me.kalmemarq.client.resource.DefaultResourcePack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture {
    private final Identifier identifier;
    private int handle = -1;
    private int width;
    private int height;

    public Texture(Identifier identifier) {
        this.identifier = identifier;
    }

    public int getId() {
        return this.handle;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

	public Identifier getIdentifier() {
		return this.identifier;
	}

	public void load() {
        var rp = DefaultResourcePack.get();

        try {
            ByteBuffer data = Client.getByteBufferFromInputStream(rp.getResource(this.identifier).get().inputSupplier().get());
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer widthP = stack.mallocInt(1);
                IntBuffer heightP = stack.mallocInt(1);
                IntBuffer channelsP = stack.mallocInt(1);

                ByteBuffer imageData = STBImage.stbi_load_from_memory(data, widthP, heightP, channelsP, 4);

                this.width = widthP.get(0);
                this.height = heightP.get(0);

                this.bind();
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageData);
                STBImage.stbi_image_free(imageData);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
            }
            MemoryUtil.memFree(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void bind() {
        if (this.handle == -1) {
            this.handle = GL11.glGenTextures();
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.handle);
    }

    public void close() {
        if (this.handle != -1) {
            GL11.glDeleteTextures(this.handle);
            this.handle = -1;
        }
    }
}
