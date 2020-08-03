#vertex
#version 450 core

layout(location = 0) in vec3 a_Position;

uniform mat4 viewMatrix;
uniform mat4 projMatrix;

out vec2 v_TexCoord;
out vec3 worldPosition;

void main() {
    v_TexCoord = a_Position.xz / 4;

    worldPosition = a_Position;
    gl_Position = projMatrix * viewMatrix * vec4(worldPosition, 1.0f);
}

#geometry
#version 450 core

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

uniform vec3 cameraPos;

in vec3 worldPosition[];
in vec2 texCoord[];

out mat3 normalMatrix;
out vec3 cameraDirection;
out vec3 lightDirection;
out vec2 v_TexCoord;

void main() {
    vec3 normal = -normalize(cross(worldPosition[1] - worldPosition[0], worldPosition[2] - worldPosition[0]));

    vec3 edge1 = worldPosition[1] - worldPosition[0];
    vec3 edge2 = worldPosition[2] - worldPosition[0];
    vec2 deltaUV1 = texCoord[1] - texCoord[0];
    vec2 deltaUV2 = texCoord[2] - texCoord[0];

    float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);

    vec3 tangent, bitangent;

    tangent.x = f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x);
    tangent.y = f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y);
    tangent.z = f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z);

    bitangent.x = f * (-deltaUV2.x * edge1.x + deltaUV1.x * edge2.x);
    bitangent.y = f * (-deltaUV2.x * edge1.y + deltaUV1.x * edge2.y);
    bitangent.z = f * (-deltaUV2.x * edge1.z + deltaUV1.x * edge2.z);

    mat3 normalTranslation = mat3(tangent, bitangent, normal);

    normalMatrix = normalTranslation;
    gl_Position = gl_in[0].gl_Position;
    cameraDirection = normalize(cameraPos - worldPosition[0]);
    lightDirection = normalize(vec3(0, 100, 0) - worldPosition[0]);
    v_TexCoord = texCoord[0];
    EmitVertex();

    normalMatrix = normalTranslation;
    gl_Position = gl_in[1].gl_Position;
    cameraDirection = normalize(cameraPos - worldPosition[1]);
    lightDirection = normalize(vec3(0, 100, 0) - worldPosition[1]);
    v_TexCoord = texCoord[1];
    EmitVertex();

    normalMatrix = normalTranslation;
    gl_Position = gl_in[2].gl_Position;
    cameraDirection = normalize(cameraPos - worldPosition[2]);
    lightDirection = normalize(vec3(0, 100, 0) - worldPosition[2]);
    v_TexCoord = texCoord[2];
    EmitVertex();
}

#fragment
#version 450 core

layout(location = 0) out vec4 o_Color;

uniform vec4 in_Color;

uniform sampler2D u_Texture;
uniform sampler2D u_NormalMap;

in vec2 v_TexCoord;

in mat3 normalMatrix;
in vec3 cameraDirection;
in vec3 lightDirection;

void main() {
    vec4 texColor = texture(u_Texture, v_TexCoord);
    vec3 normal = texture(u_NormalMap, v_TexCoord).xyz * normalMatrix;
    float highLight = pow(dot(cameraDirection, reflect(-lightDirection, normal)) / 2 + 0.5f, 20);
    float light = (dot(normal, lightDirection) / 2 + 0.5f) * 0.4f;

    // TODO better blending
    //o_Color = in_Color.w == 1f ? max(min(vec4(texColor.x + light + highLight, texColor.y + light + highLight, texColor.z + light + highLight, texColor.w), 1), 0) : vec4((in_Color.xyz + texColor.xyz) / 2, in_Color.w);
    o_Color = vec4(texColor.xyz, 1f);
}