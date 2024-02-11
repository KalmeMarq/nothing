#version 330

uniform mat4 uColor;

uniform sampler2D Sampler0;

in vec4 vUV0;

out vec4 fragColor;

void main() {
    fragColor = texture(Sampler0, vUV0) * uColor;
}