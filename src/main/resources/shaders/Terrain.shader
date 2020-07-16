#vertex
#version 450 core

layout(location = 0) in vec3 a_Position;

uniform mat4 viewMatrix;
uniform mat4 projMatrix;

void main() {
    gl_Position = projMatrix * viewMatrix * vec4(a_Position, 1.0f);
}

#fragment
#version 450 core

layout(location = 0) out vec4 o_Color;

void main() {
    o_Color = vec4(0.29f, 0.61f, 0.24f, 1.0f);
}