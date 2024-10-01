package de.hs_kl.gatav.flyingsaucerfull.objects;

import static de.hs_kl.gatav.flyingsaucerfull.util.Utilities.normalize;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class BorgCube extends Obstacle {

    // current rotation
    public float rotation = 0.0f;
    // rotation speed in deg/s
    public float angularVelocity = 0.0f;
    public float[] rotationAxis = {0.0f, 1.0f, 0.0f};
    private static final float[] colorA = {0.1f, 0.1f, 0.1f};
    private static final float[] colorB = {1.0f, 1.0f, 1.0f};

    private final float[] currentColor = new float[3];

    private static FloatBuffer borgCubeVerticesBuffer;
    private static ShortBuffer borgCubeQuadsBuffer;
    private static boolean buffersInitialized = false;

    private static final float[] vertices = {
            -0.5f, 0.5f, -0.5f,	// btl 0
            -0.5f, -0.5f, -0.5f,// bbl 1
            0.5f, -0.5f, -0.5f,	// bbr 2
            0.5f, 0.5f, -0.5f,	// btr 3
            -0.5f, 0.5f, 0.5f,	// ftl 4
            -0.5f, -0.5f, 0.5f,	// fbl 5
            0.5f, -0.5f, 0.5f,	// fbr 6
            0.5f, 0.5f, 0.5f	// ftr 7
    };
    private static final short[] quads = {
            3, 2, 1, 0, // back
            4, 5, 6, 7, // front
            0, 4, 7, 3,	// top
            1, 2, 6, 5,	// bottom
            7, 6, 2, 3,	// right
            4, 0, 1, 5	// left
    };
    public BorgCube() {
        randomizeColor();
        randomizeRotationAxis();
        if(!buffersInitialized) {

            ByteBuffer borgCubeVerticesBB = ByteBuffer.allocateDirect(vertices.length * 4);
            borgCubeVerticesBB.order(ByteOrder.nativeOrder());
            borgCubeVerticesBuffer = borgCubeVerticesBB.asFloatBuffer();
            borgCubeVerticesBuffer.put(vertices);
            borgCubeVerticesBuffer.position(0);

            ByteBuffer borgCubeQuadsBB = ByteBuffer.allocateDirect(quads.length * 2);
            borgCubeQuadsBB.order(ByteOrder.nativeOrder());
            borgCubeQuadsBuffer = borgCubeQuadsBB.asShortBuffer();
            borgCubeQuadsBuffer.put(quads);
            borgCubeQuadsBuffer.position(0);

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
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, borgCubeVerticesBuffer);
            gl.glColor4f(currentColor[0], currentColor[1], currentColor[2], 0);
            for(int i = 0; i < (borgCubeQuadsBuffer.capacity() / 4); i++) {
                borgCubeQuadsBuffer.position(4 * i);
                gl.glDrawElements(GL10.GL_LINE_LOOP, 4, GL10.GL_UNSIGNED_SHORT, borgCubeQuadsBuffer);
            }
            borgCubeQuadsBuffer.position(0);

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
