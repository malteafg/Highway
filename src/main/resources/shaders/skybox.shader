#vertex
#version 450 core

layout(location = 0) in vec3 a_Position;

uniform mat4 viewMatrix;
uniform mat4 projMatrix;
uniform mat4 transformationMatrix;

out vec3 texCoord;

void main() {
    vec4 worldPos = transformationMatrix * vec4(a_Position, 1.0);
    gl_Position = projMatrix * viewMatrix * worldPos;
    texCoord = a_Position;
}

#fragment
#version 450 core

layout(location = 0) out vec4 o_Color;

in vec3 texCoord;

uniform samplerCube cubemap;

void main() {
    o_Color = texture(cubemap, texCoord);
}