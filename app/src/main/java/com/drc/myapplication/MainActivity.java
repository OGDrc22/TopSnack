package com.drc.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.drc.mytopsnacklibrary.TopSnack;
import com.drc.mytopsnacklibrary.TopSnack2;
import com.drc.mytopsnacklibrary.TopSnack;


public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout coordinatorLayout;
    private ConstraintLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        main = findViewById(R.id.main);
        coordinatorLayout = findViewById(R.id.coordinator);


        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopSnack.defaultTopSnack(MainActivity.this, main, "Hello", "Dismiss", null, null, true, "up");
                int d = TopSnack.getDuration();
                Log.d("TAG", "onClick: " + d);
            }
        });

        Vibrator vibrator = MainActivity.this.getSystemService(Vibrator.class);
        CoordinatorLayout overActivity;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        overActivity = (CoordinatorLayout) inflater.inflate(R.layout.coords, null);
//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) overActivity.getLayoutParams();
//        lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
//        overActivity.setLayoutParams(lp);
//        overActivity.setVisibility(View.GONE);
//        main.addView(overActivity);


        Button btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));// Requires VIBRATE permission

                View view = getCustomView(MainActivity.this, R.layout.custom_snackbar, R.id.imageView, R.drawable.ic_launcher_foreground, R.id.textView1, "Title", R.id.textView2, "Sub title");
                Button action = view.findViewById(R.id.action);
                action.setText("Dismiss");
                action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
//                        SampleOfTopSnack.hideTopSnackManually(MainActivity.this);
                        TopSnack.hideTopSnack(MainActivity.this);
                    }
                });

                TopSnack.createCustomTopSnack(MainActivity.this, main, view, null, null, true, "up");
                int d = TopSnack.getDuration();
                Log.d("TAG", "onClick: " + d);
             }
        });

        TextView textView = findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getCustomView(MainActivity.this, R.layout.custom_snackbar, R.id.imageView, R.drawable.ic_launcher_foreground, R.id.textView1, "I love you LORD", R.id.textView2, "Sub title");
                TopSnack.createCustomTopSnack(MainActivity.this, main, view, null, null, true, null);
            }
        });

    }

    private @NonNull View getCustomView(@NonNull Context context, @NonNull Integer customLayout, @Nullable Integer iconHolder, @Nullable Integer icon, @Nullable Integer mainMessageTextView, @Nullable String mainMessage, @Nullable Integer descriptionTextView, @Nullable String description) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(customLayout, null);
        ImageView imageView = view.findViewById(iconHolder);
        imageView.setImageResource(icon);
        TextView textView, textView1;
        textView = view.findViewById(mainMessageTextView);
        textView1 = view.findViewById(descriptionTextView);
        textView.setText(mainMessage);
        textView1.setText(description);
        return view;
    }
}