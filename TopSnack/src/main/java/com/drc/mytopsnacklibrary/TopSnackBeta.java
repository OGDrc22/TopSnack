package com.drc.mytopsnacklibrary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.Nullable;

public class TopSnackBeta {

    private static final Integer DefaultAnimationDuration_500ms = 500;
    private static final Integer DefaultDisplayLength_5000ms = 5000;

    private static Snackbar snackbar;
    private static View view1;

    private static final Handler handler = new Handler();
    private static Runnable runnable;

    private static boolean isAnimating = false;

    public static void defaultTopSnack(Context context, @NonNull View activityLayout, @Nullable String action, @Nullable Integer animationDuration, @Nullable Integer displayLength, boolean hasNotificationSound) {

        int topInset;

        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            topInset = resources.getDimensionPixelSize(resourceId);
        } else{
            topInset = (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
        }

        if (animationDuration == null) {
            animationDuration = 300;
        }
        if (displayLength == null) {
            displayLength = 3000;
        }

        Animation slideDown = getSlideDown(animationDuration, topInset);


        snackbar = Snackbar.make(activityLayout, "Sample", displayLength);
        view1 = snackbar.getView();
        if (action != null) {
            snackbar.setAction(action, v -> hideTopSnack(context));
        }


        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view1.getLayoutParams();
        layoutParams.gravity = Gravity.TOP | Gravity.CENTER_VERTICAL;
        layoutParams.setMargins(0, topInset, 0, 0); // Set top margin based on status bar height
        view1.setLayoutParams(layoutParams);

        view1.startAnimation(slideDown);

        final MediaPlayer mp = MediaPlayer.create(context, R.raw.message_notification_190034);
        mp.start();

        snackbar.show();
        autoHide(context, displayLength);
    }

    public static void createCustomTopSnack(Context context, @NonNull View activityLayout, @NonNull View customLayout, CardView cardView, @Nullable Integer animationDuration, @Nullable Integer displayLength, boolean hasNotificationSound) {

//        View overActivity;
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        overActivity = (CoordinatorLayout) inflater.inflate(R.layout.coords, null);

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
//        FrameLayout.LayoutParams snackbarLayoutParams = (FrameLayout.LayoutParams) snackbarLayout.getLayoutParams();
//        snackbarLayoutParams.gravity = Gravity.TOP | Gravity.CENTER;
//        snackbarLayout.setLayoutParams(snackbarLayoutParams);
        CoordinatorLayout.LayoutParams snackbarLayoutParams = (CoordinatorLayout.LayoutParams) snackbarLayout.getLayoutParams();
        snackbarLayoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        snackbarLayout.setLayoutParams(snackbarLayoutParams);

        view1 = snackbar.getView();

        Animation slideDown = getSlideDown(animationDuration, topInset);
        view1.startAnimation(slideDown);

        final MediaPlayer mp = MediaPlayer.create(context, R.raw.message_notification_190034);
        mp.start();

        snackbar.show();
        autoHide(context, displayLength);

    }

    private static void autoHide(Context context, int displayLength) {

//        Log.d("TAG", "autoHide: called success");

        int topInset = removeStatusBar(context);

        Animation slideUp = getSlideUp(DefaultAnimationDuration_500ms, topInset);

        runnable = new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "after: 4sec ");
                // After 4 seconds, run this following code

                if (snackbar.isShown() || view1.getVisibility() == View.VISIBLE){
                    view1.startAnimation(slideUp);

                    // After animation, dismiss
                    view1.postDelayed(new Runnable() {
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
        handler.postDelayed(runnable, displayLength);
    }

    public static void hideTopSnack(@NonNull Context context) {

        int topInset = removeStatusBar(context);
        handler.removeCallbacks(runnable);

        Animation slideUp = getSlideUp(DefaultAnimationDuration_500ms, topInset);

        view1.startAnimation(slideUp);

        if (!isAnimating) {
            // Delay actual SnackBar dismissal until after the slide-up animation
            view1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view1.setVisibility(View.GONE);
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
}


