package de.hs_kl.gatav.flyingsaucerfull;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;
import de.hs_kl.gatav.flyingsaucerfull.objects.Asteroid;
import de.hs_kl.gatav.flyingsaucerfull.objects.BorgCube;
import de.hs_kl.gatav.flyingsaucerfull.objects.Tetraeder;
import de.hs_kl.gatav.flyingsaucerfull.objects.Doppelpyramide;
import de.hs_kl.gatav.flyingsaucerfull.objects.HexagonalPrisma;
import de.hs_kl.gatav.flyingsaucerfull.objects.Obstacle;
import de.hs_kl.gatav.flyingsaucerfull.objects.Pulsar;
import de.hs_kl.gatav.flyingsaucerfull.objects.SpaceObject;
import my.pack.graphics.primitives.Cylinder;

public class SpaceGLSurfaceView extends GLSurfaceView {

    public static float boundaryTop, boundaryBottom, boundaryLeft, boundaryRight;
    private boolean spawnPulsar = false;
    private boolean detonatePulsar = false;
    public Context context;  // activity context
    private static Obstacle currentObstacle =new Asteroid();
    private final float[] transformationMatrixFuerTodeslinie = new float[16];
    Cylinder todeslinie=new Cylinder(0.01f,0.01f,10.0f,100,1,true,true);
    public static int gameMode=1;
//    private int merkerNeustart=0;
    public final List<Obstacle> obstacles = new ArrayList<>();
    public List<Pulsar> pulsars = new ArrayList<>();
    public int pulsarCounter=1;
    public int punktecounter = 0;
    private static final float MinScale = 0.8f;
    private static final float MaxScale = 1.0f;

