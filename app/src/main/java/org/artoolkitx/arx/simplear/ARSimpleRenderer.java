package org.artoolkitx.arx.simplear;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import org.artoolkitx.arx.arxj.ARController;
import org.artoolkitx.arx.arxj.Trackable;
import org.artoolkitx.arx.arxj.rendering.ARRenderer;
import org.artoolkitx.arx.arxj.rendering.ShaderProgram;
import org.artoolkitx.arx.arxj.rendering.shader_impl.Cube;
import org.artoolkitx.arx.arxj.rendering.shader_impl.SimpleFragmentShader;
import org.artoolkitx.arx.arxj.rendering.shader_impl.SimpleShaderProgram;
import org.artoolkitx.arx.arxj.rendering.shader_impl.SimpleVertexShader;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES10.glClearColor;
import static android.opengl.GLES20.glUniform4f;

public class ARSimpleRenderer extends ARRenderer {

    private SimpleShaderProgram shaderProgram;
    private Context context;

    //TODO: I think we should add the trackable class to the library (arxj)


    public ARSimpleRenderer(Context context){
        this.context = context;
    }

    private static final Trackable trackables[] = new Trackable[]{
            new Trackable("hiro", 80.0f),
            new Trackable("kanji", 80.0f)
    };
    private int trackableUIDs[] = new int[trackables.length];

    private Cube cube;
    private float [] color;
    private Torus mTorus;
    private MagneticField mMagneticField;

    /**
     * Markers can be configured here.
     */
    @Override
    public boolean configureARScene() {
        int i = 0;
        for (Trackable trackable : trackables) {
            trackableUIDs[i] = ARController.getInstance().addTrackable("single;Data/" + trackable.getName() + ".patt;" + trackable.getWidth());
            if (trackableUIDs[i] < 0) return false;
            i++;
        }
        return true;
    }

    //Shader calls should be within a GL thread. GL threads are onSurfaceChanged(), onSurfaceCreated() or onDrawFrame()
    //As the cube instantiates the shader during setShaderProgram call we need to create the cube here.
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        this.shaderProgram = new SimpleShaderProgram(new SimpleVertexShader(), new SimpleFragmentShader());
//        try {
//            mTorus = new Torus(this.context);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try{
            mMagneticField = new MagneticField(this.context);
        }catch (IOException e){
            e.printStackTrace();
        }

        cube = new Cube(40.0f, 0.5f, 0.5f, 30.0f);
        cube.setShaderProgram(shaderProgram);
        super.onSurfaceCreated(unused, config);
    }


    /**
     * Override the draw function from ARRenderer.
     */
    @Override
    public void draw() {
        super.draw();

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glFrontFace(GLES20.GL_CCW);

        // Look for trackables, and draw on each found one.
        for (int trackableUID : trackableUIDs) {
            // If the trackable is visible, apply its transformation, and render a cube
            float[] modelViewMatrix = new float[16];
            //identificar o que é o modelViewMatrix
            if ((trackableUID == 0) && (ARController.getInstance().queryTrackableVisibilityAndTransformation(trackableUID, modelViewMatrix))) {
                float[] projectionMatrix = ARController.getInstance().getProjectionMatrix(10.0f, 10000.0f);

                mMagneticField.draw(projectionMatrix,modelViewMatrix);
            }
            if ((trackableUID == 1) && (ARController.getInstance().queryTrackableVisibilityAndTransformation(trackableUID, modelViewMatrix))) {
                float[] projectionMatrix = ARController.getInstance().getProjectionMatrix(10.0f, 10000.0f);
                cube.draw(projectionMatrix, modelViewMatrix);
            }
        }
    }


}
