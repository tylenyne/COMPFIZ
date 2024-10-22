#version 400 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 fragTextureCoordinates;
out vec3 fragNormal;
out vec3 fragPos;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
    vec4 wolrdPos = transformationMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * wolrdPos;

    fragNormal = normalize(wolrdPos.xyz);
    fragPos = wolrdPos.xyz;
    fragTextureCoordinates = textureCoordinates/2.5;


}