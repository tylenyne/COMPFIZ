package com.COMPFIZ.underscore;

import com.COMPFIZ.core.*;
import com.COMPFIZ.core.Entity.Entity;
import com.COMPFIZ.core.Entity.Material;
import com.COMPFIZ.core.Entity.Model;
import com.COMPFIZ.core.Entity.Texture;
import com.COMPFIZ.core.Entity.terrain.Terrain;
import com.COMPFIZ.core.lighting.DirectionLight;
import com.COMPFIZ.core.lighting.PointLight;
import com.COMPFIZ.core.lighting.SpotLight;
import com.COMPFIZ.core.rendering.RenderManager;
import com.COMPFIZ.core.utils.Constants;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class examGame implements _IGamelogic {

    private static final float CAMERA_MOVE_SPEED = .8f;
    private final RenderManager rendMan;
    private final ObjectLoader loader;
    private final WindowManager winMan;
    private Camera camera;
    private Vector3f cameraInc;
    private List<Entity> entities;
    private List<Terrain> terrains;
    private float lightAngle;
    private float spotAngle;
    private float spotInc;
    private DirectionLight directionLight;
    private ArrayList<PointLight> pointLights = new ArrayList<>();
    private ArrayList<SpotLight> spotLights = new ArrayList<>();//HashSet would be nice too
    private Model model;


    public examGame() {
        rendMan = new RenderManager();
        winMan = Launcher.getWinMan();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
        lightAngle = -90f; //for directionallight
    }

    @Override
    public void init() throws Exception {
        rendMan.init();
        entities = new ArrayList<>();
        Random rndm = new Random();
        float[] rectPoints = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                //Left Bottom
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f
                //Top Right
        };

        Model mesh = loader.loadOBJFILE("/models/model.obj");
        mesh.setTexture(new Texture(loader.loadTexture("textures/chest.png")), 1f);
        entities.add(new Entity(mesh, new Vector3f(0, 0, 20), new Vector3f(0, 0, 0), 1f));
        terrains = new ArrayList<>();
        //Terrain terrain = new Terrain(new Vector3f(0, -1, -800), loader, new Material(new Texture(loader.loadTexture())));


        //entities.add(new Entity(mesh, new Vector3f(0,0,-2), Constants.DEFAULTVECTOR3F, .5f));

        //directional light
        float lightIntensity = 0.0f;//I dont actually need to set all these variables Its just for readability
        Vector3f lightPos = new Vector3f(-1, -10, 0);
        Vector3f lightColor = new Vector3f(1, 1, 1);
        directionLight = new DirectionLight(lightColor, lightPos, lightIntensity);

        //pointLight
        lightIntensity = 2.0f;
        lightPos = new Vector3f(0, 0, -3.2f);
        lightColor = new Vector3f(1, 1, 1);
        pointLights.add(new PointLight(lightColor, lightPos, lightIntensity, 0, 0, 1));

        //spotLight
        PointLight spl = new PointLight(lightColor, new Vector3f(1,0,-3.2f), lightIntensity, 0, 0, 1);
        Vector3f coneDir = new Vector3f(0, 0, 1f);
        float cutOff = (float)Math.cos(Math.toRadians(180));
        spotLights.add(new SpotLight(new PointLight(lightColor, new Vector3f(2f,0,-3.2f), lightIntensity, 0,0,1), coneDir, cutOff));
        spotLights.add(new SpotLight(spl, coneDir, cutOff));



    }

    @Override
    public void input() {
        cameraInc.set(0f, 0f, 0f);
        if(winMan.isKeyPressed(GLFW.GLFW_KEY_W)){
            cameraInc.z = -1;
        }
        if(winMan.isKeyPressed(GLFW.GLFW_KEY_S)){
            cameraInc.z = 1;
        }
        if(winMan.isKeyPressed(GLFW.GLFW_KEY_A)){
            cameraInc.x = -1;
        }
        if(winMan.isKeyPressed(GLFW.GLFW_KEY_D)){
            cameraInc.x = 1;
        }
        if(winMan.isKeyPressed(GLFW.GLFW_KEY_X)){
            cameraInc.y= -1;
        }
        if(winMan.isKeyPressed(GLFW.GLFW_KEY_Z)){
            cameraInc.y = 1;
        }
        if(winMan.isKeyPressed(GLFW.GLFW_KEY_SEMICOLON)){
            pointLights.get(0).getPosition().x+=0.1f;//[*number] could correspond to one equiped
        }
        if(winMan.isKeyPressed(GLFW.GLFW_KEY_L)){
            pointLights.get(0).getPosition().x-=0.1f;
        }
        float lightPos = spotLights.get(0).getPointLight().getPosition().z;
        if(winMan.isKeyPressed(GLFW.GLFW_KEY_C)){
            spotLights.get(0).getPointLight().getPosition().z = lightPos - 0.1f;
        }
        if(winMan.isKeyPressed(GLFW.GLFW_KEY_V)){
            spotLights.get(0).getPointLight().getPosition().z = lightPos + 0.1f;
        }

    }

    @Override
    public void update(float interval, MouseInput mouseIn) {
       camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED, cameraInc.y * CAMERA_MOVE_SPEED, cameraInc.z * CAMERA_MOVE_SPEED);
       if(mouseIn.isRightButtonPressed()){
           Vector2f rotVec = mouseIn.getDisplVec();
           camera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY, rotVec.y * Constants.MOUSE_SENSITIVITY, 0);
       }
       //entity.incRotation(0f, 0.25f, 0f);
      // lightAngle+=0.5f;
       if(lightAngle > 90){
           directionLight.setIntesity(0);
           if(lightAngle >= 360){
               lightAngle = -90;
           }
       } else if(lightAngle <= 80 || lightAngle >=80){
            float factor = 1 - (Math.abs(lightAngle) - 80)/10.0f;
            directionLight.setIntesity(factor);
            directionLight.getColor().y = Math.max(factor,  0.9f);
           directionLight.getColor().z = Math.max(factor,  0.5f);
       } else{
           directionLight.setIntesity(1);
           directionLight.getColor().x = 1;
           directionLight.getColor().y = 1;
           directionLight.getColor().z = 1;

       }
       double anglRadians = Math.toRadians(lightAngle);
       directionLight.getDirection().x = (float)Math.sin(anglRadians);
       directionLight.getDirection().y = (float)Math.cos(anglRadians);

       for(Entity entitySing : entities){
           rendMan.processEntity(entitySing);
       }
       entities.get(0).incRotation(30*interval, 0, 0);
    }

    @Override
    public void render() {
        /*if(winMan.isResize()){
            GL11.glViewport(0, 0, winMan.getWidth(), winMan.getHeight());
            winMan.setResize(true);
        }*/
        rendMan.render(camera, directionLight, pointLights.toArray(PointLight[]::new), spotLights.toArray(SpotLight[]::new));
    }

    @Override
    public void cleanup() {
        rendMan.cleanup();
        loader.cleanup();
    }

    public float randomSpeed(){//.05 to .01
        return (float)((int)(Math.random()+.5))/100;
    }
}
