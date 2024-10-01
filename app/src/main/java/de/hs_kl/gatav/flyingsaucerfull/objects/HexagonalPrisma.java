package de.hs_kl.gatav.flyingsaucerfull.objects;

import static de.hs_kl.gatav.flyingsaucerfull.util.Utilities.normalize;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class HexagonalPrisma extends Obstacle {

    // current rotation
    public float rotation = 0.0f;
    // rotation speed in deg/s
    public float angularVelocity = 0.0f;
    public float[] rotationAxis = {0.0f, 1.0f, 0.0f};
    private static final float[] colorA = {0.1f, 0.1f, 0.1f};
    private static final float[] colorB = {1.0f, 1.0f, 1.0f};

    private final float[] currentColor = new float[3];

    private static FloatBuffer hexaVerticesBuffer;
    private static ShortBuffer hexaHexagonBuffer;
    private static boolean buffersInitialized = false;

    private static final float[] hexa_vertices = {
            0.5f, 0.5f, 0.0f, //topfront
            0.27429f, 0.5f, 0.5f,  //toprightfront
            -0.27429f, 0.5f, 0.5f,   //toprightback
            -0.5f, 0.5f, 0.0f,  //topback
            -0.27429f, 0.5f, -0.5f,  //topleftback
            0.27429f, 0.5f, -0.5f,  //topleftfront
            0.5f, -0.5f, 0.0f, //bottomfront
            0.27429f, -0.5f, 0.5f,  //bottomrightfront
            -0.27429f, -0.5f, 0.5f,   //bottomrightback
            -0.5f, -0.5f, 0.0f,  //bottomback
            -0.27429f, -0.5f, -0.5f,  //bottomleftback
            0.27429f, -0.5f, -0.5f,  //bottomleftfront
            0.5f, 0.0f, 0.0f, //midfront
            0.27429f, 0.0f, 0.5f,  //midrightfront
            -0.27429f, 0.0f, 0.5f,   //midrightback
            -0.5f, 0.0f, 0.0f,  //midback
            -0.27429f, 0.0f, -0.5f,  //midleftback
            0.27429f, 0.0f, -0.5f  //midleftfront
    };
    private static final short[] hexa_hexagon = {
            0,1,2,3,4,5,
            11,10,9,8,7,6,
            0,1,13,7,6,12,
            1,2,14,8,7,13,
            2,3,15,9,8,14,
            3,4,16,10,9,15,
            4,5,17,11,10,16,
            5,0,12,6,11,17

    };
    public HexagonalPrisma() {
        randomizeColor();
        randomizeRotationAxis();
        if(!buffersInitialized) {

            ByteBuffer hexaVerticesBB = ByteBuffer.allocateDirect(hexa_vertices.length * 4);
            hexaVerticesBB.order(ByteOrder.nativeOrder());
            hexaVerticesBuffer = hexaVerticesBB.asFloatBuffer();
            hexaVerticesBuffer.put(hexa_vertices);
            hexaVerticesBuffer.position(0);

            ByteBuffer hexaHexagonBB = ByteBuffer.allocateDirect(hexa_hexagon.length * 2);
            hexaHexagonBB.order(ByteOrder.nativeOrder());
            hexaHexagonBuffer = hexaHexagonBB.asShortBuffer();
            hexaHexagonBuffer.put(hexa_hexagon);
            hexaHexagonBuffer.position(0);

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
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, hexaVerticesBuffer);
            gl.glColor4f(currentColor[0], currentColor[1], currentColor[2], 0);
            for(int i = 0; i < (hexa_hexagon.length / 6); i++) {
                hexaHexagonBuffer.position(6 * i);
                gl.glDrawElements(GL10.GL_LINE_LOOP, 6, GL10.GL_UNSIGNED_SHORT, hexaHexagonBuffer);
            }
            hexaHexagonBuffer.position(0);

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
