package com.COMPFIZ.core;

import com.COMPFIZ.core.Entity.Model;
import com.COMPFIZ.core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4i;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectLoader{
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public Model loadOBJFILE(String filename){
        List<String> lines = Utils.readAllLines(filename);
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for(String line  : lines){
            String[] tokens = line.split("\\s+");
            switch(tokens[0]) {//.obj file encoding?
                case "v"://case vertices
                    Vector3f verticesVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(verticesVec);
                    break;
                case "vt"://case vertex textures
                    Vector2f texturesVec = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textures.add(texturesVec);
                    break;
                case "vn"://vertex normals
                    Vector3f normalsVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normals.add(normalsVec);
                    break;
                case "f"://faces

                    // If it's a quad, split it into two triangles
                    if(tokens.length == 5) {
                        processFaces(tokens[1], faces);
                        processFaces(tokens[2], faces);
                        processFaces(tokens[3], faces);

                        processFaces(tokens[1], faces);
                        processFaces(tokens[3], faces);
                        processFaces(tokens[4], faces);
                    } else{
                        processFaces(tokens[1], faces);
                        processFaces(tokens[2], faces);
                        processFaces(tokens[3], faces);
                    }
                    break;
                default:
                    break;
            }
        }
        List<Integer> indices = new ArrayList<>();
        float[] verticesArr = new float[vertices.size() * 3];
        int i = 0;
        for(Vector3f posi : vertices){
            verticesArr[i * 3] = posi.x;
            verticesArr[i * 3 + 1] = posi.y;
            verticesArr[i * 3 + 2] = posi.z;
            i++;
        }
        float[] texCoordArr = new float[vertices.size() * 2];
        float[] normalArr = new float[vertices.size() * 3];
        faces.stream().forEach(face -> processVertex(face.x, face.y, face.z, textures, normals, indices, texCoordArr, normalArr));
        int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();
        return loadModel(verticesArr, texCoordArr, normalArr, indicesArr);
    }
    private static void processVertex(int pos, int texCoord, int normal, List<Vector2f> texCoordsList, List<Vector3f> normalsList, List<Integer> indiceList, float[] texCoordArr, float[] normalArr){
        indiceList.add(pos);

        if(texCoord >= 0){
            Vector2f texCoordVec = texCoordsList.get(texCoord);
            texCoordArr[pos * 2] = texCoordVec.x;
            texCoordArr[pos * 2 + 1] = 1 - texCoordVec.y;
        }

        if(normal > 0){//maybe not equal to
            Vector3f normalVec = normalsList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;
        }
    }

    private static void processFaces(String token, List<Vector3i> faces){//Or face singular, idk
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int position = -1, coords = -1, normal = -1;
        position = Integer.parseInt(lineToken[0])- 1;
        if(length > 1){
            String texCoord = lineToken[1];
            coords = texCoord.length() > 0 ? Integer.parseInt(texCoord)-1 : -1;
            if(length > 2){
                normal = Integer.parseInt(lineToken[2])- 1;
            }
            Vector3i facesVec = new Vector3i(position, coords, normal);
            faces.add(facesVec);
            if(length == 5) { // If it's a quad
                position = Integer.parseInt(lineToken[3])- 1;
                facesVec = new Vector3i(position, coords, normal);
                faces.add(facesVec);
            }
        }
    }
    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices){
        int id = createVAO();
        this.StoreDataIndicesBuffer(indices);
        StoreDataInAttribList(0, 3, vertices);
        StoreDataInAttribList(1, 2, textureCoords);
        StoreDataInAttribList(2, 3, normals);
        unbind();
        return new Model(id, indices.length);
    }

    public int loadTexture(String filename) throws Exception{
        int width, height;
        ByteBuffer buffer;
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(filename, w, h, c, 4);
            if(buffer == null){
                throw new Exception("Image File " + filename + " not loaded " + STBImage.stbi_failure_reason());
            }
            width = w.get();
            height = h.get();
        }
       int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return id;
    }

    private int createVAO(){
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    private void StoreDataIndicesBuffer(int[] indices){
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.StoreDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private void StoreDataInAttribList(int attribNum, int vertexCount, float[] data){
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.StoreDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribNum, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbind(){
        GL30.glBindVertexArray(0);
    }

    public void cleanup(){
        for(int vao : vaos){
            GL30.glDeleteVertexArrays(vao);
        }
        for(int vbo : vbos){
            GL30.glDeleteBuffers(vbo);
        }
        for(int texture : textures){
            GL11.glDeleteTextures(texture);
        }
    }
}
