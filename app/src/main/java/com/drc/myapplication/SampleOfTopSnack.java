package com.drc.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.Nullable;

public class SampleOfTopSnack {

    private static Integer finalAnimationDuration = 500;
    private static Snackbar snackbarC;
    private static View view1;
    private static boolean isAnimating = false;
    private static boolean isSnackbarShowing = false;

    public static void showCustomTopSnack(Context context, @NonNull View activityLayout, @NonNull View customLayout, @Nullable Integer animationDuration, @Nullable Integer displayLength) {

        if (isAnimating || isSnackbarShowing) {
            return;  // Prevent showing a new Snackbar if one is already animating or showing
        }

        isSnackbarShowing = true;

        int topInset = removeStatusBar(context);

        if (animationDuration == null) {
            animationDuration = finalAnimationDuration;
        }
        if (displayLength == null) {
            displayLength = 3000;
        }

        Animation slideDown = getSlideDown(animationDuration, topInset);
        Animation slideUp = getSlideUp(animationDuration, topInset);

        snackbarC = Snackbar.make(activityLayout, "", Snackbar.LENGTH_INDEFINITE);
        snackbarC.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbarC.getView();
        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.addView(customLayout, 0);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        layoutParams.setMargins(0, topInset, 0, 0);
        customLayout.setLayoutParams(layoutParams);

        FrameLayout.LayoutParams snackbarLayoutParams = (FrameLayout.LayoutParams) snackbarLayout.getLayoutParams();
        snackbarLayoutParams.gravity = Gravity.TOP | Gravity.CENTER;
//        snackbarLayoutParams.setMargins(0, topInset, 0, 0);
        snackbarLayout.setLayoutParams(snackbarLayoutParams);

        view1 = snackbarC.getView();
        view1.startAnimation(slideDown);
        isAnimating = true;

        snackbarC.show();

        finalAnimationDuration = animationDuration;
        Integer finalDisplayLength = displayLength;
        view1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (view1.getVisibility() == View.VISIBLE) {
                    autoHideTopSnack(context, finalDisplayLength, finalAnimationDuration);
                }
            }
        }, displayLength);
    }

    public static void hideTopSnackManually(Context context) {
        if (isSnackbarShowing) {
            hideTopSnack(context, 0, finalAnimationDuration);
        }
    }

    private static void autoHideTopSnack(@NonNull Context context, @Nullable Integer displayLength, @Nullable Integer animationDuration) {
        if (animationDuration == null) {
            animationDuration = finalAnimationDuration;
        }

        if (view1 != null && view1.getVisibility() == View.VISIBLE) {
            hideTopSnack(context, displayLength, animationDuration);
        }
    }

    public static void hideTopSnack(@NonNull Context context, @Nullable Integer displayLength, @Nullable Integer animationDuration) {

        if (animationDuration == null) {
            animationDuration = finalAnimationDuration;
        }

        int topInset = removeStatusBar(context);
        Animation slideUp = getSlideUp(animationDuration, topInset);

        if (displayLength == null) {
            displayLength = 0;
        }

        view1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (view1 != null && view1.getVisibility() == View.VISIBLE) {
                    view1.startAnimation(slideUp);
                    view1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (snackbarC != null) {
                                snackbarC.dismiss();
                                isAnimating = false;
                                isSnackbarShowing = false; // Reset the state after animation ends
                            }
                        }
                    }, finalAnimationDuration);
                }
            }
        }, displayLength);
    }


    public static @NonNull Animation getSlideUp(@NonNull Integer animationDuration, int topInset) {
        Animation slideUp = new TranslateAnimation(
                0, 0, 0, -(topInset *3)
        );
        slideUp.setDuration(animationDuration);
        slideUp.setFillAfter(true);
        return slideUp;
    }

    public static @NonNull Animation getSlideDown(@NonNull Integer animationDuration, int topInset) {
        Animation slideDown = new TranslateAnimation(
                0, 0, -(topInset *3), 0
        );
        slideDown.setDuration(animationDuration); // Duration in milliseconds
        slideDown.setFillAfter(true);
        return slideDown;
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

}

