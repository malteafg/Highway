#vertex
#version 450 core

layout(location = 0) in vec3 a_Position;

out vec2 v_TexCoord;
out vec3 worldPosition;

uniform mat4 viewMatrix;
uniform mat4 projMatrix;

void main() {
    v_TexCoord = a_Position.xz / 4;

    worldPosition = worldPos.a_Position;
    gl_Position = projMatrix * viewMatrix * vec4(worldPos, 1.0f);
}

#geometry
#version 450 core

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in vec3 worldPosition[];
in vec2 texCoord[];

out vec3 faceNormal;
out vec3 cameraDirection;
out vec3 lightDirection;
out vec2 v_TexCoord;

uniform vec3 cameraPos;

void main() {
    vec3 normal = -normalize(cross(worldPosition[1] - worldPosition[0], worldPosition[2] - worldPosition[0]));

    faceNormal = normal;
    gl_Position = gl_in[0].gl_Position;
    cameraDirection = normalize(cameraPos - worldPosition[0]);
    lightDirection = normalize(vec3(0, 100, 0) - worldPosition[0]);
    v_TexCoord = texCoord[0];
    EmitVertex();

    faceNormal = normal;
    gl_Position = gl_in[1].gl_Position;
    cameraDirection = normalize(cameraPos - worldPosition[1]);
    lightDirection = normalize(vec3(0, 100, 0) - worldPosition[1]);
    v_TexCoord = texCoord[1];
    EmitVertex();

    faceNormal = normal;
    gl_Position = gl_in[2].gl_Position;
    cameraDirection = normalize(cameraPos - worldPosition[2]);
    lightDirection = normalize(vec3(0, 100, 0) - worldPosition[2]);
    v_TexCoord = texCoord[2];
    EmitVertex();
}

#fragment
#version 450 core

layout(location = 0) out vec4 o_Color;

in vec2 v_TexCoord;

in vec3 faceNormal;
in vec3 cameraDirection;
in vec3 lightDirection;

uniform vec4 in_Color;

uniform sampler2D u_Texture;
uniform sampler2D u_NormalMap;

void main() {
    vec4 texColor = texture(u_Texture, v_TexCoord);
    // TODO better blending
    o_Color = in_Color.w == 1f ? texColor : vec4((in_Color.xyz + texColor.xyz) / 2, in_Color.w);
}