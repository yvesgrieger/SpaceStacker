package de.hs_kl.gatav.flyingsaucerfull.objects;

import static de.hs_kl.gatav.flyingsaucerfull.util.Utilities.normalize;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Doppelpyramide extends Obstacle {

    // current rotation
    public float rotation = 0.0f;
    // rotation speed in deg/s
    public float angularVelocity = 0.0f;
    public float[] rotationAxis = {0.0f, 1.0f, 0.0f};
    private static final float[] colorA = {0.1f, 0.1f, 0.1f};
    private static final float[] colorB = {1.0f, 1.0f, 1.0f};

    private final float[] currentColor = new float[3];

    private static FloatBuffer dpyramideVerticesBuffer;
    private static ShortBuffer dpyramideTrianglesBuffer;
    private static boolean buffersInitialized = false;

    private static final float[] dpyramide_vertices = {
            0.0f, 0.7f, 0.0f, //top
            0.5f, 0.0f, 0.5f,  //rf
            -0.5f, 0.0f, 0.5f,   //rb
            -0.5f, 0.0f, -0.5f,  //lb
            0.5f, 0.0f, -0.5f,  //lf
            0.0f, -0.7f, 0.0f  //bottom
    };
    private static final short[] dpyramide_triangles = {
            0,1,4, //topfront
            0,2,1, //topright
            0,3,2, //topback
            0,4,3, //topleft
            5,4,1, //bottomfront
            5,1,2, //bottomright
            5,2,3, //bottomback
            5,3,4, //bottomleft
            
    };
    public Doppelpyramide() {
        randomizeColor();
        randomizeRotationAxis();
        if(!buffersInitialized) {

            ByteBuffer dpyramideVerticesBB = ByteBuffer.allocateDirect(dpyramide_vertices.length * 4);
            dpyramideVerticesBB.order(ByteOrder.nativeOrder());
            dpyramideVerticesBuffer = dpyramideVerticesBB.asFloatBuffer();
            dpyramideVerticesBuffer.put(dpyramide_vertices);
            dpyramideVerticesBuffer.position(0);

            ByteBuffer dpyramideTrianglesBB = ByteBuffer.allocateDirect(dpyramide_triangles.length * 2);
            dpyramideTrianglesBB.order(ByteOrder.nativeOrder());
            dpyramideTrianglesBuffer = dpyramideTrianglesBB.asShortBuffer();
            dpyramideTrianglesBuffer.put(dpyramide_triangles);
            dpyramideTrianglesBuffer.position(0);

            buffersInitialized = true;
        }
    }

    @Override
    public void draw(GL10 gl) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();
        {
            gl.glMultMatrixf(transformationMatrix, 0);
            gl.glScalef(scale, scale, scale);

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glLineWidth(1.0f);

            gl.glRotatef(rotation, rotationAxis[0], rotationAxis[1], rotationAxis[2]);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, dpyramideVerticesBuffer);
            gl.glColor4f(currentColor[0], currentColor[1], currentColor[2], 0);
            for(int i = 0; i < (dpyramide_triangles.length / 3); i++) {
                dpyramideTrianglesBuffer.position(3 * i);
                gl.glDrawElements(GL10.GL_LINE_LOOP, 3, GL10.GL_UNSIGNED_SHORT, dpyramideTrianglesBuffer);
            }
            dpyramideTrianglesBuffer.position(0);

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }
        gl.glPopMatrix();
    }

    @Override
    public void update(float fracSec) {
        updatePosition(fracSec);
        rotation += fracSec * angularVelocity;
    }

    public void randomizeRotationAxis() {
        rotationAxis[0] = (float) Math.random();
        rotationAxis[1] = (float) Math.random();
        rotationAxis[2] = (float) Math.random();
        normalize(rotationAxis);
    }

    public void randomizeColor() {
        // random color
        currentColor[0] = ((float) Math.random() * (colorB[0] - colorA[0])) + colorA[0];
        currentColor[1] = ((float) Math.random() * (colorB[1] - colorA[1])) + colorA[1];
        currentColor[2] = ((float) Math.random() * (colorB[2] - colorA[2])) + colorA[2];
    }

}
