#vertex
#version 450 core

layout(location = 0) in vec3 a_Position;
layout(location = 1) in vec4 a_Color;

uniform mat4 viewMatrix;
uniform mat4 projMatrix;

out vec2 worldPos;
flat out vec4 v_Color;

void main() {
    v_Color = a_Color;
    worldPos = a_Position.xz;
    gl_Position = projMatrix * viewMatrix * vec4(a_Position, 1.0f);
}

#fragment
#version 450 core

layout(location = 0) out vec4 o_Color;

layout(std430, binding = 2) buffer pos1buffer {
    vec2 pos1[];
};
layout(std430, binding = 3) buffer pos2buffer {
    vec2 pos2[];
};
layout(std430, binding = 4) buffer widthbuffer {
    float width[];
};
layout(std430, binding = 5) buffer colorbuffer {
    vec4 color[];
};

uniform int numOfLines;

in vec2 worldPos;
flat in vec4 v_Color;

float distToLine(int line) {
    if(dot(worldPos - pos1[line], pos2[line] - pos1[line]) < 0) return length(worldPos - pos1[line]);
    if(dot(worldPos - pos2[line], pos1[line] - pos2[line]) < 0) return length(worldPos - pos2[line]);
    vec2 x = worldPos - pos1[line];
    vec2 a = pos2[line] - pos1[line];
    return length(x - a * dot(x, a) / dot(a, a));
}

vec4 addColors(vec4 v1, vec4 v2, float a) {
    return (v1 * v1.w * (1.0f-v2.w * a) + v2 * v2.w * a) / (v1.w * (1.0f-v2.w * a) + v2.w * a);
}

float smoothEdge(float f) {
    float x = f;
    for(int i = 0; i < 5; i++, x = x * x){}
    return 32 * f * x - 33 * x + 1.0f;
}

void main() {
    float mindist = 1000;
    int line = 0;
    for(int i = 0; i < numOfLines; i++) {
        float dist = distToLine(i);
        if (distToLine(i) < mindist) {
            mindist = dist;
            line = i;
        }
    }

    bool b = mindist <= width[line] / 2.0f;
    o_Color = addColors(v_Color, (b ? color[line] : vec4(0,0,0,0)), b ? smoothEdge(2 * mindist / width[line]) : 1.0);
}