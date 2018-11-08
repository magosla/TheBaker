package com.naijaplanet.magosla.android.thebaker.ui.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.naijaplanet.magosla.android.thebaker.R;
import com.naijaplanet.magosla.android.thebaker.ui.activities.RecipeActivity;

/**
 * Implementation of App Widget functionality.
 */
public class TheBakerWidget extends AppWidgetProvider {

    //private static boolean mIsLargeWidget = true;

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mIsLargeWidget = isLargeWidget(
                    AppWidgetManager.getInstance(context).getAppWidgetOptions(appWidgetId).getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
                   // AppWidgetManager.getInstance(context).getAppWidgetInfo(appWidgetId).minHeight
                    );
        }else{
            mIsLargeWidget = true;
        }
        */

        RemoteViews views = buildLayout(context, appWidgetId);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static RemoteViews buildLayout(Context context, int appWidgetId){
        RemoteViews views;
       // if(mIsLargeWidget) {
            views = getLargeLayout(context, appWidgetId);
        //}else{
          //  views = getSmallLayout(context);
        //}
        return views;
    }

    private static RemoteViews getLargeLayout(Context context, int appWidgetId) {
        RemoteViews views;
        views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_layout);

        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent serviceIntent = new Intent(context, RecipeListWidgetService.class);
        views.setRemoteAdapter(appWidgetId, R.id.recipe_list_view, serviceIntent);

        // Set the PlantDetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, RecipeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.recipe_list_view, pendingIntent);
        // Handle empty gardens
        //views.setEmptyView(R.id.recipe_list_view, R.id.empty_view);
        return views;
    }

    /*
    private static RemoteViews getSmallLayout(Context context) {
        RemoteViews views;
        CharSequence widgetEmptyText = context.getString(R.string.msg_no_recipe);
        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_layout_small);

        // Update the header to reflect the weather for "today"
        Cursor c = context.getContentResolver().query(RecipeProvider.Recipes.CONTENT_URI, null,
                null, null, null);

        if (c != null && c.moveToPosition(0)) {
            long recipeId = Cursors.getLong(c, RecipesColumn.ID);
            String recipeName = Cursors.getString(c, RecipesColumn.NAME);
            String iconLetter = String.valueOf(recipeName.charAt(0));

            views.setTextViewText(R.id.tv_name, recipeName);
            views.setTextViewText(R.id.tv_icon, iconLetter);

            Intent intent = new Intent(context, RecipeActivity.class);
            intent.putExtra(Config.BUNDLE_RECIPE_ID, recipeId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.item_layout, pendingIntent);

        }else{
            views.setTextViewText(R.id.tv_icon, widgetEmptyText);
        }
        return views;
    }
    */

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        /*
        mIsLargeWidget = true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            mIsLargeWidget = isLargeWidget(minHeight);
        }
         */
        RemoteViews layout;
        layout = buildLayout(context, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, layout);
    }

    /*
    private static boolean isLargeWidget(int height){

        return height > 100;
    }
    */

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

}

