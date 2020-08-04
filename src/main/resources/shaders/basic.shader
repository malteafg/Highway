#vertex
#version 450 core

layout(location = 0) in vec3 a_Position;

out vec3 worldPosition;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projMatrix;

void main() {
    vec4 worldPos = transformationMatrix * vec4(a_Position, 1.0);
    worldPosition = worldPos.xyz;
    gl_Position = projMatrix * viewMatrix * worldPos;
}

#fragment
#version 450 core

layout(location = 0) out vec4 o_Color;

uniform vec4 in_Color;

void main() {
    o_Color = in_Color;
}