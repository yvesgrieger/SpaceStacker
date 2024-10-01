package de.hs_kl.gatav.flyingsaucerfull.objects;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.Matrix;

public abstract class SpaceObject {
    // current transformation matrix
    public float[] transformationMatrix;
    // current velocity (x,y,z)
    public float[] velocity;
    // current y-rotation, positive is z to x direction; angle zero is z-axis
    public float scale = 1.0f;

    public float speed = 1.0f;	// alternative way to control speed, additional scaling factor

    public SpaceObject() {
        transformationMatrix = new float[16];
        velocity = new float[3];
        Matrix.setIdentityM(transformationMatrix, 0);
    }

    public abstract void draw(GL10 gl);

    public abstract void update(float fracSec);

    public void setVelocity(float vx, float vy, float vz) {
        velocity[0] = vx;
        velocity[1] = vy;
        velocity[2] = vz;
    }

    protected void updatePosition(float fracSec) {
        Matrix.translateM(transformationMatrix, 0, fracSec*velocity[0] * speed,
                fracSec*velocity[1] * speed,//==0 einfach, dann nur bewegung auf der x-achse. Allerdings bei allen obstalce
                -0.03f);//deshalb extra methode und hier azuch = 0 machen in der zeile
    }

    public void setPosition(float x, float y, float z) {
        Matrix.setIdentityM(transformationMatrix, 0);
        Matrix.translateM(transformationMatrix, 0, x, y, z);
    }


    public float getX() {
        return transformationMatrix[12];
    }

    public float getY() {
        return transformationMatrix[13];
    }

    public float getZ() {
        return transformationMatrix[14];
    }

    public void setX(float x) {
        transformationMatrix[12] = x;
    }

    public void setZ(float z) {
        transformationMatrix[14] = z;
    }
}

