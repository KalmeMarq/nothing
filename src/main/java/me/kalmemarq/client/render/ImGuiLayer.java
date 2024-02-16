package me.kalmemarq.client.render;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import me.kalmemarq.common.Identifier;
import me.kalmemarq.client.Client;
import me.kalmemarq.client.screen.TitleMenu;
import me.kalmemarq.client.texture.Texture;
import me.kalmemarq.common.network.packet.MessagePacket;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GLCapabilities;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImGuiLayer {
    private ImGuiImplGlfw imGuiGlfw;
    private ImGuiImplGl3 imGuiGl3;

    private final Client client;
    private ImBoolean showTexturesWindow = new ImBoolean(false);
    private ImBoolean showSoundsWindow = new ImBoolean(false);
    private ImBoolean showAboutWindow = new ImBoolean(false);
    private ImBoolean showMessagesWindow = new ImBoolean(false);
    private ImBoolean showGameInfoWindow = new ImBoolean(false);
	private ImBoolean showRendererInfoWindow = new ImBoolean(false);
    private ImString messageInput = new ImString();

    public ImGuiLayer(Client client) {
        this.client = client;
    }

    private void initImGui() {
        this.imGuiGlfw = new ImGuiImplGlfw();
        this.imGuiGl3 = new ImGuiImplGl3();
        ImGui.createContext();
        this.imGuiGlfw.init(this.client.window.getHandle(), true);
        this.imGuiGl3.init("#version 150");
    }

    public void beginImGuiFrame() {
        this.imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void endImGuiFrame() {
        ImGui.render();
        this.imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    public void close() {
       if (this.imGuiGlfw != null) {
           this.imGuiGl3.dispose();
           this.imGuiGlfw.dispose();
           ImGui.destroyContext();
       }
    }

    public void render() {
        if (this.imGuiGlfw == null) {
            this.initImGui();
        }

        this.beginImGuiFrame();

        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Quit")) {
                    this.client.shutdown();
                }

                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Game")) {
                if (ImGui.menuItem("Game Info")) {
					this.showGameInfoWindow.set(true);
                }
				
				if (ImGui.menuItem("Renderer Info")) {
					this.showRendererInfoWindow.set(true);
                }

                if (ImGui.menuItem("Messages")) {
                    this.showMessagesWindow.set(true);
                }

                if (this.client.player != null) {
                    ImGui.text("Player X: " + this.client.player.x);
                    ImGui.text("Player Y: " + this.client.player.y);
                }
				
				if (this.client.menu != null) {
					ImGui.text("Screen: " + this.client.menu.getClass().getSimpleName());
				}

                ImGui.endMenu();
            }

            if (ImGui.menuItem("Textures")) {
                this.showTexturesWindow.set(true);
            }

            if (ImGui.menuItem("Sounds")) {
                this.showSoundsWindow.set(true);
            }

            if (ImGui.menuItem("About")) {
                this.showAboutWindow.set(true);
            }

            ImGui.pushStyleColor(ImGuiCol.ResizeGrip, 0);

            if (this.showMessagesWindow.get()) {
                if (ImGui.begin("Messages", this.showMessagesWindow)) {
                    if (ImGui.button("Clear messages")) {
                        this.client.messages.clear();
                    }

                    ImGui.beginChild("##messagearea", ImGui.getContentRegionAvailX(), ImGui.getContentRegionAvailY() - 24);
                    for (int i = this.client.messages.size() - 1; i >= 0; --i) {
                        ImGui.text(this.client.messages.get(i));
                    }
                    ImGui.endChild();

                    ImGui.setNextItemWidth(ImGui.getContentRegionAvailX());

                    if (ImGui.inputText("##messageinput", messageInput, ImGuiInputTextFlags.EnterReturnsTrue)) {
                        var input = messageInput.get().trim();
                        if (!input.isEmpty() && this.client.connection != null) {
                            this.client.connection.sendPacket(new MessagePacket(input, Instant.now()));
                            messageInput.set("");
                        }
                    }
                }
                ImGui.end();
            }

            if (this.showTexturesWindow.get()) {
                if (ImGui.begin("Textures", this.showTexturesWindow)) {
                    for (Map.Entry<Identifier, Texture> entry : this.client.textureManager.getTextures().entrySet()) {
                        Texture txr = entry.getValue();
                        ImGui.image(txr.getId(), txr.getWidth(), txr.getHeight());

                        if (ImGui.isItemHovered()) {
                            ImGui.beginTooltip();
                            ImGui.text(entry.getKey().toString());
                            ImGui.endTooltip();
                        }
                    }
                }
                ImGui.end();
            }

            if (this.showSoundsWindow.get()) {
                if (ImGui.begin("Sounds", this.showSoundsWindow)) {
					ImGui.text("Current Device: " + this.client.getSoundManager().getCurrentDevice().substring(15));
					
					ImGui.text("Devices available:");
					for (String device : this.client.getSoundManager().getAllDevicesAvailable()) {
						ImGui.text(device.substring(15));
					}
					
					for (Map.Entry<Identifier, Integer> entry : this.client.getSoundManager().getBuffers().entrySet()) {
						ImGui.text(entry.getValue() + ": " + entry.getKey());
					}
                }
                ImGui.end();
            }

            if (this.showAboutWindow.get()) {
                if (ImGui.begin("About", this.showAboutWindow)) {
                    ImGui.text("Name: Minicraft Not Plus");
                    ImGui.text("Version: 1.0.0");

                    ImGui.newLine();

                    ImGui.text("OS: " + System.getProperty("os.name"));
                }
                ImGui.end();
            }

			if (this.showGameInfoWindow.get()) {
				if (ImGui.begin("Game Info", this.showGameInfoWindow)) {
					ImGui.text("Window Size: " + this.client.window.getWidth() + "x" + this.client.window.getHeight());
					ImGui.text("Window Framebuffer Size: " + this.client.window.getFramebufferWidth() + "x" + this.client.window.getFramebufferHeight());
					ImGui.text("FPS: " + this.client.currentFps);
					ImGui.text("TKS: " + this.client.currentTicks);
				}
				ImGui.end();
			}

			if (this.showRendererInfoWindow.get()) {
				if (ImGui.begin("Renderer", this.showRendererInfoWindow)) {
					ImGui.text("Vertex Buffers: " + this.client.renderer.getVertexBuffers().size());
					ImGui.text("Buffer Builders: " + this.client.renderer.getBufferBuilders().size());

					ImGui.newLine();
					ImGui.text("Blit Screen Shader");
					ImGui.text("Uniforms");
					for (Uniform uniform : this.client.blitScreenShader.getUniforms().values()) {
						ImGui.text(uniform.getName() + ": " + uniform.getLocation());
					}
					
					ImGui.newLine();
					ImGui.text("GL Capabilities");
					ImGui.newLine();
					GLCapabilities caps = Renderer.getInstance().getCapabilities();
					
					List<Field> allFields = Arrays.stream(caps.getClass().getDeclaredFields()).filter(field -> field.getType() != long.class).toList();
					
					for (Field field : allFields) {
                        try {
                            ImGui.text(field.getName() + ": " + field.get(caps));
                        } catch (IllegalAccessException e) {
                        }
                    }
				}
				ImGui.end();
			}
			
            ImGui.popStyleColor();
        }
        ImGui.endMainMenuBar();

        this.endImGuiFrame();
    }
}
