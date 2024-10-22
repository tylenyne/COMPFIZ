#version 400 core
const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;

in vec2 fragTextureCoordinates;
in vec3 fragNormal;
in vec3 fragPos;

out vec4 fragColor;

struct Material{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

struct DirectionalLight{
    vec3 color;
    vec3 direction;
    float intensity;
};

struct PointLight{
    vec3 color;
    vec3 position;
    float intensity;
    float constant;
    float linear;
    float exponent;
};

struct SpotLight{//pretty sure it has to be proportional to java code
    PointLight pl;
    vec3 conedir;
    float cutoff;
};
//all uniform names correspond to the ones in render/shader man
uniform sampler2D Dissolve;
uniform float dissolveFactor;
uniform sampler2D TextureSampler;//puts image on object
uniform vec3 AmbientLight;
uniform Material material;
uniform float specularPower;
uniform DirectionalLight directionLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material, vec2 texCoords){
    if(material.hasTexture == 1){
        ambientC = texture(TextureSampler, texCoords);
        diffuseC = ambientC;
        specularC = ambientC;
    } else{
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal){
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specColor = vec4(0, 0, 0, 0);

    //diffuse light
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
    diffuseColor = diffuseC * vec4(light_color, 1.0) * light_intensity * diffuseFactor;

    //specular light/color
    vec3 camera_direction = normalize(-position);
    vec3 from_light_dir = -to_light_dir;
    vec3 reflectedLight = normalize(reflect(from_light_dir, normal));
    float specularFactor = max(dot(camera_direction, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColor = specularC * light_intensity * specularFactor * material.reflectance * vec4(light_color, 1.0);

    return (diffuseColor + specColor);
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 pos, vec3 normal){
    return calcLightColor(light.color, light.intensity, pos, normalize(light.direction), normal);
}


vec4 calcPointLight(PointLight light, vec3 position, vec3 normal){
    vec3 light_dir = light.position - position;
    vec3 to_light_dir = normalize(light_dir);
    vec4 lightColor = calcLightColor(light.color, light.intensity, position, to_light_dir, normal);//pointlightcolor

    //attenuation
    float distance = length(light_dir);
    float attenInv = light.constant + light.linear * distance + light.exponent * distance * distance;
    return lightColor/attenInv;
}

vec4 calcSpotLight(SpotLight light, vec3 position, vec3 normal){
    vec3 lightDir = light.pl.position - position;
    vec3 to_light_dir = normalize(lightDir);
    vec3 from_light_dir = -to_light_dir;
    float spot_alfa = dot(from_light_dir, normalize(light.conedir));

    vec4 color = vec4(0,0,0,0);
    if(spot_alfa > light.cutoff){
        color = calcPointLight(light.pl, position, normal);
        color*=(1.0f -(1.0f - spot_alfa)/(1.0f - light.cutoff));
    }
    return color;
}

void main(){//aparently something you add to this method to be compatible with almost all devices, see #12
    setupColors(material, fragTextureCoordinates);
    float dissolveValue = texture(Dissolve, fragTextureCoordinates).r;
    if (dissolveValue < dissolveFactor) {
        discard; // Discard the fragment, making this part of the texture transparent
    } else {
        // Sample the main texture and output its color
        vec4 texColor = texture(TextureSampler, fragTextureCoordinates);
        fragColor = texColor;// Output the color of the fragment
    }
    vec4 diffuseSpecularComp = calcDirectionalLight(directionLight, fragPos, fragNormal);
    for(int i = 0; i<MAX_POINT_LIGHTS; i++){
        if(pointLights[i].intensity > 0){
            diffuseSpecularComp+=calcPointLight(pointLights[i], fragPos, fragNormal);
        }
    }
for(int i = 0; i<MAX_SPOT_LIGHTS; i++){
    if(spotLights[i].pl.intensity > 0){
        diffuseSpecularComp+=calcSpotLight(spotLights[i], fragPos, fragNormal);
    }
}
    fragColor = ambientC * vec4(AmbientLight, 1) + diffuseSpecularComp;
}