package de.hs_kl.gatav.flyingsaucerfull.objects;

import static de.hs_kl.gatav.flyingsaucerfull.util.Utilities.normalize;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


import my.pack.graphics.primitives.Cylinder;
import my.pack.graphics.primitives.Sphere;

public class Pulsar extends Obstacle {
    static float[] CURRENT_QUATERNION, LAST_QUATERNION;
    static float[] TRANSFORM_MATRIX;
    private final Sphere sphere;
    private final Sphere smallSphere;
    private final Cylinder cone;
    public float rotation = 0.0f; // rotation speed in deg/s
    public float angularVelocity = 0.0f;
    public float[] rotationAxis = {0.0f, 1.0f, 0.0f};

    static {
        CURRENT_QUATERNION = new float[4];
        LAST_QUATERNION = new float[4];
        TRANSFORM_MATRIX = new float[16];
    }
    public Pulsar() {
        sphere = new Sphere(0.25f, 100, 100);
        smallSphere = new Sphere(0.04f, 100, 100);
        cone = new Cylinder(0.05f, 0.0f, 0.125f, 50, 2, true, false);
    }
    public void setMaterialColor(GL10 gl, float r, float g, float b, float a) { // Set material color
        float[] materialColor = {r, g, b, a};
        ByteBuffer bb = ByteBuffer.allocateDirect(materialColor.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(materialColor);
        fb.position(0);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, fb);
    }

    public void setBlinkingMaterialColor(GL10 gl) {
        long currentTime = System.currentTimeMillis();
        if (currentTime % 1000 < 500) {
            // Set color to red for the first half of each second
            setMaterialColor(gl, 1.0f, 0.0f, 0.0f, 1.0f);
        } else {
            // Set color back to original for the second half of each second
            setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        }
    }

    public void draw(GL10 gl) {

        // set ambient light color
        float[] model_ambient = {0.5f, 0.5f, 0.5f, 1.0f};
        ByteBuffer bb1 = ByteBuffer.allocateDirect(model_ambient.length * 4);
        bb1.order(ByteOrder.nativeOrder());
        FloatBuffer fb1 = bb1.asFloatBuffer();
        fb1.put(model_ambient);
        fb1.position(0);
        gl.glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, fb1);

