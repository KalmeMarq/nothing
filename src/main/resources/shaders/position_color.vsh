#version 330

uniform mat4 uProjectionMat;
uniform mat4 uModelViewMat;

in vec3 aPosition;
in vec4 aColor;

out vec4 vColor;

void main() {
    gl_Position = uProjectionMat * uModelViewMat * vec4(aPosition, 1.0);
    vColor = aColor;
}