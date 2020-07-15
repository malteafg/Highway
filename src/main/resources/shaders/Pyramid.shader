#vertex
#version 450 core

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec4 a_Color;

uniform mat4 u_MVP;
uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 perspectiveMatrix;

out vec4 v_Color;

void main() {
    v_Color = a_Color;
    vec4 worldPos = transformationMatrix * vec4(a_Position, 1.0);
    gl_Position = perspectiveMatrix * viewMatrix * worldPos;
}

#fragment
#version 450 core

layout(location = 0) out vec4 o_Color;

in vec4 v_Color;

void main() {
    o_Color = v_Color;
}