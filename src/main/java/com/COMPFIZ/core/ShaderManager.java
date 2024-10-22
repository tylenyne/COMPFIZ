package com.COMPFIZ.core;

import com.COMPFIZ.core.Entity.Material;
import com.COMPFIZ.core.lighting.DirectionLight;
import com.COMPFIZ.core.lighting.PointLight;
import com.COMPFIZ.core.lighting.SpotLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ShaderManager {//organize this thang
    private final int programID;
    private int vertexShaderID, fragmentShaderID;
    private final Map<String, Integer> uniforms;

    public ShaderManager() throws Exception{
        programID = GL20.glCreateProgram();
        if(programID == 0){
            throw new Exception("Could not create shaders");
        }
        uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) throws  Exception{
        int uniformLOC = GL20.glGetUniformLocation(programID, uniformName);
        if(uniformLOC < 0){
            throw new Exception("Could not find Uniform " + uniformName);
        }
        uniforms.put(uniformName, uniformLOC);//Location
    }

    public void setUniform(String uniformID, Matrix4f value){//Hashmap so String uniformKey -> uniform
        try(MemoryStack stack = MemoryStack.stackPush()){
            GL20.glUniformMatrix4fv(uniforms.get(uniformID), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void createDirectionLightUniform(String uniformname) throws Exception{
        createUniform(uniformname + ".color");
        createUniform(uniformname + ".direction");
        createUniform(uniformname + ".intensity");
    }

    public void createMaterialUniform(String uniformname) throws Exception{
        createUniform(uniformname + ".ambient");
        createUniform(uniformname + ".diffuse");
        createUniform(uniformname + ".specular");
        createUniform(uniformname + ".hasTexture");
        createUniform(uniformname + ".reflectance");
    }



    public void createPointLightUniform(String uniformname) throws Exception{
        createUniform(uniformname + ".color");
        createUniform(uniformname + ".position");
        createUniform(uniformname + ".intensity");
        createUniform(uniformname + ".constant");
        createUniform(uniformname + ".linear");
        createUniform(uniformname + ".exponent");
    }
    public void createSpotLightUniform(String uniformname) throws  Exception{
        createPointLightUniform(uniformname + ".pl");
        createUniform(uniformname + ".conedir");//pretty sure corresponds to the variable
        createUniform(uniformname + ".cutoff");
    }

    //LightSection
    public void createPointLightListU(String uniformname, int size) throws Exception{
        for(int i = 0; i<size; i++){
            createPointLightUniform(uniformname + "[" + i + "]");
        }
    }

    public void createSpotLightListU(String uniformname, int size) throws Exception{
        for(int i = 0; i<size; i++){
            createSpotLightUniform(uniformname + "[" + i + "]");
        }
    }

//Array- sets all uniforms in an array
    public void setUniforms(String uniforms, PointLight[] plist){
        for(int i = 0; i < plist.length; i++){
            setUniform(uniforms, plist[i], i);
        }
    }

    public void setUniforms(String uniforms, SpotLight[] slist){
        for(int i = 0; i < slist.length; i++){
            setUniform(uniforms, slist[i], i);
        }
    }

//sets indivisual uniform in array. The problem is Im trying to link a java Array and/or ArrayList to a shaderArray because I dont think there is shaderArrayList in .glsl language or .fsh file
    public void setUniform(String uniform, PointLight pointLight, int pos){
        setUniform(uniform + "[" + pos + "]", pointLight);
    }
//sets indivisual uniform in array
    public void setUniform(String uniform, SpotLight spotLight, int pos){
        setUniform(uniform + "[" + pos + "]", spotLight);
    }

    public void setUniform(String uniform, Vector4f value){
        GL20.glUniform4f(uniforms.get(uniform), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniform, DirectionLight directionLight){
        setUniform(uniform + ".color", directionLight.getColor());
        setUniform(uniform + ".direction", directionLight.getDirection());
        setUniform(uniform + ".intensity", directionLight.getIntesity());
    }

    public void setUniform(String uniform, boolean value){
        float res = 0;
        if(value)
            res = 1;
        GL20.glUniform1f(uniforms.get(uniform), res);
    }

    public void setUniform(String uniform, SpotLight spotLight){
        setUniform(uniform + ".pl", spotLight.getPointLight());
        setUniform(uniform + ".conedir", spotLight.getConeDirection());
        setUniform(uniform + ".cutoff", spotLight.getCutoff());
    }


    public void setUniform(String uniform, Material material ){
        setUniform(uniform + ".ambient", material.getAmbientColor());
        setUniform(uniform + ".diffuse", material.getDiffuseColor());
        setUniform(uniform + ".specular", material.getSpecularColor());
        setUniform(uniform + ".hasTexture", material.hasTexture() ? 1 : 0);
        setUniform(uniform + ".reflectance", material.getReflectance());
    }

    public void setUniform(String uniform, PointLight pointLight){
        setUniform(uniform + ".color", pointLight.getColor());
        setUniform(uniform + ".position", pointLight.getPosition());
        setUniform(uniform + ".intensity", pointLight.getIntensity());
        setUniform(uniform + ".constant", pointLight.getConstant());
        setUniform(uniform + ".linear", pointLight.getLinear());
        setUniform(uniform + ".exponent", pointLight.getExponent());
    }

    public void setUniform(String uniform, Vector3f value){
        GL20.glUniform3f(uniforms.get(uniform), value.x, value.y, value.z);
    }

    public void setUniform(String uniform, float value){
        GL20.glUniform1f(uniforms.get(uniform), value);
    }

    public void setUniform(String uniform, int value){
        GL20.glUniform1i(uniforms.get(uniform), value);
    }

    public void createVertexShader(String shdrCode) throws Exception{
        vertexShaderID = createShader(shdrCode, GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String fragCode) throws Exception{
        fragmentShaderID = createShader(fragCode, GL20.GL_FRAGMENT_SHADER);
    }

    public int createShader(String shdrCode, int shdrType) throws Exception{
        int shaderID = GL20.glCreateShader(shdrType);
        if(shaderID == 0){
            throw new Exception("Could not create shader, Type: " + shdrType);
        }
        GL20.glShaderSource(shaderID, shdrCode);
        compile:{
            GL20.glCompileShader(shaderID);
            if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0) {
                throw new Exception("Error compiling shader code, Type: " + shdrType
                        + " Info- " + GL20.glGetShaderInfoLog(shaderID, 1024));
            }
            GL20.glAttachShader(programID, shaderID);
        }
        return shaderID;
    }

    public void link() throws Exception{ //Links and validates shaderCodes and FragCodes. Deals alot with the program also
        GL20.glLinkProgram(programID);
        if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0){
            throw new Exception("Error linking shader code, Info- "
                    + GL20.glGetProgramInfoLog(programID, 1024));
        }
        if(vertexShaderID != 0){
            GL20.glDetachShader(programID, vertexShaderID);
        }
        if(fragmentShaderID != 0){
            GL20.glDetachShader(programID, fragmentShaderID);
        }
        GL20.glValidateProgram(programID);
        if(GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0){
            throw new Exception("Unable to validate shader code: " + GL20.glGetProgramInfoLog(programID, 1024));
        }
    }

    public void bind(){
        GL20.glUseProgram(programID);
    }

    public void unbind(){
        GL20.glUseProgram(0);
    }

    public void cleanup(){
        this.unbind();
        if(programID!=0){
            GL20.glDeleteProgram(programID);
        }
    }

}
