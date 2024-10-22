package com.COMPFIZ.core.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Scanner;

public class Constants {
    public static final String TITLE = "_UNDERSCORE";
    public static final float MOUSE_SENSITIVITY = 0.2f;
    public static final Vector3f DEFAULTVECTOR3F = new Vector3f(0, 0, 0);
    //I could overload methods in entity class to use this but I dont wanna right now
    public static final float DEFAULTSCALE = 1;
    //public static class Color;
    public static final int MAX_SPOTLIGHTS = 5;
    public static final int MAX_POINTLIGHTS = 5;
    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Vector3f AMBIENT_LIGHT = new Vector3f(2f, 2f, 2f);
    public static final float SPECULAR_POWER = 10f;
}
