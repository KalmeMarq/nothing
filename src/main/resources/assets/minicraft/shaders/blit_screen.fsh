#version 330

uniform vec4 uColor;

uniform sampler2D Sampler0;

in vec2 vUV0;

out vec4 fragColor;

void main() {
    fragColor = texture2D(Sampler0, vUV0) * uColor;
}