    public SpaceGLSurfaceView(Context context) {
        super(context);
        SpaceRenderer renderer = new SpaceRenderer();
        setRenderer(renderer);

        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
    // Constructor for XML layout file called in MainActivity restartGame() *setContentView(R.layout.activity_main);*
    public SpaceGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SpaceRenderer renderer = new SpaceRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    // called from sensor
    public void setObstacleVelocity(float vx, float vy, float vz) {
        if(currentObstacle!=null) currentObstacle.setVelocity(vx, vy, vz);
    }

    // Spawn LaBomba
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            if (gameMode == 1) {
                if (pulsarCounter<=3){
                    if (spawnPulsar){
                        Toast.makeText(context, "Bombe ist schon aktiviert!", Toast.LENGTH_SHORT).show();
                    } else if (detonatePulsar) {
                        Toast.makeText(context, "Warte erstmal diese Bombe ab", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Pulsar Activated!"+pulsarCounter+"/3", Toast.LENGTH_SHORT).show();
                        spawnPulsar = true;
                        pulsarCounter++;
                    }
                }
                else {
                    Toast.makeText(context, "Es gibt keine weiteren Bomben mehr!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Neue Runde neues Glück", Toast.LENGTH_SHORT).show();
                MainActivity.merker_view_wechsel = true;
                resetGame();
            }
        }
        return true;
    }


    private class SpaceRenderer implements Renderer {
        private final float[] modelViewScene = new float[16];
//        private SurfaceHolder sh= getHolder();

        long lastFrameTime;

        public SpaceRenderer() {
            lastFrameTime = System.currentTimeMillis();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            // update time calculation
            long delta = System.currentTimeMillis() - lastFrameTime;
            float fracSec = (float) delta / 1000;
            lastFrameTime = System.currentTimeMillis();

            // scene updates
            if(gameMode==1){
                updateShip(fracSec,gl);
            }
            if(gameMode==1){
                updateObstacles(fracSec,gl);
            }




            // clear screen and depth buffer
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            GL11 gl11 = (GL11) gl;

            // load local system to draw scene items
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl11.glLoadMatrixf(modelViewScene, 0);
            //
            for (Pulsar pulsar: pulsars) {
                pulsar.draw(gl);
            }
            //
            drawTodeslinie(gl);
            //todeslinie.draw(gl);
            if(currentObstacle!=null)currentObstacle.draw(gl);
            for (Obstacle obstacle: obstacles) {
                obstacle.draw(gl);
            }

        }
        private void drawTodeslinie(GL10 gl) {
            Matrix.setIdentityM(transformationMatrixFuerTodeslinie, 0);
            Matrix.translateM(transformationMatrixFuerTodeslinie, 0, -5, 0, 3);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glPushMatrix();
            {
                gl.glMultMatrixf(transformationMatrixFuerTodeslinie, 0);
                gl.glScalef(1.0f, 1.0f, 1.0f);

                gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

                gl.glLineWidth(1.0f);
                gl.glRotatef(90, 0, 1, 0);
                gl.glColor4f(1,0,0,0);
                todeslinie.draw(gl);
                gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            }
            gl.glPopMatrix();
        }


        private void updateShip(float fracSec,GL10 gl) {
            if (currentObstacle != null) {
                currentObstacle.update(fracSec);

                // Überprüfen, ob currentObstacle ein BorgCube ist
                //if (currentObstacle instanceof BorgCube) {
                    //BorgCube borgCube = (BorgCube) currentObstacle;
                    // keep ship within window boundaries
                    if (currentObstacle.getX() < boundaryLeft + currentObstacle.scale / 2) {
                        currentObstacle.setX(boundaryLeft + currentObstacle.scale / 2);
                        obstacles.add(currentObstacle);
                        currentObstacle = collision(gl);
                        return;
                    }
                    if (currentObstacle.getX() > boundaryRight - currentObstacle.scale / 2) {
                        currentObstacle.setX(boundaryRight - currentObstacle.scale / 2);
                        obstacles.add(currentObstacle);
                        currentObstacle = collision(gl);
                        return;
                    }
                    if (currentObstacle.getZ() < boundaryBottom + currentObstacle.scale / 2) {
                        currentObstacle.setZ(boundaryBottom + currentObstacle.scale / 2);
                        obstacles.add(currentObstacle);
                        currentObstacle = collision(gl);
                    }

            } else {
            currentObstacle = collision(gl); // Setze ein neues Hindernis, wenn kein aktuelles Hindernis vorhanden ist
            }
        }


        private boolean areColliding(SpaceObject obj1, SpaceObject obj2) {
            float obj1X = obj1.getX();
            float obj1Z = obj1.getZ();
            float obj2X = obj2.getX();
            float obj2Z = obj2.getZ();
            float squaredHitDistance = ((obj1.scale + obj2.scale) / 2) * ((obj1.scale + obj2.scale) / 2);
            float squaredDistance = (obj1X - obj2X) * (obj1X - obj2X) + (obj1Z - obj2Z) * (obj1Z - obj2Z);

            return squaredDistance < squaredHitDistance;
        }


        private void updateObstacles(float fracSec,GL10 gl) {
            // obstacle collision with space ship
            boolean merkerKollision=false;
            for (Obstacle obstacle : obstacles) {
                if (areColliding(currentObstacle, obstacle)) {
                    merkerKollision=true;

                }
            }
            if(merkerKollision){
            obstacles.add(currentObstacle);
            currentObstacle = collision(gl);
            }

            // Update position of pulsars
            for (Pulsar pulsar : pulsars) {
                pulsar.update(fracSec);
                if (pulsar.getZ() < boundaryBottom + pulsar.scale / 2){
                    pulsar.setZ(boundaryBottom + pulsar.scale / 2);
                }
            }
        }
        @Override
        // Called when surface is created or the viewport gets resized
        // set projection matrix
        // precalculate modelview matrix
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GL11 gl11 = (GL11) gl;
            gl.glViewport(0, 0, width, height);

            float aspectRatio = (float) width / height;
            float fovy = 45.0f;

            // set up projection matrix for scene
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            GLU.gluPerspective(gl, fovy, aspectRatio, 0.001f, 100.0f);

            // set up modelview matrix for scene
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();

            float desired_height=10.0f;
            // We want to be able to see the range of 5 to -5 units at the y
            // axis (height=10).
            // To achieve this we have to pull the camera towards the positive z axis
            // based on the following formula:
            // z = (desired_height / 2) / tan(fovy/2)
            float z = (float) (desired_height / 2 / Math.tan(fovy / 2 * (Math.PI / 180.0f)));
            // forward for the camera is backward for the scene
            gl.glTranslatef(0.0f, 0.0f, -z);
            // rotate local to achive top down view from negative y down to xz-plane
            // z range is the desired height
            gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
            // save local system as a basis to draw scene items
            gl11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelViewScene, 0);

            // window boundaries
            // z range is the desired height
            boundaryTop = desired_height/2;
            boundaryBottom = -desired_height/2;
            // x range is the desired width
            boundaryLeft = -(desired_height/2 * aspectRatio);
            boundaryRight = (desired_height/2 * aspectRatio);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glDisable(GL10.GL_DITHER);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glShadeModel(GL10.GL_FLAT);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glDepthFunc(GL10.GL_LEQUAL);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);
        }

        public Obstacle collision(GL10 gl){
            punktecounter++;
//            boolean zuNah=false;
            int sourceCode = ((Math.random()<0.5?0:1)<<1) | (Math.random()<0.5?0:1);
            float spawnX = (sourceCode&1)>0?boundaryRight*(float)Math.random() : boundaryLeft*(float)Math.random();
            if (spawnX<boundaryLeft+1.5){
                spawnX=-1.0f;
            }
            if (spawnX>boundaryRight-1.5){
                spawnX=1.0f;
            }
            for(Obstacle obstacle: obstacles) {
                if(obstacle.getZ()+obstacle.scale/2>boundaryTop-2 && !(currentObstacle instanceof Pulsar)){
                    verloren();
                    return null;
                }
            }// Distance too small -> invalid position

            if (spawnPulsar) {
                MainActivity.mPlayermainmusic.stop();
                try {
                    MainActivity.mPlayermainmusic.prepare();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                MainActivity.mPlayerpulsarcoming.start();
                MainActivity.mPlayermainmusicisPlaying = false;
                Pulsar newPulsar = new Pulsar();
                newPulsar.setPosition(spawnX, 0, boundaryTop);
                newPulsar.scale = 0.5f;
                newPulsar.randomizeRotationAxis();
                newPulsar.angularVelocity = 50;
                spawnPulsar = false;
                detonatePulsar = true;
                return newPulsar;
            } else {
                if(detonatePulsar){
                    punktecounter--;
                    collisionPulsar(currentObstacle, 2.0f);
                    detonatePulsar = false;
                    gl.glDisable(GL10.GL_LIGHT0);
                    gl.glDisable(GL10.GL_LIGHTING);
                    MainActivity.mPlayermainmusic.start();
                    MainActivity.mPlayermainmusicisPlaying = true;
                }
                Obstacle newObstacle;



                float random = (float)Math.random();
                if (random < 1/5f) {
                    Asteroid newAsteroid = new Asteroid();
                    newAsteroid.setPosition(spawnX, 0, boundaryTop);
                    newAsteroid.scale = (float) Math.random() * (MaxScale - MinScale) + MinScale;
                    newAsteroid.randomizeRotationAxis();
                    newAsteroid.angularVelocity = 50;
                    newObstacle = newAsteroid;
                } else if(random < 2/5f) {
                    BorgCube newBorgCube = new BorgCube();
                    newBorgCube.setPosition(spawnX, 0, boundaryTop);
                    newBorgCube.scale = (float) Math.random() * (MaxScale - MinScale) + MinScale - 0.15f;
                    newBorgCube.randomizeRotationAxis();
                    newBorgCube.angularVelocity= 50;
                    newObstacle = newBorgCube;
                } else if(random < 3/5f) {
                    Tetraeder newTetraeder = new Tetraeder();
                    newTetraeder.setPosition(spawnX, 0, boundaryTop);
                    newTetraeder.scale = (float) Math.random() * (MaxScale - MinScale) + MinScale ;
                    newTetraeder.randomizeRotationAxis();
                    newTetraeder.angularVelocity= 50;
                    newObstacle = newTetraeder;
                } else if(random < 4/5f){
                    Doppelpyramide newDoppelpyramide = new Doppelpyramide();
                    newDoppelpyramide.setPosition(spawnX, 0, boundaryTop);
                    newDoppelpyramide.scale = (float) Math.random() * (MaxScale - MinScale) + MinScale ;
                    newDoppelpyramide.randomizeRotationAxis();
                    newDoppelpyramide.angularVelocity= 50;
                    newObstacle = newDoppelpyramide;
                } else{
                    HexagonalPrisma newHexa = new HexagonalPrisma();
                    newHexa.setPosition(spawnX, 0, boundaryTop);
                    newHexa.scale = (float) Math.random() * (MaxScale - MinScale) + MinScale;
                    newHexa.randomizeRotationAxis();
                    newHexa.angularVelocity = 50;
                    newObstacle = newHexa;
                }

                //punktecounter++;

                return newObstacle;
            }



        }
        public void collisionPulsar(Obstacle pulsar, float radius){
            List<Obstacle> toRemove = new ArrayList<>();

            for(Obstacle obstacle: obstacles) {
                float distance = (float) Math.sqrt(Math.pow(pulsar.getX() - obstacle.getX(), 2) + Math.pow(pulsar.getZ() - obstacle.getZ(), 2));

                if(distance <= radius) {
                    toRemove.add(obstacle);
                }
            }
            // Start explosion animation at the position of the pulsar
            MainActivity mainActivity = (MainActivity) context;
            mainActivity.runOnUiThread(() -> mainActivity.startExplosion(pulsar.getX(), pulsar.getZ()));
            MainActivity.mPlayerpulsarcoming.stop();
            MainActivity.mPlayermainmusicisPlaying = true;
            try {
                MainActivity.mPlayerpulsarcoming.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            MainActivity.mPlayerexplosion.start();


            synchronized (obstacles) { // Synchronize to avoid concurrent modification (ConcurrentModificationException)
                obstacles.removeAll(toRemove);
            }

        }
    }
    public void verloren(){
        gameMode=0;
    }
    public void resetGame() {
        // Reset game state
        gameMode = 1;
        obstacles.clear();
        pulsars.clear();
        currentObstacle = new Asteroid();
        currentObstacle.setPosition(0,0,boundaryTop);
        pulsarCounter=1;
    }

}


