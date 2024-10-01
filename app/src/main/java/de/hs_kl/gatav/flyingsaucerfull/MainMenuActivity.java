package de.hs_kl.gatav.flyingsaucerfull;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
            startActivity(intent);
        });
        Button erklaerButton= findViewById(R.id.erklaer_button);
        erklaerButton.setOnClickListener(M -> {
            Intent intent = new Intent(MainMenuActivity.this, ErklaerActivity.class);
            startActivity(intent);
        });

        // Create a pulsating effect by animating the background color
        ObjectAnimator animator = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            animator = ObjectAnimator.ofArgb(startButton, "backgroundColor",
                    Color.parseColor("#FF6347"), Color.parseColor("#8B0000"));
        }
        assert animator != null;
        animator.setDuration(1000);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }
}