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

#geometry
#version 450 core

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

uniform vec3 cameraPos;

in vec3 worldPosition[];
out vec3 faceNormal;
out vec3 cameraDirection;
out vec3 lightDirection;

void main() {
    vec3 normal = normalize(cross(worldPosition[1] - worldPosition[0], worldPosition[2] - worldPosition[0]));

    faceNormal = normal;
    gl_Position = gl_in[0].gl_Position;
    cameraDirection = normalize(cameraPos - worldPosition[0]);
    lightDirection = normalize(vec3(0, 30, 0) - worldPosition[0]);
    EmitVertex();

    faceNormal = normal;
    gl_Position = gl_in[1].gl_Position;
    cameraDirection = normalize(cameraPos - worldPosition[1]);
    lightDirection = normalize(vec3(0, 30, 0) - worldPosition[1]);
    EmitVertex();

    faceNormal = normal;
    gl_Position = gl_in[2].gl_Position;
    cameraDirection = normalize(cameraPos - worldPosition[2]);
    lightDirection = normalize(vec3(0, 30, 0) - worldPosition[2]);
    EmitVertex();

}


#fragment
#version 450 core

layout(location = 0) out vec4 o_Color;

in vec3 faceNormal;
in vec3 cameraDirection;
in vec3 lightDirection;

void main() {
    float highLight = pow(dot(cameraDirection, reflect(-lightDirection, faceNormal)) / 2 + 0.5f, 20);
    float light = (dot(faceNormal, lightDirection) / 2 + 0.5f) * 0.4f;
    vec3 color = max(min(vec3(1.0 + light + highLight, light + highLight, light + highLight), 1), 0);

    o_Color = vec4(color, 1);
}