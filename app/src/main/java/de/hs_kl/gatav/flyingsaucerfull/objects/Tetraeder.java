package de.hs_kl.gatav.flyingsaucerfull.objects;

import static de.hs_kl.gatav.flyingsaucerfull.util.Utilities.normalize;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Tetraeder extends Obstacle {

    // current rotation
    public float rotation = 0.0f;
    // rotation speed in deg/s
    public float angularVelocity = 0.0f;
    public float[] rotationAxis = {0.0f, 1.0f, 0.0f};
    private static final float[] colorA = {0.1f, 0.1f, 0.1f};
    private static final float[] colorB = {1.0f, 1.0f, 1.0f};

    private final float[] currentColor = new float[3];

    private static FloatBuffer tetraederVerticesBuffer;
    private static ShortBuffer tetraederTrianglesBuffer;
    private static boolean buffersInitialized = false;

    private static final float[] tetraeder_vertices = {
            0.5f, -0.5f, 0.5f,	// frontbottomleft 0
            -0.5f, -0.5f, 0.5f,// frontbottomright 1
            0.0f, -0.5f, -0.5f,	// backbootommid 2
            0.0f, 0.5f, 0.0f,	// midtopmid 3
    };
    private static final short[] tetraeder_triangles = {
            2, 0, 1,  // bottom
            1, 0, 3,  // front
            0, 2, 3, 	// top
            2, 1, 3, 	// bottom
    };
    public Tetraeder() {
        randomizeColor();
        randomizeRotationAxis();
        if(!buffersInitialized) {

            ByteBuffer tetraederVerticesBB = ByteBuffer.allocateDirect(tetraeder_vertices.length * 4);
            tetraederVerticesBB.order(ByteOrder.nativeOrder());
            tetraederVerticesBuffer = tetraederVerticesBB.asFloatBuffer();
            tetraederVerticesBuffer.put(tetraeder_vertices);
            tetraederVerticesBuffer.position(0);

            ByteBuffer tetraederTrianglesBB = ByteBuffer.allocateDirect(tetraeder_triangles.length * 2);
            tetraederTrianglesBB.order(ByteOrder.nativeOrder());
            tetraederTrianglesBuffer = tetraederTrianglesBB.asShortBuffer();
            tetraederTrianglesBuffer.put(tetraeder_triangles);
            tetraederTrianglesBuffer.position(0);

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
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, tetraederVerticesBuffer);
            gl.glColor4f(currentColor[0], currentColor[1], currentColor[2], 0);
            for(int i = 0; i < (tetraeder_triangles.length / 3); i++) {
                tetraederTrianglesBuffer.position(3 * i);
                gl.glDrawElements(GL10.GL_LINE_LOOP, 3, GL10.GL_UNSIGNED_SHORT, tetraederTrianglesBuffer);
            }
            tetraederTrianglesBuffer.position(0);

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
