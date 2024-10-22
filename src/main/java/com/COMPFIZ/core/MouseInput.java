package com.COMPFIZ.core;

import com.COMPFIZ.underscore.Launcher;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {
    private final Vector2d previousPos, currentPos;
    private final Vector2f displVec;
    private boolean inWindow = false, leftButtonPressed = false, rightButtonPressed = false;

    public MouseInput(){
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public void init(){
        GLFW.glfwSetCursorPosCallback(Launcher.getWinMan().getWindow(), (window, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });
        GLFW.glfwSetCursorEnterCallback(Launcher.getWinMan().getWindow(), (window, entered) -> {
            inWindow = entered;
        });
        GLFW.glfwSetMouseButtonCallback(Launcher.getWinMan().getWindow(), (window, button, action, mods) ->{
            leftButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_1 && action ==GLFW.GLFW_PRESS;
            rightButtonPressed = button == GLFW.GLFW_MOUSE_BUTTON_2 && action ==GLFW.GLFW_PRESS;
        });
    }

    public void input(){
        displVec.x = 0;
        displVec.y = 0;
        if(previousPos.x > 0 && previousPos.y > 0 && inWindow){
            double x = currentPos.x - previousPos.x;
            double y = currentPos.y - previousPos.y;
            boolean rotatex = x!=0;
            boolean rotatey = y!=0;
            if(rotatex){
                displVec.y = (float)x;
            }
            if(rotatey){
                displVec.x = (float)y;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public Vector2f getDisplVec() {
        return displVec;
    }
}
