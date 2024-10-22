package com.COMPFIZ.core.rendering;

import com.COMPFIZ.core.Camera;
import com.COMPFIZ.core.Entity.Model;
import com.COMPFIZ.core.lighting.DirectionLight;
import com.COMPFIZ.core.lighting.PointLight;
import com.COMPFIZ.core.lighting.SpotLight;


public interface _IRenders <T> {//List format no enter/too much
    public void init() throws Exception;
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionLight directionLight);
    abstract void bind(Model model);
    public void unbind();
    public void prepare(T t, Camera camera);
    public void cleanup();


}
