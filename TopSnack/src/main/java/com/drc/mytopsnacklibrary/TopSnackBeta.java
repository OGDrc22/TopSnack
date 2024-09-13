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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.Nullable;

public class TopSnackBeta implements GestureDetector.OnGestureListener {

    private static final Integer DefaultAnimationDuration_500ms = 500;
    private static final Integer DefaultDisplayLength_5000ms = 5000;

    private static Snackbar snackbar;
    private static View snackBarView;

    private static final Handler handler = new Handler();
    private static Runnable runnableAutoHide;

    private static boolean isAnimating = false;
    private static boolean isOnHold = false;

    private static int duration;
    private static long durationSlideUp;
    private static long durationSlideDown;

    public static void defaultTopSnack(Context context, @NonNull View activityLayout, @Nullable String message, @Nullable String action, @Nullable Integer animationDuration, @Nullable Integer displayLength, boolean hasNotificationSound, @NonNull String swipeDirection) {

        int topInset;

        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            topInset = resources.getDimensionPixelSize(resourceId);
        } else{
            topInset = (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
        }

        if (animationDuration == null) {
            animationDuration = DefaultAnimationDuration_500ms;
        }
        if (displayLength == null) {
            displayLength = DefaultDisplayLength_5000ms;
        }

        Animation slideDown = getSlideDown(animationDuration, topInset);
        durationSlideDown = slideDown.getDuration();


        snackbar = Snackbar.make(activityLayout, message, displayLength);
        snackBarView = snackbar.getView();
        if (action != null) {
            snackbar.setAction(action, v -> hideTopSnack(context));
        }


        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();
//      layoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        layoutParams.setMargins(0, topInset, 0, 0);
        if (swipeDirection == null) {
            layoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        } else if (swipeDirection.equalsIgnoreCase("down")) {
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
            layoutParams.setMargins(0, 0, 0, topInset);
        } else if (swipeDirection.equalsIgnoreCase("up")) {
            layoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        }
        snackBarView.setLayoutParams(layoutParams);

        snackBarView.startAnimation(slideDown);

        snackBarView.setOnTouchListener(new MyOnTouchListener(snackBarView, snackbar, swipeDirection));

        if (hasNotificationSound) {
            final MediaPlayer mp = MediaPlayer.create(context, R.raw.message_notification_190034);
            mp.start();
        }

        snackbar.show();
        autoHide(context, displayLength, swipeDirection);
    }



    public static void createCustomTopSnack(Context context, @NonNull View activityLayout, @NonNull View customLayout, @Nullable Integer animationDuration, @Nullable Integer displayLength, boolean hasNotificationSound, @NonNull String swipeDirection) {

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

        snackbar = Snackbar.make(activityLayout, "", Snackbar.LENGTH_INDEFINITE);
        handler.removeCallbacks(runnableAutoHide);
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
//        snackbarLayoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        if (swipeDirection == null) {
            snackbarLayoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        } else if (swipeDirection.equalsIgnoreCase("down")) {
            snackbarLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
            layoutParams.setMargins(0, 0, 0, topInset);
        } else if (swipeDirection.equalsIgnoreCase("up")) {
            snackbarLayoutParams.gravity = Gravity.TOP | Gravity.CENTER;
            layoutParams.setMargins(0, topInset, 0, 0);
        }
        snackbarLayout.setLayoutParams(snackbarLayoutParams);

        snackBarView = snackbar.getView();

        Animation slideDown = getSlideDown(animationDuration, topInset);
        snackBarView.startAnimation(slideDown);
        durationSlideDown = slideDown.getDuration();



        if (hasNotificationSound){
            final MediaPlayer mp = MediaPlayer.create(context, R.raw.message_notification_190034);
            mp.start();
        }

        snackbar.show();


