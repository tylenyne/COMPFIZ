package com.COMPFIZ.core;

import com.COMPFIZ.core.utils.Constants;
import com.COMPFIZ.underscore.Launcher;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {
    public static final long NANOSECOND = 1000000000L;
    public static final float FRAMERATE = 120f;
    private static int fps;
    private static float frametime = 1.0f/FRAMERATE;

    private boolean isRunning;

    private WindowManager winMan;
    private MouseInput mouseIn;
    private _IGamelogic gameLogic;
    private GLFWErrorCallback errorCallback;

    private void init() throws Exception{
        GLFW.glfwSetErrorCallback(this.errorCallback = GLFWErrorCallback.createPrint(System.err));
        winMan = Launcher.getWinMan();
        gameLogic = Launcher.getThisGame();
        mouseIn = new MouseInput();
        winMan.init();//Trying to initialize window but have control of when to show it. Not really important but its practice
        winMan.showWindow();//Make a function for this string of functions especially if  it gets bigger
        gameLogic.init();
        mouseIn.init();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }

    public void start() throws Exception{
        this.init();
        if(isRunning){
            return;
        }
        run();
    }

    public void run(){
        this.isRunning = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unproccessedTime = 0D;
//MainGameLoop-
        while(isRunning){
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime-lastTime;
            lastTime = startTime;

            unproccessedTime+=passedTime/(double)this.NANOSECOND;
            frameCounter+=passedTime;

            input();

            while(unproccessedTime > frametime){
                render = true;
                unproccessedTime-=frametime;

                if(winMan.windowShouldClose()){
                    this.stop();
                }

                if(frameCounter >= NANOSECOND){
                    this.setFps(frames);
                    winMan.setTitle(Constants.TITLE +" Fps:"  + getFps());
                    frames = 0;
                    frameCounter = 0L;
                }
            }

            if(render){
                update(frametime);
                render();
                frames++;
            }

        }
        this.cleanup();
    }

    private void stop(){
        if(!this.isRunning){
            return;
        }
        this.isRunning = false;
    }

    private void input(){
        mouseIn.input();
        gameLogic.input();
    }

    private void render(){
        gameLogic.render();
        winMan.update();
    }

    private void update(float interval){
        gameLogic.update(interval, mouseIn);
    }

    private void cleanup(){
        winMan.cleanup();
        gameLogic.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }
}