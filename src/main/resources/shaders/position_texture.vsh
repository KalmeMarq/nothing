#version 330

uniform mat4 uProjectionMat;
uniform mat4 uModelViewMat;

in vec3 aPosition;
in vec4 aUV0;

out vec4 vUV0;

void main() {
    gl_Position = uProjectionMat * uModelViewMat * vec4(aPosition, 1.0);
    vUV0 = aUV0;
}