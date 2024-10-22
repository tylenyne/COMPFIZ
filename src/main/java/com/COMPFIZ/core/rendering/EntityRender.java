package com.COMPFIZ.core.rendering;

import com.COMPFIZ.core.Camera;
import com.COMPFIZ.core.Entity.Entity;
import com.COMPFIZ.core.Entity.Model;
import com.COMPFIZ.core.ShaderManager;
import com.COMPFIZ.core.Transformation;
import com.COMPFIZ.core.lighting.DirectionLight;
import com.COMPFIZ.core.lighting.PointLight;
import com.COMPFIZ.core.lighting.SpotLight;
import com.COMPFIZ.core.utils.Constants;
import com.COMPFIZ.core.utils.Utils;
import com.COMPFIZ.underscore.Launcher;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRender implements _IRenders {
    ShaderManager shadeMan;//Package-private
    private Map<Model, List<Entity>> entities;
    private int dissolveFactor = 1;

    public EntityRender() throws Exception{
        entities = new HashMap<>();
        //GL11.glbindTexture(...);
        shadeMan = new ShaderManager();
    }
    @Override
    public void init() throws Exception{
        shadeMan.createVertexShader(Utils.loadResource("/shaders/entity_vertex.vsh"));
        shadeMan.createFragmentShader(Utils.loadResource("/shaders/entity_fragment.fsh"));
        shadeMan.link();
        shadeMan.createUniform("TextureSampler");
        shadeMan.createUniform("Dissolve");
        shadeMan.createUniform("dissolveFactor");
        shadeMan.createUniform("transformationMatrix");
        shadeMan.createUniform("projectionMatrix");
        shadeMan.createUniform("viewMatrix");
        shadeMan.createUniform("AmbientLight");
        shadeMan.createMaterialUniform("material");
        shadeMan.createUniform("specularPower");
        shadeMan.createDirectionLightUniform("directionLight");
        shadeMan.createPointLightListU("pointLights", Constants.MAX_POINTLIGHTS);
        shadeMan.createSpotLightListU("spotLights", Constants.MAX_SPOTLIGHTS);
    }

    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionLight directionLight){
        shadeMan.bind();
        shadeMan.setUniform("projectionMatrix", Launcher.getWinMan().updateProjectionMatrix());
        RenderManager.renderLights(pointLights, spotLights, directionLight, shadeMan);
        for(Model model : entities.keySet()){
            this.bind(model);
            List<Entity> entityList = entities.get(model);//incase of multiple of same model
            for(Entity entitySingular : entityList){
                prepare(entitySingular, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, entitySingular.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }//Why no GL_QUADS
            this.unbind();

        }
        entities.clear();
        shadeMan.unbind();
    }

    @Override
    public void bind(Model model){
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        shadeMan.setUniform("material", model.getMaterial());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
    }

    @Override
    public void unbind(){
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(Object entity, Camera camera){
        shadeMan.setUniform("TextureSampler", 0);
        shadeMan.setUniform("Dissolve", 1);
        shadeMan.setUniform("dissolveFactor", dissolveFactor);
        shadeMan.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Entity)entity));
        shadeMan.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
    }

    @Override
    public void cleanup(){
        shadeMan.cleanup();
    }//Entity renderER if you were wondering

    public Map<Model, List<Entity>> getEntities(){
        return entities;
    }
}
