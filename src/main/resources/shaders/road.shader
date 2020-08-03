#vertex
#version 450 core

layout(location = 0) in vec3 a_Position;

out vec2 v_TexCoord;

uniform mat4 viewMatrix;
uniform mat4 projMatrix;

void main() {
    v_TexCoord = a_Position.xz / 3;

    vec4 worldPos = vec4(a_Position, 1.0);
    gl_Position = projMatrix * viewMatrix * worldPos;
}

#fragment
#version 450 core

layout(location = 0) out vec4 o_Color;

in vec2 v_TexCoord;

uniform vec4 in_Color;

uniform sampler2D u_Texture;

void main() {
    vec4 texColor = texture(u_Texture, v_TexCoord);
    // TODO better blending
    o_Color = in_Color.w == 1f ? texColor : vec4((in_Color.xyz + texColor.xyz) / 2, in_Color.w);
}