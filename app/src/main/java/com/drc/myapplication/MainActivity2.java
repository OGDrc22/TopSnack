package com.drc.myapplication;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity2 extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String TAG = "Swipe detector";
    private float x1, x2, y1, y2;
    private static final int MIN_DISTANCE = 20;
    private GestureDetector gestureDetector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ConstraintLayout main = findViewById(R.id.main);
        CardView cardView = findViewById(R.id.materialCardView);
        TextView textView = findViewById(R.id.tv);

        this.gestureDetector = new GestureDetector(MainActivity2.this, this);

        // Attach the gesture detector to the CardView's touch listener
        cardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();
                        y1 = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        x2 = event.getX();
                        y2 = event.getY();

                        float valueX = x2 - x1;
                        float valueY = y2 - y1;

                        if (Math.abs(valueX) > MIN_DISTANCE) {
                            if (x1 > x2) {
                                // Left to Right swipe
                                Toast.makeText(MainActivity2.this, "L2R on Card", Toast.LENGTH_SHORT).show();
                            } else {
                                // Right to Left swipe
                                Toast.makeText(MainActivity2.this, "R2L on Card", Toast.LENGTH_SHORT).show();
                            }
                        } else if (Math.abs(valueY) > MIN_DISTANCE) {
                            if (y2 > y1) {
                                // Top to Bottom swipe
                                Toast.makeText(MainActivity2.this, "T2B on Card", Toast.LENGTH_SHORT).show();

                                textView.setText("Swipe this");
                            } else {
                                // Bottom to Top swipe
                                Toast.makeText(MainActivity2.this, "B2T on Card", Toast.LENGTH_SHORT).show();

                                textView.setText("Dismissed");
                            }
                        }
                        break;
                }

                return true; // Return true to indicate the touch event is consumed
            }
        });

        int topInset;

        final Resources resources = MainActivity2.this.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            topInset = resources.getDimensionPixelSize(resourceId);
        } else{
            topInset = (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
        }

        CardView btn = findViewById(R.id.materialCardView2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Snackbar sb = Snackbar.make(main,"Sample", Snackbar.LENGTH_INDEFINITE);

                if (sb.isShown()) {
                    Log.d(TAG, "onClick: sb isShown");
                }

                View snackView = sb.getView();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackView.getLayoutParams();
                layoutParams.gravity = Gravity.TOP | Gravity.CENTER_VERTICAL;
                layoutParams.setMargins(0, topInset, 0, 0); // Set top margin based on status bar height
                snackView.setLayoutParams(layoutParams);


                sb.show();


                snackView.setOnTouchListener(new MyOnTouchListener(snackView, sb));
            }
        });

    }



    // Main uI
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        gestureDetector.onTouchEvent(event);
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                x1 = event.getX();
//                y1 = event.getY();
//                break;
//
//            case MotionEvent.ACTION_UP:
//                x2 = event.getX();
//                y2 = event.getY();
//
//
//                float valueX = x2 - x1;
//
//                float valueY = y2 - y1;
//
//                if (Math.abs(valueX) > MIN_DISTANCE) {
//                    if (x1>x2) {
//                        Toast.makeText(this, "L2R", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(this, "R2L", Toast.LENGTH_SHORT).show();
//                    }
//                } else if (Math.abs(valueY) > MIN_DISTANCE) {
//                    if (y2>y1) {
//                        Toast.makeText(this, "T2B", Toast.LENGTH_SHORT).show();
//                    } else  {
//                        Toast.makeText(this, "B2T", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        }
//
//        return super.onTouchEvent(event);
//    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    private class MyOnTouchListener implements View.OnTouchListener {
        private final View snackView;
        private final Snackbar sb;
        private float y1, y2;
        private boolean isSwiping;

        public MyOnTouchListener(View snackView, Snackbar sb) {
            this.snackView = snackView;
            this.sb = sb;
            isSwiping = false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            gestureDetector.onTouchEvent(event);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
//                                x1 = event.getX();
                    y1 = event.getRawY();
                    isSwiping = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    y2 = event.getRawY();
                    float deltaY = y2 - y1;

                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        isSwiping = true;
                    }

                    if (isSwiping) {
                        snackView.setTranslationY(deltaY);
                    }

                case MotionEvent.ACTION_UP:
                    if (isSwiping) {
                        y2 = event.getRawY();
                        float valueY = y2 - y1;

                        if (Math.abs(valueY) > MIN_DISTANCE) {
                            if (valueY > 0) {
                                // Swipe down -> Dismiss Snackbar with downward animation
                                snackView.animate().translationY(snackView.getHeight()).setDuration(300)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                sb.dismiss(); // Dismiss Snackbar
                                                isSwiping = false; // Reset swipe flag
                                                Log.d(TAG, "run: swipe down");
                                            }
                                        }).start();
                            } else {
                                // Swipe up -> Dismiss Snackbar with upward animation
                                snackView.animate().translationY(-snackView.getHeight()).setDuration(300)
                                        .withEndAction(new Runnable() {
                                            @Override
                                            public void run() {
                                                sb.dismiss(); // Dismiss Snackbar
                                                isSwiping = false; // Reset swipe flag
                                                Log.d(TAG, "run: swipe up");
                                            }
                                        }).start();
                            }
                        } else {
                            snackView.animate().translationY(0).setDuration(300)
                                    .start();
                        }
                    }
                    break;
            }
            return true; // Consume the touch event
        }
    }
}