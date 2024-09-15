package com.drc.mytopsnacklibrary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

public class TopSnack2 implements GestureDetector.OnGestureListener {

    private Context context;
    private View customLayout;
    private int animationDuration;
    private int displayLength;
    private boolean hasNotificationSound;

    private boolean isSwipeable;



    private static final Integer DefaultAnimationDuration_500ms = 500;
    private static final Integer DefaultDisplayLength_5000ms = 5000;

    private static Snackbar snackbar;
    private static View snackBarView;

    private static final Handler handler = new Handler();
    private static Runnable runnable;

    private static boolean isAnimating = false;
    private static boolean isOnHold = false;

    private static int duration;
    private static long durationSlideUp;
    private static long durationSlideDown;

    public TopSnack2(Context context,  @NonNull View activityLayout, @NonNull View customLayout, @Nullable Integer animationDuration, @Nullable Integer displayLength, boolean hasNotificationSound) {
        this.context = context;
        this.customLayout = customLayout;
        this.animationDuration = animationDuration;
        this.displayLength = displayLength;
        this.hasNotificationSound = hasNotificationSound;

        int topInset = removeStatusBar(context);

        if (animationDuration == null) {
            animationDuration = DefaultAnimationDuration_500ms;
        }
        if (displayLength == null) {
            displayLength = DefaultDisplayLength_5000ms;
        }

        if (customLayout.getParent() != null) {
            ((ViewGroup) customLayout.getParent()).removeView(customLayout);
        }

        snackbar = Snackbar.make(activityLayout, "", displayLength);
        handler.removeCallbacks(runnable);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.addView(customLayout, 0);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        // Custom Layout
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        layoutParams.setMargins(0, topInset, 0, 0);
        customLayout.setLayoutParams(layoutParams);

        // Directly move the snackbar to the top of its parent
        FrameLayout.LayoutParams snackbarLayoutParams = (FrameLayout.LayoutParams) snackbarLayout.getLayoutParams();
        snackbarLayoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        snackbarLayout.setLayoutParams(snackbarLayoutParams);

        snackBarView = snackbar.getView();

        Animation slideDown = getSlideDown(animationDuration, topInset);
        snackBarView.startAnimation(slideDown);
        durationSlideDown = slideDown.getDuration();

        snackBarView.setOnTouchListener(new TopSnack2.MyOnTouchListener(snackBarView, snackbar));

        if (hasNotificationSound){
            final MediaPlayer mp = MediaPlayer.create(context, R.raw.message_notification_190034);
            mp.start();
        }

        snackbar.show();
        autoHide(context, displayLength);

    }

    public TopSnack2(Context context,  @NonNull View activityLayout, @NonNull View customLayout, @Nullable Integer animationDuration, @Nullable Integer displayLength, boolean hasNotificationSound, boolean isSwipeable) {
        this.context = context;
        this.customLayout = customLayout;
        this.animationDuration = animationDuration;
        this.displayLength = displayLength;
        this.hasNotificationSound = hasNotificationSound;
        this.isSwipeable = isSwipeable;

        int topInset = removeStatusBar(context);

        if (animationDuration == null) {
            animationDuration = DefaultAnimationDuration_500ms;
        }
        if (displayLength == null) {
            displayLength = DefaultDisplayLength_5000ms;
        }

        if (customLayout.getParent() != null) {
            ((ViewGroup) customLayout.getParent()).removeView(customLayout);
        }

        snackbar = Snackbar.make(activityLayout, "", displayLength);
        handler.removeCallbacks(runnable);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.addView(customLayout, 0);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);

        // Custom Layout
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        layoutParams.setMargins(0, topInset, 0, 0);
        customLayout.setLayoutParams(layoutParams);

        // Directly move the snackbar to the top of its parent
        FrameLayout.LayoutParams snackbarLayoutParams = (FrameLayout.LayoutParams) snackbarLayout.getLayoutParams();
        snackbarLayoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        snackbarLayout.setLayoutParams(snackbarLayoutParams);

        snackBarView = snackbar.getView();

        Animation slideDown = getSlideDown(animationDuration, topInset);
        snackBarView.startAnimation(slideDown);
        durationSlideDown = slideDown.getDuration();

        snackBarView.setOnTouchListener(new TopSnack2.MyOnTouchListener(snackBarView, snackbar));

        if (hasNotificationSound){
            final MediaPlayer mp = MediaPlayer.create(context, R.raw.message_notification_190034);
            mp.start();
        }

        snackbar.show();
        autoHide(context, displayLength);

    }


    private static void autoHide(Context context, int displayLength) {

//        Log.d("TAG", "autoHide: called success");

        int topInset = removeStatusBar(context);

        Animation slideUp = getSlideUp(DefaultAnimationDuration_500ms, topInset);

        if (!isOnHold) {
            runnable = new Runnable() {
                @Override
                public void run() {

                    Log.d("Swipe", "run:onHold " + isOnHold);

                    if (snackbar.isShown() || snackBarView.getVisibility() == View.VISIBLE) {
                        snackBarView.startAnimation(slideUp);

                        // After animation, dismiss
                        snackBarView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // After 500 milliseconds, run this following code
                                snackbar.dismiss();

                                Log.d("TAG", "dismissed? " + snackbar.isShown());
                            }
                        }, slideUp.getDuration());
                    }
                }
            };
        }
        handler.postDelayed(runnable, displayLength);
        duration = displayLength;
        durationSlideUp = slideUp.getDuration();
    }


    public static int removeStatusBar(Context context) {
        int topInset;
        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            topInset = resources.getDimensionPixelSize(resourceId);
        } else{
            topInset = (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
        }
        return topInset;
    }

    private static @NonNull Animation getSlideUp(@NonNull Integer animationDuration, int topInset) {
        Animation slideUp = new TranslateAnimation(
                0, 0, 0, -(topInset * 3)
        );
        slideUp.setDuration(animationDuration);
        slideUp.setFillAfter(true);
        isAnimating = true;
        return slideUp;
    }

    private static @NonNull Animation getSlideDown(@NonNull Integer animationDuration, int topInset) {
        Animation slideDown = new TranslateAnimation(
                0, 0, -(topInset * 3), 0
        );
        slideDown.setDuration(animationDuration); // Duration in milliseconds
        slideDown.setFillAfter(true);
        isAnimating = true;
        return slideDown;
    }

    private static class MyOnTouchListener implements View.OnTouchListener {
        private final View snackView;
        private final Snackbar sb;
        private float y1, y2;
        private boolean isSwiping; // Make this global


        private static final int MIN_DISTANCE = 75;

        public MyOnTouchListener(View snackView, Snackbar sb) {
            this.snackView = snackView;
            this.sb = sb;
            isSwiping = false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    y1 = event.getRawY();
                    isSwiping = false;
                    break;

                case MotionEvent.ACTION_MOVE:
                    y2 = event.getRawY();
                    float deltaY = y2 - y1;

                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        isSwiping = true;
                        isOnHold = true;
                        Log.d("Swipe", "run: hold" + isSwiping);
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
//                                                sb.dismiss(); // Dismiss Snackbar
                                                isSwiping = false; // Reset swipe flag
                                                Log.d("Swipe", "run: swipe down " + isSwiping);
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
                                                Log.d("Swipe", "run: swipe up " + isSwiping);
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
}
