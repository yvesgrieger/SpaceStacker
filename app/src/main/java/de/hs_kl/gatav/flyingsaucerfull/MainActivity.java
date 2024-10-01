package de.hs_kl.gatav.flyingsaucerfull;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends Activity implements SensorEventListener {

    public static MediaPlayer mPlayerexplosion;
    public static MediaPlayer mPlayermainmusic;
    public static MediaPlayer mPlayerpulsarcoming;
    public static boolean mPlayermainmusicisPlaying = true;
    private SpaceGLSurfaceView spaceGLSurfaceView;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Display mDisplay;
    TextView mTextView;
    TextView nTextView;
    public static boolean merker_view_wechsel =false;
    private ImageView animView;
    private AnimationDrawable drawableAnimation;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mPlayerexplosion = MediaPlayer.create(this, R.raw.explosion);
        mPlayerexplosion.setLooping(false);
        mPlayermainmusic = MediaPlayer.create(this, R.raw.spacemusic);
        mPlayermainmusic.setLooping(true);
        mPlayerpulsarcoming = MediaPlayer.create(this, R.raw.pulsarcoming);
        mPlayerpulsarcoming.setLooping(false);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        spaceGLSurfaceView = (SpaceGLSurfaceView) findViewById(R.id.spaceView);
        spaceGLSurfaceView.context=this;
        animView = (ImageView) findViewById(R.id.explosionView);
        drawableAnimation = (AnimationDrawable) animView.getDrawable();
        mPlayermainmusic.start();

        nTextView = new TextView(this);
        nTextView.setText("Punkte: " + spaceGLSurfaceView.punktecounter );
        nTextView.setTextColor(Color.WHITE);
        nTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.TOP;
        addContentView(nTextView,params);
    }
    protected void startExplosion(float x, float y) {
        float scale = 1.5f;
        animView.setScaleX(scale);
        animView.setScaleY(scale);

        // Abrufen der skalierten Größe der Animation
        int animationWidth = (int) (animView.getDrawable().getIntrinsicWidth());
        int animationHeight = (int) (animView.getDrawable().getIntrinsicHeight());

        int screenWidth = spaceGLSurfaceView.getWidth();
        int screenHeight = spaceGLSurfaceView.getHeight();

        float boundaryLeft = SpaceGLSurfaceView.boundaryLeft;
        float boundaryRight = SpaceGLSurfaceView.boundaryRight;
        float boundaryTop = SpaceGLSurfaceView.boundaryTop;
        float boundaryBottom = SpaceGLSurfaceView.boundaryBottom;

        int pixelX = (int) ((x - boundaryLeft) / (boundaryRight - boundaryLeft) * screenWidth - animationWidth / 2);
        int pixelY = (int) ((1 - (y - boundaryBottom) / (boundaryTop - boundaryBottom)) * screenHeight - animationHeight / 2);

        animView.setX(pixelX);
        animView.setY(pixelY);

        animView.setVisibility(View.VISIBLE);
        animView.post(
                () -> {
                    drawableAnimation.start();

                    // Berechne Dauer der Animation
                    int duration = 0;
                    for (int i = 0; i < drawableAnimation.getNumberOfFrames(); i++) {
                        duration += drawableAnimation.getDuration(i);
                    }

                    // Handler erstellen, um die ImageView nach der Animation unsichtbar zu machen
                    new Handler().postDelayed(() -> {
                        animView.setVisibility(View.INVISIBLE);
                        drawableAnimation.stop();
                    }, duration);
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        if(mPlayermainmusicisPlaying) {
            mPlayermainmusic.start();
        }else{
            mPlayerpulsarcoming.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        if(mPlayermainmusic.isPlaying()) {
            mPlayermainmusic.stop();
            try {
                mPlayermainmusic.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            mPlayerpulsarcoming.stop();
            try {
                mPlayerpulsarcoming.prepare();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @SuppressLint("SetTextI18n")
    private void restartGame() {
        setContentView(R.layout.activity_main); // Ursprünglichje Layout wiederherstellen
        spaceGLSurfaceView = (SpaceGLSurfaceView) findViewById(R.id.spaceView);
        spaceGLSurfaceView.context=this;
        mPlayermainmusic.stop();
        try {
            mPlayermainmusic.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mPlayermainmusic.start();
        nTextView = new TextView(this);
        nTextView.setText("Punkte: " + spaceGLSurfaceView.punktecounter );
        nTextView.setTextColor(Color.WHITE);
        nTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.TOP;
        addContentView(nTextView,params);
        // Erneute Initialisierung der ImageView, um die Animation nach Neustart laden zu können
        // Initialisierung verschoben in post Runnable um NullPointerException zu vermeiden
        // Post wird erst aufgeführt wenn die View vollständig geladen ist
        spaceGLSurfaceView.post(() -> {
            animView = (ImageView) findViewById(R.id.explosionView);
            drawableAnimation = (AnimationDrawable) animView.getDrawable();
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        switch(mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                //Log.d("rot", ""+0);
                spaceGLSurfaceView.setObstacleVelocity(-event.values[0], 0, -event.values[1]);
                break;
            case Surface.ROTATION_90:
                //Log.d("rot", ""+90);
                spaceGLSurfaceView.setObstacleVelocity(event.values[1], 0, -event.values[0]);
                break;
            case Surface.ROTATION_180:
                //Log.d("rot", ""+180);
                spaceGLSurfaceView.setObstacleVelocity(event.values[0], 0, event.values[1]);
                break;
            case Surface.ROTATION_270:
                //Log.d("rot", ""+270);
                spaceGLSurfaceView.setObstacleVelocity(-event.values[1], 0, event.values[0]);
                break;
        }

        if(SpaceGLSurfaceView.gameMode==1){
            nTextView.setText("Punkte: " + spaceGLSurfaceView.punktecounter );
        }
        if(SpaceGLSurfaceView.gameMode==0 && !merker_view_wechsel){
            nTextView.setText("");
            mTextView = new TextView(this);
            mTextView.setText("Game Over\nDein Score: "+(spaceGLSurfaceView.punktecounter-1)+" \nFür Neustart tippen");
            mTextView.setTextColor(Color.WHITE);
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,32);
            FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.gravity= Gravity.CENTER;
            addContentView(mTextView,params);
        }
        if(merker_view_wechsel){
            restartGame();
            //setContentView(spaceGLSurfaceView);
            merker_view_wechsel =false;
        }
    }
}