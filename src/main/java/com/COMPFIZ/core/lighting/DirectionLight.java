package com.COMPFIZ.core.lighting;

import org.joml.Vector3f;


public class DirectionLight {
    private Vector3f color, direction;
    private float intesity;

    public DirectionLight(Vector3f color, Vector3f direction, float intesity) {
        this.color = color;
        this.direction = direction;
        this.intesity = intesity;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getIntesity() {
        return intesity;
    }

    public void setIntesity(float intesityl) {
        this.intesity = intesityl;
    }
}
