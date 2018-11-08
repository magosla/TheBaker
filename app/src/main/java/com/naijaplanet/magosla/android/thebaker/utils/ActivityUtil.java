package com.naijaplanet.magosla.android.thebaker.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.view.Window;

import com.naijaplanet.magosla.android.thebaker.R;

import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public class ActivityUtil {
    // if activity change transition has been enabled
    private static boolean mTransitionEnabled;
    // it a call the enable activity transition has been made
    private static boolean mCalledTransitionEnable;


// --Commented out by Inspection START (09/09/2018 16:10):
//    /**
//     * Launches an activity
//     * @param activity the current activity
//     * @param cls the Destination activity class
//     * @param extraBundles extra {@link Bundle} to send
//     */
//    public static void lunchActivity(Activity activity, Class<?> cls, @Nullable Bundle extraBundles){
//        Intent intent = new Intent(activity, cls);
//        if(extraBundles != null){
//            intent.putExtras(extraBundles);
//        }
//            activity.startActivity(intent);
//    }
// --Commented out by Inspection STOP (09/09/2018 16:10)
    /**
     * Launches an activity with transitions
     * @param activity the current activity
     * @param cls the Destination activity class
     * @param extraBundles extra {@link Bundle} to send
     */
    public static void lunchActivityWithTransition(Activity activity, Class<?> cls, @Nullable Bundle extraBundles){
        Intent intent = new Intent(activity, cls);
        if(extraBundles != null){
            intent.putExtras(extraBundles);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startActivity(intent, getTransitionsBundle(activity));
        }else{
            activity.startActivity(intent);
        }
    }


    /**
     * Tries to get the transition bundle that would be used to animate
     * Activity change
     *
     * @param activity the current activity
     * @return the bundle if available
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Bundle getTransitionsBundle(Activity activity) {
        Bundle bundle = null;
        // if enableTransition has been called and the device SDK support transition
        if (mCalledTransitionEnable && mTransitionEnabled) {
            bundle = ActivityOptions.makeSceneTransitionAnimation(activity).toBundle();
        }
        return bundle;
    }

    /**
     * Enables activity transition if supported by the SDK version
     *
     * @param activity the current activity
     */
    public static void enableTransition(Activity activity) {
        if (!mCalledTransitionEnable) {
            mCalledTransitionEnable = true;
            // Check if we're running on Android 5.0 or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Apply activity transition
                // inside your activity (if you did not enable transitions in your theme)
                activity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
                // set an exit transition
                activity.getWindow().setExitTransition(new Explode());
                mTransitionEnabled = true;

            } else {
                // Swap without transition
                mTransitionEnabled = false;
            }
        }

    }

    public static class Dimensions {
        public int height;
        public int width;
        Dimensions(){}
    }

    /**
     * Gets the screen Dimension
     * @param activity the current {@link Activity} instance
     * @return {@link Dimensions} the dimension of the screen
     */
    public static Dimensions getScreenDimension(Activity activity){
        Dimensions dimensions = new Dimensions();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        dimensions.height = displayMetrics.heightPixels;
        dimensions.width = displayMetrics.widthPixels;

        return dimensions;
    }

    public static boolean isLandScapeView(Context context) {
        return context.getResources().getBoolean(R.bool.landscape_orientation);
    }

    public static boolean isTabletDevice(Context context) {
        return context.getResources().getBoolean(R.bool.tablet_device);
    }
}