        // set light position of LIGHT0
        float[] light_position = {1.0f, 1.0f, 1.0f, 0.0f};
        ByteBuffer bb2 = ByteBuffer.allocateDirect(light_position.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        FloatBuffer fb2 = bb2.asFloatBuffer();
        fb2.put(light_position);
        fb2.position(0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, fb2);

        // enable ligth and lighting
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glEnable(GL10.GL_LIGHTING);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        // gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glColor4x(65536, 0, 0, 65536);
        float[] materialAmbient = {0.25f, 0.25f, 0.25f, 1.0f};
        float[] materialDiffuse = {0.4f, 0.4f, 0.4f, 1.0f};
        float[] materialSpecular = {0.774597f, 0.774597f, 0.774597f, 1.0f};
        float materialShininess = 20f;

        ByteBuffer bbAmbient = ByteBuffer.allocateDirect(materialAmbient.length * 4);
        bbAmbient.order(ByteOrder.nativeOrder());
        FloatBuffer fbAmbient = bbAmbient.asFloatBuffer();
        fbAmbient.put(materialAmbient);
        fbAmbient.position(0);

        ByteBuffer bbDiffuse = ByteBuffer.allocateDirect(materialDiffuse.length * 4);
        bbDiffuse.order(ByteOrder.nativeOrder());
        FloatBuffer fbDiffuse = bbDiffuse.asFloatBuffer();
        fbDiffuse.put(materialDiffuse);
        fbDiffuse.position(0);

        ByteBuffer bbSpecular = ByteBuffer.allocateDirect(materialSpecular.length * 4);
        bbSpecular.order(ByteOrder.nativeOrder());
        FloatBuffer fbSpecular = bbSpecular.asFloatBuffer();
        fbSpecular.put(materialSpecular);
        fbSpecular.position(0);

        // Apply the material properties
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, fbAmbient);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, fbDiffuse);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, fbSpecular);
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, materialShininess);

        // Anwenden der Positionierung auf das gesamte Objekt
        // Save the current matrix
        gl.glPushMatrix();
        // Move to the position of the pulsar
        gl.glTranslatef(this.getX(), this.getY(), this.getZ());

        // Anwenden der Rotation auf das gesamte Objekt
        gl.glPushMatrix();
        gl.glRotatef(rotation, rotationAxis[0], rotationAxis[1], rotationAxis[2]);

        // SPHERE
        gl.glPushMatrix();
        sphere.draw(gl);
        gl.glPopMatrix();


        float orthogonalConeDistance = 0.24f; //Abstand Kugelmittelpunkt zu Kegelmittelpunkt (etwas kleiner als Radius)
        float coneHeight = 0.125f; //Höhe des Kegels

        // CONE front
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, orthogonalConeDistance);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f); // Set color to original
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, orthogonalConeDistance + coneHeight);
        setBlinkingMaterialColor(gl); // Set color to blinking red
        smallSphere.draw(gl);
        gl.glPopMatrix();

        // CONE back
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -orthogonalConeDistance);
        gl.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f); // Set color back to original
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -orthogonalConeDistance - coneHeight);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();

        // CONE top
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, orthogonalConeDistance, 0.0f);
        gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, orthogonalConeDistance + coneHeight, 0.0f);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();

        // CONE bottom
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, -orthogonalConeDistance, 0.0f);
        gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, -orthogonalConeDistance - coneHeight, 0.0f);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();

        // CONE left
        gl.glPushMatrix();
        gl.glTranslatef(-orthogonalConeDistance, 0.0f, 0.0f);
        gl.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(-orthogonalConeDistance - coneHeight, 0.0f, 0.0f);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();

        // CONE right
        gl.glPushMatrix();
        gl.glTranslatef(orthogonalConeDistance, 0.0f, 0.0f);
        gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(orthogonalConeDistance + coneHeight, 0.0f, 0.0f);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();

        float diagonalConeDistance = (float) (orthogonalConeDistance * 1 / Math.sqrt(3));
        float diagonalSphereDistance = (float) ((orthogonalConeDistance + coneHeight) * 1 / Math.sqrt(3));

        float angleFront = 54.735610317245360f;
        float angleBack = 125.264389682754640f;

        // CONE front top right
        gl.glPushMatrix();
        gl.glTranslatef(diagonalConeDistance, diagonalConeDistance, diagonalConeDistance);
        gl.glRotatef(angleFront, -1.0f, 1.0f, 0.0f);
        gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(diagonalSphereDistance, diagonalSphereDistance, diagonalSphereDistance);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();


        // CONE front top left
        gl.glPushMatrix();
        gl.glTranslatef(-diagonalConeDistance, diagonalConeDistance, diagonalConeDistance);
        gl.glRotatef(angleFront, -1.0f, -1.0f, 0.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(-diagonalSphereDistance, diagonalSphereDistance, diagonalSphereDistance);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();

        // CONE front bottom left
        gl.glPushMatrix();
        gl.glTranslatef(-diagonalConeDistance, -diagonalConeDistance, diagonalConeDistance);
        gl.glRotatef(angleFront, 1.0f, -1.0f, 0.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(-diagonalSphereDistance, -diagonalSphereDistance, diagonalSphereDistance);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();

        // CONE front bottom right
        gl.glPushMatrix();
        gl.glTranslatef(diagonalConeDistance, -diagonalConeDistance, diagonalConeDistance);
        gl.glRotatef(angleFront, 1.0f, 1.0f, 0.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(diagonalSphereDistance, -diagonalSphereDistance, diagonalSphereDistance);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();

        // CONE back top right
        gl.glPushMatrix();
        gl.glTranslatef(diagonalConeDistance, diagonalConeDistance, -diagonalConeDistance);
        gl.glRotatef(angleBack, -1.0f, 1.0f, 0.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(diagonalSphereDistance, diagonalSphereDistance, -diagonalSphereDistance);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();

        // CONE back top left
        gl.glPushMatrix();
        gl.glTranslatef(-diagonalConeDistance, diagonalConeDistance, -diagonalConeDistance);
        gl.glRotatef(angleBack, -1.0f, -1.0f, 0.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(-diagonalSphereDistance, diagonalSphereDistance, -diagonalSphereDistance);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();

        // CONE back bottom left
        gl.glPushMatrix();
        gl.glTranslatef(-diagonalConeDistance, -diagonalConeDistance, -diagonalConeDistance);
        gl.glRotatef(angleBack, 1.0f, -1.0f, 0.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(-diagonalSphereDistance, -diagonalSphereDistance, -diagonalSphereDistance);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();

        // CONE back bottom right
        gl.glPushMatrix();
        gl.glTranslatef(diagonalConeDistance, -diagonalConeDistance, -diagonalConeDistance);
        gl.glRotatef(angleBack, 1.0f, 1.0f, 0.0f);
        setMaterialColor(gl, 0.25f, 0.25f, 0.25f, 1.0f);
        cone.draw(gl);
        gl.glPopMatrix();
        gl.glPushMatrix();
        gl.glTranslatef(diagonalSphereDistance, -diagonalSphereDistance, -diagonalSphereDistance);
        setBlinkingMaterialColor(gl);
        smallSphere.draw(gl);
        gl.glPopMatrix();


        gl.glPopMatrix(); // Ende der Rotation

        gl.glPopMatrix(); // Ende der Positionierung

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
}