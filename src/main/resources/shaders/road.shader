#vertex
#version 450 core

layout(location = 0) in vec3 a_Position;

uniform mat4 viewMatrix;
uniform mat4 projMatrix;

out vec3 worldPosition;
out vec2 texCoord;

void main() {
    texCoord = a_Position.xz / 4;

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

out vec2 v_TexCoord;
out mat3 normalMatrix;
out vec3 cameraDirection;
out vec3 lightDirection;

void main() {
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

    normalMatrix = mat3(tangent, bitangent, -normalize(cross(worldPosition[1] - worldPosition[0], worldPosition[2] - worldPosition[0])));

    gl_Position = gl_in[0].gl_Position;
    v_TexCoord = texCoord[0];
    cameraDirection = normalize(cameraPos - worldPosition[0]);
    lightDirection = normalize(vec3(0, 1000, 0) - worldPosition[0]);
    EmitVertex();

    gl_Position = gl_in[1].gl_Position;
    v_TexCoord = texCoord[1];
    cameraDirection = normalize(cameraPos - worldPosition[1]);
    lightDirection = normalize(vec3(0, 1000, 0) - worldPosition[1]);
    EmitVertex();

    gl_Position = gl_in[2].gl_Position;
    v_TexCoord = texCoord[2];
    cameraDirection = normalize(cameraPos - worldPosition[2]);
    lightDirection = normalize(vec3(0, 1000, 0) - worldPosition[2]);
    EmitVertex();

    EndPrimitive();
}

#fragment
#version 450 core

layout(location = 0) out vec4 o_Color;

layout(binding = 0) uniform sampler2D u_Texture;
layout(binding = 1) uniform sampler2D u_NormalMap;

uniform vec4 in_Color;

in vec2 v_TexCoord;

in mat3 normalMatrix;
in vec3 cameraDirection;
in vec3 lightDirection;

void main() {
    vec4 texColor = texture(u_Texture, v_TexCoord);
    vec3 fragnormal = normalize(texture(u_NormalMap, v_TexCoord).xyz * normalMatrix);

    //float highLight = pow(dot(cameraDirection, reflect(-lightDirection, fragnormal)) / 2 + 0.5f, 500) * 0.1f;
    //float light = (dot(fragnormal, lightDirection) / 2 + 0.5f) * 0.5f - 0.2f;
    //o_Color = in_Color.w == 1f ? max(min(vec4(texColor.x + light + highLight, texColor.y + light + highLight, texColor.z + light + highLight, texColor.w), 1), 0) : vec4((in_Color.xyz + texColor.xyz) / 2, in_Color.w);

    vec3 ambient = 0.5 * vec3(1, 1, 1);
    float diff = max(dot(fragnormal, lightDirection), 0.0);
    vec3 diffuse = diff * vec3(1, 1, 1);
    vec3 result = (ambient + diffuse) * texColor.xyz;
    o_Color = vec4(result, in_Color.w);

}