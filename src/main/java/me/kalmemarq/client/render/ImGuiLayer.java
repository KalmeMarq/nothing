package me.kalmemarq.client.render;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import me.kalmemarq.client.Client;
import me.kalmemarq.client.screen.TitleScreen;
import me.kalmemarq.client.texture.Texture;
import me.kalmemarq.network.packet.MessagePacket;
import org.lwjgl.glfw.GLFW;

import java.time.Instant;
import java.util.Map;

public class ImGuiLayer {
    private ImGuiImplGlfw imGuiGlfw;
    private ImGuiImplGl3 imGuiGl3;

    private final Client client;
    private ImBoolean showTexturesWindow = new ImBoolean(false);
    private ImBoolean showSoundsWindow = new ImBoolean(false);
    private ImBoolean showAboutWindow = new ImBoolean(false);
    private ImBoolean showMessagesWindow = new ImBoolean(false);
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
                if (ImGui.menuItem("Open Title")) {
                    this.client.screen = new TitleScreen(this.client);
                }

                if (ImGui.menuItem("Messages")) {
                    this.showMessagesWindow.set(true);
                }

                if (this.client.player != null) {
                    ImGui.text("Player X: " + this.client.player.x);
                    ImGui.text("Player Y: " + this.client.player.y);
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
                    for (Map.Entry<String, Texture> entry : this.client.textureManager.getTextures().entrySet()) {
                        Texture txr = entry.getValue();
                        ImGui.image(txr.getId(), txr.getWidth(), txr.getHeight());

                        if (ImGui.isItemHovered()) {
                            ImGui.beginTooltip();
                            ImGui.text(entry.getKey());
                            ImGui.endTooltip();
                        }
                    }
                }
                ImGui.end();
            }

            if (this.showSoundsWindow.get()) {
                if (ImGui.begin("Sounds", this.showSoundsWindow)) {
                }
                ImGui.end();
            }

            if (this.showAboutWindow.get()) {
                if (ImGui.begin("About", this.showAboutWindow)) {
                    ImGui.text("Name: Minicraft");
                    ImGui.text("Version: 1.0.0");

                    ImGui.newLine();

                    ImGui.text("OS: " + System.getProperty("os.name"));
                }
                ImGui.end();
            }

            ImGui.popStyleColor();
        }
        ImGui.endMainMenuBar();

        this.endImGuiFrame();
    }
}