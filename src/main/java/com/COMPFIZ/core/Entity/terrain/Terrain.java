package com.COMPFIZ.core.Entity.terrain;


import com.COMPFIZ.core.Entity.Material;
import com.COMPFIZ.core.Entity.Model;
import com.COMPFIZ.core.Entity.Texture;
import com.COMPFIZ.core.ObjectLoader;
import org.joml.Vector3f;

public class Terrain {
    private static final float SIZE = 800;
    private static final int VERTEX_COUNT = 128;
    private Vector3f position, rotation;
    private Model model;

    public Terrain(Vector3f position, ObjectLoader loader, Material material){
        this.position = position;
        this.model = generateTerrain(loader);
        this.model.setMaterial(material);
    }

    private Model generateTerrain(ObjectLoader loader){
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 *(VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for(int i = 0; i<VERTEX_COUNT; i++){
            for(int j = 0; i<VERTEX_COUNT; j++){
                vertices[vertexPointer * 3] = j/(VERTEX_COUNT-1.0f) * SIZE;
                vertices[vertexPointer * 3 + 1] = 0; //height map
                vertices[vertexPointer * 3 + 2] = i/(VERTEX_COUNT-1.0f) * SIZE;
                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2] = 0;
                textureCoords[vertexPointer * 2] = j/(VERTEX_COUNT-1.0f) * SIZE;
                textureCoords[vertexPointer * 2 + 1] = i/(VERTEX_COUNT-1.0f) * SIZE;
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int z = 0; z<VERTEX_COUNT - 1.0f; z++){
            for(int x = 0; z<VERTEX_COUNT - 1.0f; x++){// I think these below are the coords of the overall terrain
                //square I think
                int TOPLEFT = (z * VERTEX_COUNT) + x;
                int TOPRIGHT = TOPLEFT + 1;
                int BOTTOMLEFT = ((z + 1) * VERTEX_COUNT) + x;
                int BOTTOMRIGHT = BOTTOMLEFT + 1;
                indices[pointer++] = TOPLEFT;
                indices[pointer++] = BOTTOMLEFT;
                indices[pointer++] = TOPRIGHT;
                indices[pointer++] = TOPRIGHT;
                indices[pointer++] = BOTTOMLEFT;
                indices[pointer++] = BOTTOMRIGHT;
            }
        }
        return loader.loadModel(vertices, textureCoords, normals, indices);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Model getModel() {
        return model;
    }

    public Material getMaterial(){
        return model.getMaterial();
    }

    public Texture getTexture(){
        return model.getTexture();
    }
}
