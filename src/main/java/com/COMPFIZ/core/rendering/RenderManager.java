package com.COMPFIZ.core.rendering;

import com.COMPFIZ.core.Camera;
import com.COMPFIZ.core.Entity.Entity;
import com.COMPFIZ.core.Entity.terrain.Terrain;
import com.COMPFIZ.core.ShaderManager;
import com.COMPFIZ.core.WindowManager;
import com.COMPFIZ.core.lighting.DirectionLight;
import com.COMPFIZ.core.lighting.PointLight;
import com.COMPFIZ.core.lighting.SpotLight;
import com.COMPFIZ.core.utils.Constants;
import com.COMPFIZ.underscore.Launcher;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class RenderManager {
    private final WindowManager winMan;
    private EntityRender entityRender;
    private TerrainRender terrainRender;

    public RenderManager(){
        winMan = Launcher.getWinMan();
    }

    public void init() throws Exception{
        entityRender = new EntityRender();
        terrainRender = new TerrainRender();
        entityRender.init();
        terrainRender.init();
    }

    //You should know how to code multiple[] directionLights if you want
    //since you already have the code and videos for multiple pointLight/spotLights
    public static void renderLights(PointLight[] pointLights, SpotLight[] spotLights, DirectionLight directionLight, ShaderManager shader){
        shader.setUniform("AmbientLight", Constants.AMBIENT_LIGHT);//shadeMan could be a better name idk
        shader.setUniform("specularPower", Constants.SPECULAR_POWER);
        //many lights
        for(int i = 0;  i<spotLights.length; i++){
            shader.setUniform("spotLights", spotLights[i], i);
        }
        for(int j = 0; j<pointLights.length; j++){
            shader.setUniform("pointLights", pointLights[j], j);
        }
        //Only one so far, which is the sun lol
        shader.setUniform("directionLight", directionLight);
    }

    public void render(Camera camera, DirectionLight directionLight, PointLight[] pointLights, SpotLight[] spotLights){
        clear();
        if(winMan.isResized()){
            GL11.glViewport(0, 0, winMan.getWidth(), winMan.getHeight());
            winMan.setResized(false);
        }

        entityRender.render(camera, pointLights, spotLights, directionLight);
        terrainRender.render(camera, pointLights, spotLights, directionLight);
    }

    public void processEntity(Entity entity){
        List<Entity> entityList = entityRender.getEntities().get(entity.getModel());
        if(entityList != null){
            entityList.add(entity);
        } else{
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entityRender.getEntities().put(entity.getModel(), newEntityList);
        }
    }

    public void processTerrain(Terrain terrain){
        terrainRender.getTerrains().add(terrain);
    }

    public void clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup(){
        entityRender.cleanup();
        terrainRender.cleanup();
    }
}
