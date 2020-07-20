#vertex
#version 450 core

layout(location = 0) in vec3 a_Position;

uniform mat4 viewMatrix;
uniform mat4 projMatrix;

void main() {
    vec4 worldPos = vec4(a_Position, 1.0);
    gl_Position = projMatrix * viewMatrix * worldPos;
}

#fragment
#version 450 core

layout(location = 0) out vec4 o_Color;

uniform vec4 in_Color;

void main() {
    o_Color = in_Color;
}