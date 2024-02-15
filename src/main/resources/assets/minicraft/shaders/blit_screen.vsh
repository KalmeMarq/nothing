#version 330

in vec3 aPosition;
in vec2 aUV0;

out vec2 vUV0;

void main() {
    gl_Position = vec4(aPosition, 1.0);
    vUV0 = aUV0;
}