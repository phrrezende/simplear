package org.artoolkitx.arx.simplear;

import android.content.Context;

import org.apache.commons.io.IOUtils;
import org.artoolkitx.arx.arxj.rendering.ARDrawable;
import org.artoolkitx.arx.arxj.rendering.ShaderProgram;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class Teste implements ARDrawable {

    private ShaderProgram shaderProgram;
    private List<String> verticesList;
    private List<String> facesList;
    private FloatBuffer verticesBuffer;
    private ShortBuffer facesBuffer;
    private Context context;

    public Teste(Context context) throws IOException {
        verticesList = new ArrayList<>();
        facesList = new ArrayList<>();
        this.context = context;

        Scanner scanner = new Scanner(context.getAssets().open("torus.obj"));

        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            if(line.startsWith("v ")) {
                // Add vertex line to list of vertices
                verticesList.add(line);
            } else if(line.startsWith("f ")) {
                // Add face line to faces list
                facesList.add(line);
            }
        }
        scanner.close();

        ByteBuffer buffer1 = ByteBuffer.allocateDirect(verticesList.size() * 3 * 4);
        buffer1.order(ByteOrder.nativeOrder());
        verticesBuffer = buffer1.asFloatBuffer();

        ByteBuffer buffer2 = ByteBuffer.allocateDirect(facesList.size() * 3 * 2);
        buffer2.order(ByteOrder.nativeOrder());
        facesBuffer = buffer2.asShortBuffer();

        for(String vertex: verticesList) {
            String coords[] = vertex.split(" "); // Split by space
            float x = Float.parseFloat(coords[1]);
            float y = Float.parseFloat(coords[2]);
            float z = Float.parseFloat(coords[3]);
            verticesBuffer.put(x);
            verticesBuffer.put(y);
            verticesBuffer.put(z);
        }
        verticesBuffer.position(0);

        for(String face: facesList) {
            String vertexIndices[] = face.split(" ");
            short vertex1 = Short.parseShort(vertexIndices[1]);
            short vertex2 = Short.parseShort(vertexIndices[2]);
            short vertex3 = Short.parseShort(vertexIndices[3]);
            facesBuffer.put((short)(vertex1 - 1));
            facesBuffer.put((short)(vertex2 - 1));
            facesBuffer.put((short)(vertex3 - 1));
        }
        facesBuffer.position(0);

        InputStream vertexShaderStream = context.getResources().openRawResource(R.raw.vertex_shader);
        String vertexShaderCode = IOUtils.toString(vertexShaderStream, Charset.defaultCharset());
        vertexShaderStream.close();
        InputStream fragmentShaderStream = context.getResources().openRawResource(R.raw.fragment_shader);
        String fragmentShaderCode = IOUtils.toString(fragmentShaderStream, Charset.defaultCharset());
        fragmentShaderStream.close();
    }

    @Override
    public void draw(float[] projectionMatrix, float[] modelViewMatrix) {
        shaderProgram.setProjectionMatrix(projectionMatrix);
        shaderProgram.setModelViewMatrix(modelViewMatrix);

        //shaderProgram.render(this.getmVertexBuffer(), this.getmColorBuffer(), this.getmIndexBuffer());
        shaderProgram.render(this.verticesBuffer, null, null);
    }

    @Override
    public void setShaderProgram(ShaderProgram program) {
        this.shaderProgram = program;

    }
}
