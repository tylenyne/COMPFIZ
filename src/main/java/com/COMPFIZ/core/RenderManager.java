package com.COMPFIZ.core;

import com.COMPFIZ.underscore.Launcher;
import org.lwjgl.opengl.GL11;

public class RenderManager {
    private final WindowManager winMan;

    public RenderManager(){
        winMan = Launcher.getWinMan();
    }

    public void init() throws Exception{

    }

    public void render(){

    }

    public void clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup(){

    }
}
