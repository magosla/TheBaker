package com.naijaplanet.magosla.android.thebaker;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.naijaplanet.magosla.android.thebaker.ui.widgets.TheBakerWidget;

public class Config {
    public static final String BUNDLE_RECIPE_ID = "bundle_recipe_id";
    public static final String BUNDLE_RECIPE_STEP = "bundle_recipe_step";
    public static final String BUNDLE_RECIPE_STEP_NO = "bundle_recipe_step_no";
    private static final String PREF_RECIPE_ID = "pref_recipe_id";



    public static final String RECIPE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";


    /**
     *  Saves the users favorite recipe
     * @param context the context
     * @param recipe_id the selected {@link com.naijaplanet.magosla.android.thebaker.data.models.Recipe} id
     */
    public static void saveUsersRecipe(Context context, int recipe_id){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_RECIPE_ID, recipe_id)
                .apply();
        notifyWidgetUpdate(context);
    }

    /**
     * Clears the user data from the saved preference
     * @param context the application context
     */
    public static void clearUserRecipe(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().clear().apply();
        notifyWidgetUpdate(context);
    }

    /**
     * If the recipe is what the user selected
     * @param context the application context
     * @param recipe_id the id of the recipe
     * @return boolean if it is the users recipe
     */
    public static boolean isUserRecipe(Context context, int recipe_id){
        int u_recipe_id=0;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(pref!=null){
            u_recipe_id = pref.getInt(PREF_RECIPE_ID, 0);
        }
        return recipe_id!=0 && u_recipe_id == recipe_id;
    }

    /**
     * Gets the users recipe if any
     * @param context the application context
     * @return the user object
     */
    public static int getUserRecipe(Context context){
        int recipe_id=0;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

            if (pref != null) {
                recipe_id = pref.getInt(PREF_RECIPE_ID, 0);
            }

        return recipe_id;
    }

    public static void notifyWidgetUpdate(Context context){
        // Update the Widgets
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, TheBakerWidget.class));
        //Trigger data update to handle the ListView widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.recipe_list_view);
    }

}
