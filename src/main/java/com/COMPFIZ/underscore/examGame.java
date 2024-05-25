package com.COMPFIZ.underscore;

import com.COMPFIZ.core.Entity.Model;
import com.COMPFIZ.core.ILogic;
import com.COMPFIZ.core.ObjectLoader;
import com.COMPFIZ.core.RenderManager;
import com.COMPFIZ.core.WindowManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class examGame implements ILogic {

    private int direction = 0;
    private float color = 0f;
    private final RenderManager rendMan;
    private final ObjectLoader loader;
    private final WindowManager winMan;
    private Model model;


    public examGame() {
        rendMan = new RenderManager();
        winMan = Launcher.getWinMan();
        loader = new ObjectLoader();
    }

    @Override
    public void init() throws Exception {
        rendMan.init();
        float[] vertices = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f
        };
        model = loader.loadModel(vertices);
    }

    @Override
    public void input() {
        if(winMan.isKeyPressed(GLFW.GLFW_KEY_UP)){
            direction = 1;
        } else if (winMan.isKeyPressed(GLFW.GLFW_KEY_DOWN)){
            direction = -1;
        }
        else{
            direction = 0;
        }
    }

    @Override
    public void update() {
        color+=direction*0.01f;
        if(color > 1){
            color = 1.0f;
        } else if (color < 0){
            color = 0.0f;
        }
    }

    @Override
    public void render() {
        if(winMan.isResize()){
            GL11.glViewport(0, 0, winMan.getWidth(), winMan.getHeight());
            winMan.setResize(true);
        }

        winMan.setClearColor(color, color, color, 0.0f);
        rendMan.render(model);
    }

    @Override
    public void cleanup() {
        rendMan.cleanup();
        loader.cleanup();
    }
}