        autoHide(context, displayLength, swipeDirection);
        snackBarView.setOnTouchListener(new MyOnTouchListener(snackBarView, snackbar, swipeDirection));

    }

    private static void autoHide(Context context, int displayLength, String direction) {

        Log.d("TAG", "autoHide: called success");

        int topInset = removeStatusBar(context);

        Animation slideUp = getSlideUp(DefaultAnimationDuration_500ms, topInset);
        Animation slideDown = getSlideDown(DefaultAnimationDuration_500ms, topInset);
        Animation fade = getFadeAnim(DefaultAnimationDuration_500ms, topInset);
//        isOnHold = true;


        if (!isOnHold) {
            runnableAutoHide = new Runnable() {
                @Override
                public void run() {

                    Log.d("Swipe", "run:onHold " + isOnHold);

                    if (snackbar.isShown() || snackBarView.getVisibility() == View.VISIBLE) {
                        if (direction == null) {
                            snackBarView.startAnimation(fade);
                        } else if (direction.equalsIgnoreCase("up")) {
                            snackBarView.startAnimation(slideUp);
                        } else if (direction.equalsIgnoreCase("down")) {
                            snackBarView.startAnimation(slideDown);
                        }

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

        handler.removeCallbacks(runnableAutoHide);
        handler.postDelayed(runnableAutoHide, displayLength);
        duration = displayLength;
        durationSlideUp = slideUp.getDuration();
    }

    public static void hideTopSnack(@NonNull Context context) {

        int topInset = removeStatusBar(context);
        handler.removeCallbacks(runnableAutoHide);

        Animation slideUp = getSlideUp(DefaultAnimationDuration_500ms, topInset);

        snackBarView.startAnimation(slideUp);

        if (!isAnimating) {
            // Delay actual SnackBar dismissal until after the slide-up animation
            snackBarView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    snackBarView.setVisibility(View.GONE);
                    snackbar.dismiss();
                }
            }, slideUp.getDuration());
        }

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


    public static int getDuration() {
        return (int) (durationSlideDown + duration + durationSlideUp);
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

    private static @NonNull Animation getFadeAnim(@NonNull Integer animationDuration, int topInset) {
        Animation fade = new AlphaAnimation(100, 0);
        isAnimating = true;
        return fade;
    }


    private static class MyOnTouchListener implements View.OnTouchListener {
        private final View snackView;
        private final Snackbar sb;
        private float y1, y2;
        private boolean isSwiping; // Make this global

        private String swipeDirection;


        private static final int MIN_DISTANCE = 75;

        public MyOnTouchListener(View snackView, Snackbar sb, @NonNull String swipeDirection) {
            this.snackView = snackView;
            this.sb = sb;
            isSwiping = false;
            this.swipeDirection = swipeDirection;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    y1 = event.getRawY();
                    isSwiping = false;
                    isOnHold = true;
                    handler.removeCallbacks(runnableAutoHide);
                    break;

                case MotionEvent.ACTION_MOVE:
                    y2 = event.getRawY();
                    float deltaY = y2 - y1;
//                    snackView.setTranslationY(deltaY);

                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        isSwiping = true;
                        Log.d("Swipe", "run: hold " + isSwiping);
                    }


                case MotionEvent.ACTION_UP:
                    isOnHold = false;
//                    autoHide(sb.getContext(), 5000);
                    if (isSwiping) {
                        y2 = event.getRawY();
                        float valueY = y2 - y1;

                            if (swipeDirection == null) {
                                restartAutoHide();
                            } else if (swipeDirection.equalsIgnoreCase("down")) {
                                // Swipe down -> Dismiss Snackbar with downward animation
                                if (valueY > 0) {
                                        snackView.setTranslationY(valueY);
                                        restartAutoHide();

                                    if (Math.abs(valueY) > MIN_DISTANCE) {
                                        snackView.animate().translationY(snackView.getHeight()).setDuration(300)
                                                .withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        sb.dismiss(); // Dismiss Snackbar
                                                        isSwiping = false; // Reset swipe flag
                                                        Log.d("Swipe", "run: swipe down " + isSwiping);
                                                        handler.removeCallbacks(runnableAutoHide);
                                                    }
                                                }).start();
                                    }
                                }
                            } else if (swipeDirection.equalsIgnoreCase("up")) {
                                // Swipe up -> Dismiss Snackbar with upward animation
                                if (valueY < 0) {
                                        snackView.setTranslationY(valueY);
                                        restartAutoHide();

                                    if (Math.abs(valueY) > MIN_DISTANCE) {
                                        snackView.animate().translationY(-snackView.getHeight()).setDuration(300)
                                                .withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        sb.dismiss(); // Dismiss Snackbar
                                                        isSwiping = false; // Reset swipe flag
                                                        Log.d("Swipe", "run: swipe up " + isSwiping);
                                                        handler.removeCallbacks(runnableAutoHide);
                                                    }
                                                }).start();
                                    }
                                }
                            } else if (swipeDirection.equalsIgnoreCase("both")) {
                                if (isSwiping) {
                                    snackView.setTranslationY(valueY);
                                }
                                if (valueY > 0) {
                                    // Swipe down -> Dismiss Snackbar with downward animation
                                    snackView.animate().translationY(snackView.getHeight()).setDuration(300)
                                            .withEndAction(new Runnable() {
                                                @Override
                                                public void run() {
                                                    sb.dismiss(); // Dismiss Snackbar
                                                    isSwiping = false; // Reset swipe flag
                                                    Log.d("Swipe", "run: swipe down " + isSwiping);
                                                    handler.removeCallbacks(runnableAutoHide);
                                                }
                                            }).start();
                                } else {
                                    snackView.animate().translationY(-snackView.getHeight()).setDuration(300)
                                            .withEndAction(new Runnable() {
                                                @Override
                                                public void run() {
                                                    sb.dismiss(); // Dismiss Snackbar
                                                    isSwiping = false; // Reset swipe flag
                                                    Log.d("Swipe", "run: swipe up " + isSwiping);
                                                    handler.removeCallbacks(runnableAutoHide);
                                                }
                                            }).start();
                                }
                            } else {
                                Log.d("TAG", "onTouch: Error: wrong key " + swipeDirection);
                            }

//                        } else {
//                            snackView.animate().translationY(0).setDuration(300)
//                                    .start();
//                            restartAutoHide();
//                        }
                    }
                    break;
            }
            return true; // Consume the touch event
        }


        private void restartAutoHide() {
//            if (isOnHold && snackbar.isShown()) {
                handler.postDelayed(runnableAutoHide, duration);
                Log.d("Swipe", "Auto-hide restarted");
//            }
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
    public boolean onScroll(@androidx.annotation.Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {

    }

    @Override
    public boolean onFling(@androidx.annotation.Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

}

